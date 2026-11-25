package com.dv.apna.feature.crop_doctor.presentation.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dv.apna.R
import com.dv.apna.core.ads.BannerAdView
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.crop_doctor.domain.model.CropDiagnosisResult
import com.dv.apna.feature.crop_doctor.presentation.effect.CropDoctorEffect
import com.dv.apna.feature.crop_doctor.presentation.event.CropDoctorEvent
import com.dv.apna.feature.crop_doctor.presentation.state.CropDoctorState
import com.dv.apna.feature.crop_doctor.presentation.util.CropDoctorShareHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.io.File

private val BrandGreen = Color(0xFF38C792)
private val DarkGreen = Color(0xFF2CA074)
private val LightGreenBg = Color(0xFFEFFAF6)
private val TextDark = Color(0xFF1E232C)
private val TextMuted = Color(0xFF8391A1)

@Composable
fun CropDoctorScreen(
    state: CropDoctorState,
    onEvent: (CropDoctorEvent) -> Unit,
    effect: Flow<CropDoctorEffect>,
    remoteConfigManager: RemoteConfigManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val cameraFile = remember { File(context.cacheDir, "crop_camera_temp.jpg") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && cameraFile.exists() && cameraFile.length() > 0) {
            val bitmap = loadOrientedBitmap(cameraFile.absolutePath)
            if (bitmap != null) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cameraFile
                )
                onEvent(CropDoctorEvent.ImageSelected(uri, bitmap))
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = uriToBitmap(context, uri)
            onEvent(CropDoctorEvent.ImageSelected(uri, bitmap))
        }
    }

    val startCamera = {
        try {
            if (cameraFile.exists()) cameraFile.delete()
            cameraFile.createNewFile()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cameraFile
            )
            cameraLauncher.launch(uri)
        } catch (_: Exception) {
            val isHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
            Toast.makeText(
                context,
                if (isHindi) "कैमरा शुरू नहीं हो सका" else "Unable to start camera",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(CropDoctorEvent.StopSpeech)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(context, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        effect.collectLatest { eff ->
            when (eff) {
                CropDoctorEffect.NavigateBack -> onNavigateBack()
                is CropDoctorEffect.ShowToast -> {
                    Toast.makeText(context, eff.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MintGradientStart, MintGradientMiddle, MintGradientEnd)))
    ) {
        val (bottomImage, mainContent) = createRefs()

        // Bottom Decoration Image
        Image(
            painter = painterResource(id = R.drawable.iv_bottomview),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomImage) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            // Standard App Top Bar
            CropDoctorTopBar(onBackClick = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.sdp(), vertical = 4.sdp())
            ) {
                if (state.diagnosisResult == null && !state.isLoading) {
                    // Initial State: Pick Photo
                    InitialUploadSection(
                        onCameraClick = {
                            val isGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (isGranted) {
                                startCamera()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onGalleryClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.sdp()))

                    // Tips Section
                    TipsCard()
                } else if (state.isLoading) {
                    // Loading State
                    LoadingScanSection(selectedBitmap = state.selectedBitmap)
                } else if (state.diagnosisResult != null) {
                    // Result State
                    DiagnosisResultSection(
                        result = state.diagnosisResult,
                        selectedBitmap = state.selectedBitmap,
                        isSpeaking = state.isSpeaking,
                        onToggleSpeech = { onEvent(CropDoctorEvent.ToggleSpeech) },
                        onReset = { onEvent(CropDoctorEvent.ResetScan) },
                        onShareWhatsApp = {
                            val isCurrentHindi = context.resources.configuration.locales[0].language.equals("hi", ignoreCase = true)
                            val shareText = CropDoctorShareHelper.formatReportShareText(
                                result = state.diagnosisResult,
                                packageName = context.packageName,
                                isHindi = isCurrentHindi
                            )
                            CropDoctorShareHelper.shareText(context, shareText, isHindi = isCurrentHindi)
                        }
                    )
                }

                // Error display if any
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(12.sdp()))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.sdp()),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.sdp())
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.sdp()))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.sdp()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.sdp())
                                )
                                Spacer(modifier = Modifier.width(8.sdp()))
                                Text(
                                    text = state.error.asString(),
                                    color = Color(0xFFC62828),
                                    fontSize = 12.ssp()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.sdp()))
                BannerAdView(remoteConfigManager = remoteConfigManager)
            }
        }
    }
}

