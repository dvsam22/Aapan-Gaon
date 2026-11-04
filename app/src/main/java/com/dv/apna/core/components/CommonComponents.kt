package com.dv.apna.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dv.apna.R
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import java.net.URLEncoder

private val BrandGreen = Color(0xFF38C792)
private val LightGreenBg = Color(0xFFEFFAF6)
private val TextDark = Color(0xFF1E232C)
private val TextMuted = Color(0xFF8391A1)

@Composable
fun AapanGavButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF38C792),
    contentColor: Color = Color.White,
    disabledContainerColor: Color = Color(0xFF38C792).copy(alpha = 0.4f),
    disabledContentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(42.sdp()),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
        shape = RoundedCornerShape(27.sdp())
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.ssp()
            )
        )
    }
}

@Composable
fun AapanGavCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.sdp()),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.sdp())
    ) {
        Column(
            modifier = Modifier.padding(16.sdp()),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AapanGavToolbar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.ssp(),
                    fontWeight = FontWeight.Bold
                )
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.sdp())
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun AapanGavLoading(
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.sdp()),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

val LocalNavigateToAddBusiness = compositionLocalOf<() -> Unit> { {} }

@Composable
fun AapanGavEmptyData(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.sdp()),
    message: String = stringResource(id = R.string.no_data_found_title),
    subtitle: String = stringResource(id = R.string.no_data_found_subtitle),
    buttonText: String = stringResource(id = R.string.add_information),
    iconEmoji: String = "🤝",
    onActionClick: (() -> Unit)? = null
) {
    val defaultNavigateToAddBusiness = LocalNavigateToAddBusiness.current

    Box(
        modifier = modifier.padding(horizontal = 24.sdp()),
        contentAlignment = Alignment.Center
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
                    // Top Friendly Icon Badge
                    Box(
                        modifier = Modifier
                            .size(56.sdp())
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iconEmoji,
                            fontSize = 28.ssp()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.sdp()))

                    // Title Header ("No Data Found" or custom title/message)
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.ssp()
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.sdp()))

                    // Support Message
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.ssp(),
                            color = TextMuted,
                            lineHeight = 18.ssp()
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.sdp()))

                    // Action Button - Matching App Pill Shape
                    Button(
                        onClick = {
                            if (onActionClick != null) {
                                onActionClick()
                            } else {
                                defaultNavigateToAddBusiness()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.sdp()),
                        shape = RoundedCornerShape(27.sdp()),
                        contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text(
                            text = buttonText,
                            color = Color.White,
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
fun AapanGavErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier.padding(24.sdp()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.ssp()),
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.sdp()))
        AapanGavButton(
            text = "Retry",
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AapanGavEmptyDataPreview() {
    AapanGavEmptyData()
}
