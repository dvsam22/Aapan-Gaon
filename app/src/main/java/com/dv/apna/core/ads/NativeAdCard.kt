package com.dv.apna.core.ads

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.utils.sdp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun rememberNativeAd(
    adUnitId: String,
    isEnabled: Boolean = true
): NativeAd? {
    if (!isEnabled) return null

    val context = LocalContext.current.applicationContext
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(adUnitId, isEnabled) {
        if (!isEnabled) return@LaunchedEffect
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Fail silently to avoid UI disruptions
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(Unit) {
        onDispose {
            nativeAd?.destroy()
        }
    }

    return nativeAd
}

@Composable
fun NativeAdCard(
    nativeAd: NativeAd?,
    modifier: Modifier = Modifier
) {
    if (nativeAd == null) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.sdp(),
                shape = RoundedCornerShape(15.sdp()),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(15.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.sdp(), Color(0xFF2CA074).copy(alpha = 0.4f))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                createNativeAdCardView(ctx, nativeAd)
            },
            update = { view ->
                populateNativeAdView(view, nativeAd)
            }
        )
    }
}

@Composable
fun NativeAdCard(
    remoteConfigManager: RemoteConfigManager,
    modifier: Modifier = Modifier
) {
    val nativeAd = rememberNativeAd(
        adUnitId = remoteConfigManager.getNativeAdUnitId(),
        isEnabled = remoteConfigManager.isNativeAdsEnabled()
    )
    NativeAdCard(nativeAd = nativeAd, modifier = modifier)
}

private fun sdpToPx(context: Context, resId: Int): Int {
    return context.resources.getDimensionPixelSize(resId)
}

private fun createNativeAdCardView(context: Context, ad: NativeAd): NativeAdView {
    val nativeAdView = NativeAdView(context)
    nativeAdView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    // Outer Container (Card padding inside Compose Card)
    val cardContainer = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val pad = sdpToPx(context, com.intuit.sdp.R.dimen._16sdp)
        setPadding(pad, pad, pad, pad)
    }

    // --- Top Header Row (Icon + Headline + Badge) ---
    val headerRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    // Ad Icon (Avatar Frame)
    val iconSize = sdpToPx(context, com.intuit.sdp.R.dimen._50sdp)
    val iconFrame = FrameLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
        background = GradientDrawable().apply {
            setColor(AndroidColor.parseColor("#EEF7F6"))
            cornerRadius = sdpToPx(context, com.intuit.sdp.R.dimen._12sdp).toFloat()
            setStroke(sdpToPx(context, com.intuit.sdp.R.dimen._1sdp), AndroidColor.parseColor("#6638C792"))
        }
    }
    val iconView = ImageView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ).apply {
            gravity = Gravity.CENTER
        }
        scaleType = ImageView.ScaleType.FIT_CENTER
    }
    iconFrame.addView(iconView)
    nativeAdView.iconView = iconView
    headerRow.addView(iconFrame)

    // Title & Info Column
    val titleCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = sdpToPx(context, com.intuit.sdp.R.dimen._12sdp)
        }
    }

    // Headline & "Ad" Badge Row
    val headlineRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    val headlineView = TextView(context).apply {
        textSize = 14f
        setTypeface(null, Typeface.BOLD)
        setTextColor(AndroidColor.BLACK)
        maxLines = 1
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }
    nativeAdView.headlineView = headlineView
    headlineRow.addView(headlineView)

    // Green "Ad" / "प्रायोजित" Badge
    val adBadge = TextView(context).apply {
        text = "Ad"
        textSize = 10f
        setTypeface(null, Typeface.BOLD)
        setTextColor(AndroidColor.parseColor("#2CA074"))
        val padH = sdpToPx(context, com.intuit.sdp.R.dimen._6sdp)
        val padV = sdpToPx(context, com.intuit.sdp.R.dimen._2sdp)
        setPadding(padH, padV, padH, padV)
        background = GradientDrawable().apply {
            setColor(AndroidColor.parseColor("#E6F7F1"))
            cornerRadius = sdpToPx(context, com.intuit.sdp.R.dimen._4sdp).toFloat()
            setStroke(sdpToPx(context, com.intuit.sdp.R.dimen._1sdp), AndroidColor.parseColor("#2CA074"))
        }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = sdpToPx(context, com.intuit.sdp.R.dimen._6sdp)
        }
    }
    headlineRow.addView(adBadge)
    titleCol.addView(headlineRow)

    // Subtitle / Advertiser
    val advertiserView = TextView(context).apply {
        textSize = 11f
        setTextColor(AndroidColor.parseColor("#99000000"))
        maxLines = 1
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = sdpToPx(context, com.intuit.sdp.R.dimen._2sdp)
        }
    }
    nativeAdView.advertiserView = advertiserView
    titleCol.addView(advertiserView)

    headerRow.addView(titleCol)
    cardContainer.addView(headerRow)

    // --- Body Text (Description) ---
    val bodyView = TextView(context).apply {
        textSize = 12f
        setTextColor(AndroidColor.parseColor("#B3000000"))
        maxLines = 2
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = sdpToPx(context, com.intuit.sdp.R.dimen._12sdp)
        }
    }
    nativeAdView.bodyView = bodyView
    cardContainer.addView(bodyView)

    // --- Call To Action Button (Matches Call Now button) ---
    val buttonHeight = sdpToPx(context, com.intuit.sdp.R.dimen._40sdp)
    val ctaButton = Button(context).apply {
        textSize = 14f
        setTypeface(null, Typeface.BOLD)
        setTextColor(AndroidColor.WHITE)
        isAllCaps = false
        setPadding(0, 0, 0, 0)
        includeFontPadding = false
        gravity = Gravity.CENTER
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            buttonHeight
        ).apply {
            topMargin = sdpToPx(context, com.intuit.sdp.R.dimen._16sdp)
        }
        background = GradientDrawable().apply {
            setColor(AndroidColor.parseColor("#38C792"))
            cornerRadius = sdpToPx(context, com.intuit.sdp.R.dimen._27sdp).toFloat()
        }
        elevation = 0f
    }
    nativeAdView.callToActionView = ctaButton
    cardContainer.addView(ctaButton)

    nativeAdView.addView(cardContainer)
    populateNativeAdView(nativeAdView, ad)

    return nativeAdView
}

private fun populateNativeAdView(nativeAdView: NativeAdView, ad: NativeAd) {
    (nativeAdView.headlineView as? TextView)?.text = ad.headline

    if (ad.body == null) {
        nativeAdView.bodyView?.visibility = View.GONE
    } else {
        nativeAdView.bodyView?.visibility = View.VISIBLE
        (nativeAdView.bodyView as? TextView)?.text = ad.body
    }

    if (ad.advertiser == null) {
        nativeAdView.advertiserView?.visibility = View.GONE
    } else {
        nativeAdView.advertiserView?.visibility = View.VISIBLE
        (nativeAdView.advertiserView as? TextView)?.text = ad.advertiser
    }

    if (ad.icon == null) {
        nativeAdView.iconView?.visibility = View.GONE
    } else {
        nativeAdView.iconView?.visibility = View.VISIBLE
        (nativeAdView.iconView as? ImageView)?.setImageDrawable(ad.icon?.drawable)
    }

    if (ad.callToAction == null) {
        nativeAdView.callToActionView?.visibility = View.GONE
    } else {
        nativeAdView.callToActionView?.visibility = View.VISIBLE
        (nativeAdView.callToActionView as? Button)?.text = ad.callToAction
    }

    nativeAdView.setNativeAd(ad)
}