@Composable
fun CropDoctorTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 16.sdp()),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .width(34.sdp())
                .height(46.sdp())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() },
            shape = RoundedCornerShape(24.sdp()),
            color = Color(0xFFEFFAF6),
            border = BorderStroke(1.sdp(), Color(0xFFD9D9D9))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier.size(20.sdp()),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = stringResource(id = R.string.crop_doctor_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.ssp()
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun InitialUploadSection(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.sdp()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Friendly White Icon Badge (Matching App Standard / AapanGavEmptyData)
                Box(
                    modifier = Modifier
                        .size(56.sdp())
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(28.sdp())
                    )
                }

                Spacer(modifier = Modifier.height(14.sdp()))

                // Title
                Text(
                    text = stringResource(id = R.string.crop_doctor_take_photo),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.ssp()
                    ),
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.sdp()))

                // Subtitle / Description
                Text(
                    text = stringResource(id = R.string.crop_doctor_take_photo_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.ssp(),
                        color = TextMuted,
                        lineHeight = 18.ssp()
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.sdp()))

                // Primary Button (Camera)
                Button(
                    onClick = onCameraClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.sdp()),
                    shape = RoundedCornerShape(27.sdp()),
                    contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = null,
                            modifier = Modifier.size(18.sdp()),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.sdp()))
                        Text(
                            text = stringResource(id = R.string.crop_doctor_btn_camera),
                            color = Color.White,
                            fontSize = 13.ssp(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.sdp()))

                // Secondary Button (Gallery)
                Button(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.sdp()),
                    shape = RoundedCornerShape(27.sdp()),
                    border = BorderStroke(1.sdp(), BrandGreen),
                    contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BrandGreen
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(18.sdp()),
                            tint = BrandGreen
                        )
                        Spacer(modifier = Modifier.width(8.sdp()))
                        Text(
                            text = stringResource(id = R.string.crop_doctor_btn_gallery),
                            color = BrandGreen,
                            fontSize = 13.ssp(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(modifier = Modifier.padding(16.sdp())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lightbulb),
                        contentDescription = null,
                        modifier = Modifier.size(18.sdp()),
                        tint = BrandGreen
                    )
                    Spacer(modifier = Modifier.width(6.sdp()))
                    Text(
                        text = stringResource(id = R.string.crop_doctor_tips_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.ssp()
                        ),
                        color = TextDark
                    )
                }
                Spacer(modifier = Modifier.height(8.sdp()))
                Text(
                    text = stringResource(id = R.string.crop_doctor_tips_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.ssp(),
                        color = TextMuted,
                        lineHeight = 18.ssp()
                    )
                )
            }
        }
    }
}

