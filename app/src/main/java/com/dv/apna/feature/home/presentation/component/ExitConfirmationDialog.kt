package com.dv.apna.feature.home.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dv.apna.R
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp

private val BrandGreen = Color(0xFF38C792)
private val LightGreenBg = Color(0xFFEFFAF6)
private val TextDark = Color(0xFF1E232C)
private val TextMuted = Color(0xFF8391A1)

@Composable
fun ExitConfirmationDialog(
    onDismiss: () -> Unit,
    onExit: () -> Unit,
    onFeedback: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.sdp()),
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
                                text = "👋",
                                fontSize = 28.ssp()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.sdp()))

                        Text(
                            text = stringResource(id = R.string.exit_dialog_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.ssp()
                            ),
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.sdp()))

                        Text(
                            text = stringResource(id = R.string.exit_dialog_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.ssp(),
                                color = TextMuted
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.sdp()))

                        // Top Action (Feedback) - Matching App Pill Shape
                        Button(
                            onClick = onFeedback,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.sdp()),
                            shape = RoundedCornerShape(27.sdp()),
                            contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                        ) {
                            Text(
                                text = stringResource(id = R.string.exit_dialog_feedback_btn),
                                color = Color.White,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.sdp()))

                        // Bottom Action (Exit, Sure?) - Matching App Pill Shape
                        OutlinedButton(
                            onClick = onExit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.sdp()),
                            shape = RoundedCornerShape(27.sdp()),
                            contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                            border = BorderStroke(1.sdp(), BrandGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen)
                        ) {
                            Text(
                                text = stringResource(id = R.string.exit_dialog_exit_btn),
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
}
