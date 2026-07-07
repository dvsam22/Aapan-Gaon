package com.dv.apna.core.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import com.dv.apna.core.utils.sdp

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200)
        ),
        label = "shimmerOffsetX"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFEBEBF4),
                Color(0xFFF4F4F4),
                Color(0xFFEBEBF4),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

@Composable
fun LabourSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp()),
        verticalArrangement = Arrangement.spacedBy(16.sdp())
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.sdp(),
                        shape = RoundedCornerShape(15.sdp())
                    )
                    .clip(RoundedCornerShape(15.sdp()))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.sdp())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.sdp())
                                .clip(RoundedCornerShape(12.sdp()))
                                .shimmerEffect()
                        )

                        Spacer(modifier = Modifier.width(12.sdp()))

                        Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                            Box(
                                modifier = Modifier
                                    .width(120.sdp())
                                    .height(16.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(80.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.sdp()))

                    repeat(2) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.sdp())
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.width(8.sdp()))
                            Box(
                                modifier = Modifier
                                    .width(150.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.sdp()))
                    }

                    Spacer(modifier = Modifier.height(8.sdp()))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.sdp())
                            .clip(RoundedCornerShape(27.sdp()))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}


@Composable
fun ConstructionSkeleton() {
    LabourSkeleton() // Since they have very similar card layouts
}

@Composable
fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.sdp())
    ) {
        // Banner Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp())
                .height(130.sdp())
                .clip(RoundedCornerShape(12.sdp()))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(20.sdp()))

        // Services Title Skeleton
        Box(
            modifier = Modifier
                .padding(start = 16.sdp())
                .width(100.sdp())
                .height(20.sdp())
                .clip(RoundedCornerShape(4.sdp()))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(15.sdp()))

        // Services Grid Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp()),
            verticalArrangement = Arrangement.spacedBy(8.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.sdp())
                    .clip(RoundedCornerShape(12.sdp()))
                    .shimmerEffect()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.sdp())
                        .clip(RoundedCornerShape(12.sdp()))
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.sdp())
                        .clip(RoundedCornerShape(12.sdp()))
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
fun MandiTableSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp()),
        verticalArrangement = Arrangement.spacedBy(16.sdp())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.sdp(), shape = RoundedCornerShape(15.sdp()))
                .clip(RoundedCornerShape(15.sdp()))
                .background(Color.White)
                .padding(16.sdp())
        ) {
            Column {
                // Skeleton Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.sdp()),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.sdp())
                            .height(14.sdp())
                            .clip(RoundedCornerShape(4.sdp()))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.sdp())
                            .height(14.sdp())
                            .clip(RoundedCornerShape(4.sdp()))
                            .shimmerEffect()
                    )
                    Box(
                        modifier = Modifier
                            .width(50.sdp())
                            .height(14.sdp())
                            .clip(RoundedCornerShape(4.sdp()))
                            .shimmerEffect()
                    )
                }

                // Skeleton Rows
                Column(verticalArrangement = Arrangement.spacedBy(12.sdp())) {
                    repeat(12) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(100.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(40.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(45.sdp())
                                        .height(12.sdp())
                                        .clip(RoundedCornerShape(4.sdp()))
                                        .shimmerEffect()
                                )
                                Spacer(modifier = Modifier.width(4.sdp()))
                                Box(
                                    modifier = Modifier
                                        .size(12.sdp())
                                        .clip(CircleShape)
                                        .shimmerEffect()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalBuyerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp()),
        verticalArrangement = Arrangement.spacedBy(16.sdp())
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.sdp(),
                        shape = RoundedCornerShape(15.sdp())
                    )
                    .clip(RoundedCornerShape(15.sdp()))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.sdp())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.sdp())
                                .clip(RoundedCornerShape(12.sdp()))
                                .shimmerEffect()
                        )

                        Spacer(modifier = Modifier.width(12.sdp()))

                        Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                            Box(
                                modifier = Modifier
                                    .width(120.sdp())
                                    .height(16.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(150.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.sdp()))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.sdp())
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                        Spacer(modifier = Modifier.width(8.sdp()))
                        Box(
                            modifier = Modifier
                                .width(150.sdp())
                                .height(12.sdp())
                                .clip(RoundedCornerShape(4.sdp()))
                                .shimmerEffect()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.sdp()))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.sdp())
                            .clip(RoundedCornerShape(27.sdp()))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun TransportSkeleton() {
    LocalBuyerSkeleton() // Layout is very similar
}

@Composable
fun HealthSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp()),
        verticalArrangement = Arrangement.spacedBy(16.sdp())
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.sdp(),
                        shape = RoundedCornerShape(15.sdp())
                    )
                    .clip(RoundedCornerShape(15.sdp()))
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.sdp())
                ) {
                    // Top row: Icon + Name/Address
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.sdp())
                                .clip(RoundedCornerShape(12.sdp()))
                                .shimmerEffect()
                        )

                        Spacer(modifier = Modifier.width(12.sdp()))

                        Column(verticalArrangement = Arrangement.spacedBy(8.sdp())) {
                            Box(
                                modifier = Modifier
                                    .width(140.sdp())
                                    .height(16.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                            Box(
                                modifier = Modifier
                                    .width(100.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.sdp()))

                    // Detail rows: Specialization/Facilities and Timing/Status
                    repeat(2) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.sdp())
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.width(8.sdp()))
                            Box(
                                modifier = Modifier
                                    .width(180.sdp())
                                    .height(12.sdp())
                                    .clip(RoundedCornerShape(4.sdp()))
                                    .shimmerEffect()
                            )
                        }
                        Spacer(modifier = Modifier.height(10.sdp()))
                    }

                    Spacer(modifier = Modifier.height(8.sdp()))

                    // Call Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.sdp())
                            .clip(RoundedCornerShape(27.sdp()))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