@Composable
private fun LoadingScanSection(selectedBitmap: Bitmap?) {
    // Stepped progressive status messages
    val stepTitles = listOf(
        stringResource(id = R.string.crop_doctor_step1_title),
        stringResource(id = R.string.crop_doctor_step2_title),
        stringResource(id = R.string.crop_doctor_step3_title)
    )
    val stepDescriptions = listOf(
        stringResource(id = R.string.crop_doctor_step1_desc),
        stringResource(id = R.string.crop_doctor_step2_desc),
        stringResource(id = R.string.crop_doctor_step3_desc)
    )
    var stepIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2200)
            stepIndex = (stepIndex + 1) % stepTitles.size
        }
    }

    // AI Laser Beam Sweep Animation
    val transition = rememberInfiniteTransition(label = "scannerTransition")
    val laserProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.sdp(), vertical = 20.sdp()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The Photo with AI Laser Scanner Overlay
                if (selectedBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(140.sdp())
                            .clip(RoundedCornerShape(12.sdp()))
                    ) {
                        Image(
                            bitmap = selectedBitmap.asImageBitmap(),
                            contentDescription = "Scanning crop photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // AI Scanner Overlay (Glowing laser line + beam gradient + corner brackets)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // 1. Draw 4 Corner Scanning Brackets (Google Lens style)
                            val bracketLen = 18.dp.toPx()
                            val strokeW = 3.dp.toPx()
                            val bracketColor = BrandGreen

                            // Top-Left
                            drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW)
                            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), strokeW)
                            // Top-Right
                            drawLine(bracketColor, Offset(w, 0f), Offset(w - bracketLen, 0f), strokeW)
                            drawLine(bracketColor, Offset(w, 0f), Offset(w, bracketLen), strokeW)
                            // Bottom-Left
                            drawLine(bracketColor, Offset(0f, h), Offset(bracketLen, h), strokeW)
                            drawLine(bracketColor, Offset(0f, h), Offset(0f, h - bracketLen), strokeW)
                            // Bottom-Right
                            drawLine(bracketColor, Offset(w, h), Offset(w - bracketLen, h), strokeW)
                            drawLine(bracketColor, Offset(w, h), Offset(w, h - bracketLen), strokeW)

                            // 2. Draw Moving Laser Beam with upward gradient trail
                            val laserY = laserProgress * h
                            val trailHeight = 40.dp.toPx()

                            // Vertical gradient trail behind the laser line
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BrandGreen.copy(alpha = 0.28f),
                                        BrandGreen.copy(alpha = 0.65f)
                                    ),
                                    startY = (laserY - trailHeight).coerceAtLeast(0f),
                                    endY = laserY
                                ),
                                topLeft = Offset(0f, (laserY - trailHeight).coerceAtLeast(0f)),
                                size = Size(w, trailHeight)
                            )

                            // 3. Crisp Glowing Laser Line
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, laserY),
                                end = Offset(w, laserY),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = BrandGreen,
                                start = Offset(0f, laserY),
                                end = Offset(w, laserY),
                                strokeWidth = 4.dp.toPx()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.sdp()))
                }

                // Sleek Horizontal Linear Progress Bar (Matching modern app standard)
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(5.sdp())
                        .clip(RoundedCornerShape(3.sdp())),
                    color = BrandGreen,
                    trackColor = Color(0xFFDCEFE7)
                )

                Spacer(modifier = Modifier.height(14.sdp()))

                // Dynamic Animated Step Title with Premium Vector Icon - Strictly Single Line & Centered
                AnimatedContent(
                    targetState = stepIndex,
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(400)) + slideInVertically { height -> height / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutVertically { height -> -height / 2 })
                    },
                    label = "stepTitleAnimation"
                ) { targetStep ->
                    val stepIconRes = when (targetStep % 3) {
                        0 -> R.drawable.ic_plant_scan
                        1 -> R.drawable.ic_symptoms
                        else -> R.drawable.ic_chemical_treatment
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = stepIconRes),
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(16.sdp())
                            )
                            Spacer(modifier = Modifier.width(6.sdp()))
                            Text(
                                text = stepTitles[targetStep % stepTitles.size],
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.ssp()
                                ),
                                color = TextDark,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(5.sdp()))

                        Text(
                            text = stepDescriptions[targetStep % stepDescriptions.size],
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 11.ssp(),
                                color = TextMuted,
                                lineHeight = 16.ssp()
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.sdp())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosisResultSection(
    result: CropDiagnosisResult,
    selectedBitmap: Bitmap?,
    isSpeaking: Boolean,
    onToggleSpeech: () -> Unit,
    onReset: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    if (!result.isPlant) {
        // Not a plant warning
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.sdp()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp())
                    .background(LightGreenBg, RoundedCornerShape(8.sdp()))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.sdp()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.sdp())
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_alert_triangle),
                            contentDescription = null,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(28.sdp())
                        )
                    }
                    Spacer(modifier = Modifier.height(14.sdp()))
                    Text(
                        text = stringResource(id = R.string.crop_doctor_not_plant_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.ssp()
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.sdp()))
                    Text(
                        text = result.generalAdvice.ifBlank { stringResource(id = R.string.crop_doctor_not_plant_desc) },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.ssp(),
                            color = TextMuted,
                            lineHeight = 18.ssp()
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.sdp()))
                    Button(
                        onClick = onReset,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.sdp()),
                        shape = RoundedCornerShape(20.sdp()),
                        contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.sdp()),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.sdp()))
                            Text(
                                text = stringResource(id = R.string.crop_doctor_btn_retake),
                                color = Color.White,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        return
    }

    // Success Diagnosis Report
    Column(modifier = Modifier.fillMaxWidth()) {
        // Main Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.sdp()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp())
                    .background(LightGreenBg, RoundedCornerShape(8.sdp()))
            ) {
                Column(modifier = Modifier.padding(12.sdp())) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedBitmap != null) {
                            Image(
                                bitmap = selectedBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.sdp())
                                    .clip(RoundedCornerShape(8.sdp())),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.sdp()))
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.cropName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.ssp()
                                ),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.sdp()))
                            Surface(
                                shape = RoundedCornerShape(12.sdp()),
                                color = Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (result.isHealthy) BrandGreen else Color(0xFFEF5350)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.sdp(), vertical = 4.sdp()),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.sdp())
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (result.isHealthy) R.drawable.ic_check_circle else R.drawable.ic_alert_triangle
                                        ),
                                        contentDescription = null,
                                        tint = if (result.isHealthy) DarkGreen else Color(0xFFC62828),
                                        modifier = Modifier.size(12.sdp())
                                    )
                                    Text(
                                        text = if (result.isHealthy) stringResource(id = R.string.crop_doctor_healthy) else result.diseaseName,
                                        color = if (result.isHealthy) DarkGreen else Color(0xFFC62828),
                                        fontSize = 11.ssp(),
                                        lineHeight = 14.ssp(),
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hands-Free Audio Speaker Card (Matching What to Cook Listen card)
        Spacer(modifier = Modifier.height(10.sdp()))
        CropReportAudioCard(
            isSpeaking = isSpeaking,
            onToggleSpeech = onToggleSpeech
        )

        // Symptoms Section
        if (result.symptoms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.sdp()))
            ReportSectionCard(
                title = stringResource(id = R.string.crop_doctor_symptoms),
                titleColor = TextDark,
                iconRes = R.drawable.ic_symptoms
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.sdp())) {
                    result.symptoms.forEach { symptom ->
                        Text(
                            text = "• $symptom",
                            fontSize = 12.ssp(),
                            color = Color(0xFF424242),
                            lineHeight = 16.ssp()
                        )
                    }
                }
            }
        }

        // Chemical Treatment
        if (result.chemicalTreatment.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.sdp()))
            ReportSectionCard(
                title = stringResource(id = R.string.crop_doctor_chemical),
                titleColor = Color(0xFF1565C0),
                iconRes = R.drawable.ic_chemical_treatment
            ) {
                Text(
                    text = result.chemicalTreatment,
                    fontSize = 12.ssp(),
                    color = Color.Black,
                    lineHeight = 18.ssp()
                )
            }
        }

        // Organic Treatment
        if (result.organicTreatment.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.sdp()))
            ReportSectionCard(
                title = stringResource(id = R.string.crop_doctor_organic),
                titleColor = DarkGreen,
                iconRes = R.drawable.ic_organic_remedy
            ) {
                Text(
                    text = result.organicTreatment,
                    fontSize = 12.ssp(),
                    color = Color.Black,
                    lineHeight = 18.ssp()
                )
            }
        }

        // Precautions
        if (result.precautions.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.sdp()))
            ReportSectionCard(
                title = stringResource(id = R.string.crop_doctor_precautions),
                titleColor = Color(0xFFE65100),
                iconRes = R.drawable.ic_shield_alert
            ) {
                Text(
                    text = result.precautions,
                    fontSize = 12.ssp(),
                    color = Color(0xFF424242),
                    lineHeight = 17.ssp()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.sdp()))

        // 1-Click WhatsApp Share Button - Matching TodayMarketScreen (Image 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp(), vertical = 4.sdp()),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onShareWhatsApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.sdp()),
                shape = RoundedCornerShape(21.sdp()),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iv_whatsapp),
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(18.sdp())
                    )
                    Spacer(modifier = Modifier.width(8.sdp()))
                    Text(
                        text = stringResource(id = R.string.send_on_whatsapp),
                        color = Color.White,
                        fontSize = 13.ssp(),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.sdp()))

        // Scan Another Button - Matching exact same width, height & shape
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp(), vertical = 4.sdp()),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.sdp()),
                shape = RoundedCornerShape(21.sdp()),
                border = BorderStroke(1.dp, BrandGreen),
                contentPadding = PaddingValues(horizontal = 16.sdp(), vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = BrandGreen
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(18.sdp())
                    )
                    Spacer(modifier = Modifier.width(8.sdp()))
                    Text(
                        text = stringResource(id = R.string.crop_doctor_scan_another),
                        color = BrandGreen,
                        fontSize = 13.ssp(),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun CropReportAudioCard(
    isSpeaking: Boolean,
    onToggleSpeech: () -> Unit
) {
    val activeDarkGreen = Color(0xFF1B6A4C)
    val stopRed = Color(0xFFDC2626)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onToggleSpeech() },
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(
                    if (isSpeaking) Color(0xFFFFEBEE) else LightGreenBg,
                    RoundedCornerShape(8.sdp())
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.sdp(), vertical = 10.sdp()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(34.sdp()),
                    shape = CircleShape,
                    color = if (isSpeaking) Color(0xFFFFCDD2) else Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(
                                id = if (isSpeaking) R.drawable.ic_speaker_stop else R.drawable.ic_speaker_sound
                            ),
                            contentDescription = null,
                            tint = if (isSpeaking) stopRed else activeDarkGreen,
                            modifier = Modifier.size(18.sdp())
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.sdp()))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSpeaking) {
                            stringResource(id = R.string.crop_doctor_stop_speaking)
                        } else {
                            stringResource(id = R.string.crop_doctor_listen_title)
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.ssp(),
                            color = if (isSpeaking) stopRed else activeDarkGreen
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(id = R.string.crop_doctor_listen_sub),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.ssp(),
                            color = TextMuted
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.sdp()))

                Surface(
                    shape = RoundedCornerShape(20.sdp()),
                    color = if (isSpeaking) stopRed else BrandGreen,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.sdp()))
                        .clickable { onToggleSpeech() }
                ) {
                    Row(
                        modifier = Modifier.padding(start = 10.sdp(), end = 6.sdp(), top = 5.sdp(), bottom = 5.sdp()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.sdp())
                    ) {
                        Text(
                            text = if (isSpeaking) {
                                stringResource(id = R.string.kya_banega_btn_stop)
                            } else {
                                stringResource(id = R.string.kya_banega_btn_listen)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.ssp(),
                                color = Color.White
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                        Box(
                            modifier = Modifier
                                .size(20.sdp())
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = if (isSpeaking) stopRed else BrandGreen,
                                modifier = Modifier.size(14.sdp())
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSectionCard(
    title: String,
    titleColor: Color,
    @DrawableRes iconRes: Int? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.sdp())
                .background(LightGreenBg, RoundedCornerShape(8.sdp()))
        ) {
            Column(modifier = Modifier.padding(14.sdp())) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = titleColor,
                            modifier = Modifier.size(16.sdp())
                        )
                        Spacer(modifier = Modifier.width(6.sdp()))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.ssp()
                        ),
                        color = titleColor
                    )
                }
                Spacer(modifier = Modifier.height(6.sdp()))
                content()
            }
        }
    }
}

