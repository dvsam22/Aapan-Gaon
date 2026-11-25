package com.dv.apna.core.common

import android.graphics.Bitmap
import android.util.Base64
import com.dv.apna.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Shared Gemini JSON helper that calls the v1beta REST endpoint directly with
 * minimal thinking config to cut tail latency, and caches the working model in memory.
 */
object GeminiContentHelper {

    @Volatile
    private var cachedWorkingModel: String? = null

    private val defaultModels = listOf(
        "gemini-3.5-flash-lite",
        "gemini-3-flash-preview",
        "gemini-3.8-flash"
    )

    suspend fun generateJson(
        promptText: String,
        bitmap: Bitmap? = null,
        temperature: Float,
        maxOutputTokens: Int,
        fallbackErrorMessage: String,
        logTag: String = "GeminiAI"
    ): String {
        val orderedModels = buildList {
            cachedWorkingModel?.let { add(it) }
            defaultModels.forEach { if (it != cachedWorkingModel) add(it) }
        }

        val encodedJpeg = bitmap?.let { encodeBitmapToBase64(it) }
        var lastException: Exception? = null

        for (modelName in orderedModels) {
            val startedAt = System.currentTimeMillis()
            try {
                val text = callRestGenerateContent(
                    modelName = modelName,
                    promptText = promptText,
                    base64Jpeg = encodedJpeg,
                    temperature = temperature,
                    maxOutputTokens = maxOutputTokens
                )
                if (text.isNotBlank()) {
                    cachedWorkingModel = modelName
                    val elapsedMs = System.currentTimeMillis() - startedAt
                    android.util.Log.d(logTag, "REST model $modelName succeeded in ${elapsedMs}ms")
                    return text
                }
            } catch (e: Exception) {
                val elapsedMs = System.currentTimeMillis() - startedAt
                if (modelName == cachedWorkingModel) {
                    cachedWorkingModel = null
                }
                android.util.Log.d(logTag, "REST model $modelName failed in ${elapsedMs}ms: ${e.message}")
                lastException = e
            }
        }

        // SDK fallback if REST attempts failed
        val sdkModels = listOf("gemini-3.5-flash-lite", "gemini-3-flash-preview", "gemini-3.8-flash")
        val config = generationConfig {
            this.temperature = temperature
            topP = 0.95f
            this.maxOutputTokens = maxOutputTokens
            responseMimeType = "application/json"
        }
        val sdkContent = content {
            if (bitmap != null) image(bitmap)
            text(promptText)
        }
        for (modelName in sdkModels) {
            val startedAt = System.currentTimeMillis()
            try {
                val model = GenerativeModel(
                    modelName = modelName,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    generationConfig = config
                )
                val text = model.generateContent(sdkContent).text?.trim()
                if (!text.isNullOrBlank()) {
                    val elapsedMs = System.currentTimeMillis() - startedAt
                    android.util.Log.d(logTag, "SDK fallback $modelName succeeded in ${elapsedMs}ms")
                    return text
                }
            } catch (e: Exception) {
                val elapsedMs = System.currentTimeMillis() - startedAt
                android.util.Log.d(logTag, "SDK fallback $modelName failed in ${elapsedMs}ms: ${e.message}")
                lastException = e
            }
        }

        throw lastException ?: Exception(fallbackErrorMessage)
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 72, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    private fun callRestGenerateContent(
        modelName: String,
        promptText: String,
        base64Jpeg: String?,
        temperature: Float,
        maxOutputTokens: Int
    ): String {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 8_000
            readTimeout = 15_000
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("x-goog-api-key", BuildConfig.GEMINI_API_KEY)
        }

        try {
            val parts = JSONArray()
            if (!base64Jpeg.isNullOrEmpty()) {
                parts.put(
                    JSONObject().put(
                        "inlineData",
                        JSONObject()
                            .put("mimeType", "image/jpeg")
                            .put("data", base64Jpeg)
                    )
                )
            }
            parts.put(JSONObject().put("text", promptText))

            val genConfig = JSONObject()
                .put("temperature", temperature.toDouble())
                .put("topP", 0.95)
                .put("maxOutputTokens", maxOutputTokens)
                .put("responseMimeType", "application/json")

            when {
                modelName.startsWith("gemini-3") -> {
                    genConfig.put("thinkingConfig", JSONObject().put("thinkingLevel", "MINIMAL"))
                }
                modelName.startsWith("gemini-2.5") -> {
                    genConfig.put("thinkingConfig", JSONObject().put("thinkingBudget", 0))
                }
            }

            val bodyJson = JSONObject()
                .put("contents", JSONArray().put(JSONObject().put("parts", parts)))
                .put("generationConfig", genConfig)
                .toString()

            conn.outputStream.use { os ->
                os.write(bodyJson.toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else (conn.errorStream ?: conn.inputStream)
            val responseBody = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            if (code !in 200..299) {
                throw Exception("HTTP $code: $responseBody")
            }

            val root = JSONObject(responseBody)
            val partsArr = root.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")

            val resultBuilder = StringBuilder()
            if (partsArr != null) {
                for (i in 0 until partsArr.length()) {
                    val p = partsArr.optJSONObject(i) ?: continue
                    if (!p.optBoolean("thought", false)) {
                        val txt = p.optString("text", "")
                        if (txt.isNotEmpty()) resultBuilder.append(txt)
                    }
                }
            }
            return resultBuilder.toString().trim()
        } finally {
            conn.disconnect()
        }
    }

    fun extractErrorMessage(e: Throwable, defaultMessage: String): String {
        val raw = e.message ?: return defaultMessage
        return if (raw.contains("\"message\":")) {
            try {
                val start = raw.indexOf('{')
                val end = raw.lastIndexOf('}')
                if (start != -1 && end != -1 && end > start) {
                    val json = JSONObject(raw.substring(start, end + 1))
                    val msg = json.optJSONObject("error")?.optString("message")
                    if (!msg.isNullOrBlank()) msg else raw
                } else {
                    raw
                }
            } catch (_: Exception) {
                raw
            }
        } else {
            raw
        }
    }
}
