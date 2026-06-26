package com.dv.apna.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.dv.apna.R
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp

val Poppins = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extra_bold, FontWeight.ExtraBold)
)

private val defaultTypography = Typography()

@Composable
fun appTypography(): Typography {
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = Poppins, fontSize = 57.ssp()),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = Poppins, fontSize = 45.ssp()),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = Poppins, fontSize = 36.ssp()),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 32.ssp(),
            lineHeight = 40.ssp()
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = Poppins, fontSize = 28.ssp()),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = Poppins, fontSize = 24.ssp()),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.ssp(),
            lineHeight = 28.ssp()
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = Poppins, fontSize = 16.ssp()),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = Poppins, fontSize = 14.ssp()),
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            fontSize = 16.ssp(),
            lineHeight = 24.ssp()
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = Poppins, fontSize = 14.ssp()),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = Poppins, fontSize = 12.ssp()),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = Poppins, fontSize = 14.ssp()),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 12.ssp(),
            lineHeight = 16.ssp()
        ),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = Poppins, fontSize = 11.ssp())
    )
}

@Composable
fun appShapes() = androidx.compose.material3.Shapes(
    extraSmall = RoundedCornerShape(4.sdp()),
    small = RoundedCornerShape(8.sdp()),
    medium = RoundedCornerShape(12.sdp()),
    large = RoundedCornerShape(16.sdp()),
    extraLarge = RoundedCornerShape(24.sdp())
)