private fun loadOrientedBitmap(filePath: String, maxDimension: Int = 1280): Bitmap? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(filePath, boundsOptions)
        val origW = boundsOptions.outWidth
        val origH = boundsOptions.outHeight
        if (origW <= 0 || origH <= 0) return null

        var sampleSize = 1
        var halfW = origW
        var halfH = origH
        while (halfW > maxDimension || halfH > maxDimension) {
            sampleSize *= 2
            halfW /= 2
            halfH /= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val sampledBitmap = BitmapFactory.decodeFile(filePath, decodeOptions) ?: return null

        val exif = try { ExifInterface(filePath) } catch (_: Exception) { null }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        val currentW = sampledBitmap.width
        val currentH = sampledBitmap.height
        val maxCurr = maxOf(currentW, currentH)
        if (maxCurr > maxDimension) {
            val scale = maxDimension.toFloat() / maxCurr.toFloat()
            matrix.postScale(scale, scale)
        }

        val finalBitmap = Bitmap.createBitmap(
            sampledBitmap, 0, 0, currentW, currentH, matrix, true
        )
        if (finalBitmap != sampledBitmap) {
            sampledBitmap.recycle()
        }
        finalBitmap
    } catch (_: Exception) {
        null
    }
}

private fun uriToBitmap(context: Context, uri: Uri, maxDimension: Int = 1280): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.isMutableRequired = true
                val size = info.size
                val maxDim = maxOf(size.width, size.height)
                if (maxDim > maxDimension) {
                    val ratio = maxDimension.toFloat() / maxDim.toFloat()
                    val targetW = (size.width * ratio).toInt().coerceAtLeast(1)
                    val targetH = (size.height * ratio).toInt().coerceAtLeast(1)
                    decoder.setTargetSize(targetW, targetH)
                }
            }
        } else {
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            val origW = boundsOptions.outWidth
            val origH = boundsOptions.outHeight
            if (origW <= 0 || origH <= 0) return null

            var sampleSize = 1
            var halfW = origW
            var halfH = origH
            while (halfW > maxDimension || halfH > maxDimension) {
                sampleSize *= 2
                halfW /= 2
                halfH /= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val sampledBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            } ?: return null

            val orientation = try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    ExifInterface(stream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } ?: ExifInterface.ORIENTATION_NORMAL
            } catch (_: Exception) {
                ExifInterface.ORIENTATION_NORMAL
            }

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            val currentW = sampledBitmap.width
            val currentH = sampledBitmap.height
            val maxCurr = maxOf(currentW, currentH)
            if (maxCurr > maxDimension) {
                val scale = maxDimension.toFloat() / maxCurr.toFloat()
                matrix.postScale(scale, scale)
            }

            val finalBitmap = Bitmap.createBitmap(
                sampledBitmap, 0, 0, currentW, currentH, matrix, true
            )
            if (finalBitmap != sampledBitmap) {
                sampledBitmap.recycle()
            }
            finalBitmap
        }
    } catch (_: Exception) {
        null
    }
}
