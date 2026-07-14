package com.dv.apna.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
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

val NotoSansHindi = FontFamily(
    Font(R.font.noto_sans_light, FontWeight.Light),
    Font(R.font.noto_sans_regular, FontWeight.Normal),
    Font(R.font.noto_sans_medium, FontWeight.Medium),
    Font(R.font.noto_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.noto_sans_bold, FontWeight.Bold),
    Font(R.font.noto_sans_extra_bold, FontWeight.ExtraBold)
)

private val defaultTypography = Typography()

@Composable
fun appTypography(): Typography {
    val configuration = LocalConfiguration.current
    val isHindi = configuration.locales[0].language == "hi"
    val activeFontFamily = if (isHindi) NotoSansHindi else Poppins

    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = activeFontFamily, fontSize = 57.ssp()),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = activeFontFamily, fontSize = 45.ssp()),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = activeFontFamily, fontSize = 36.ssp()),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = activeFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.ssp(),
            lineHeight = 40.ssp()
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = activeFontFamily, fontSize = 28.ssp()),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = activeFontFamily, fontSize = 24.ssp()),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = activeFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.ssp(),
            lineHeight = 28.ssp()
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = activeFontFamily, fontSize = 16.ssp()),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = activeFontFamily, fontSize = 14.ssp()),
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = activeFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.ssp(),
            lineHeight = 24.ssp()
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = activeFontFamily, fontSize = 14.ssp()),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = activeFontFamily, fontSize = 12.ssp()),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = activeFontFamily, fontSize = 14.ssp()),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = activeFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.ssp(),
            lineHeight = 16.ssp()
        ),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = activeFontFamily, fontSize = 11.ssp())
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
