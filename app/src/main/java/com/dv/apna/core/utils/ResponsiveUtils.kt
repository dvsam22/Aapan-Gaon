package com.dv.apna.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Utility to fetch Scalable DP (sdp) from resources.
 * Handles values from 1 to 150.
 */
@Composable
@ReadOnlyComposable
fun Int.sdp(): Dp {
    val id = when (this) {
        1 -> com.intuit.sdp.R.dimen._1sdp
        2 -> com.intuit.sdp.R.dimen._2sdp
        3 -> com.intuit.sdp.R.dimen._3sdp
        4 -> com.intuit.sdp.R.dimen._4sdp
        5 -> com.intuit.sdp.R.dimen._5sdp
        6 -> com.intuit.sdp.R.dimen._6sdp
        7 -> com.intuit.sdp.R.dimen._7sdp
        8 -> com.intuit.sdp.R.dimen._8sdp
        9 -> com.intuit.sdp.R.dimen._9sdp
        10 -> com.intuit.sdp.R.dimen._10sdp
        11 -> com.intuit.sdp.R.dimen._11sdp
        12 -> com.intuit.sdp.R.dimen._12sdp
        13 -> com.intuit.sdp.R.dimen._13sdp
        14 -> com.intuit.sdp.R.dimen._14sdp
        15 -> com.intuit.sdp.R.dimen._15sdp
        16 -> com.intuit.sdp.R.dimen._16sdp
        17 -> com.intuit.sdp.R.dimen._17sdp
        18 -> com.intuit.sdp.R.dimen._18sdp
        19 -> com.intuit.sdp.R.dimen._19sdp
        20 -> com.intuit.sdp.R.dimen._20sdp
        21 -> com.intuit.sdp.R.dimen._21sdp
        22 -> com.intuit.sdp.R.dimen._22sdp
        23 -> com.intuit.sdp.R.dimen._23sdp
        24 -> com.intuit.sdp.R.dimen._24sdp
        25 -> com.intuit.sdp.R.dimen._25sdp
        26 -> com.intuit.sdp.R.dimen._26sdp
        27 -> com.intuit.sdp.R.dimen._27sdp
        28 -> com.intuit.sdp.R.dimen._28sdp
        29 -> com.intuit.sdp.R.dimen._29sdp
        30 -> com.intuit.sdp.R.dimen._30sdp
        31 -> com.intuit.sdp.R.dimen._31sdp
        32 -> com.intuit.sdp.R.dimen._32sdp
        33 -> com.intuit.sdp.R.dimen._33sdp
        34 -> com.intuit.sdp.R.dimen._34sdp
        35 -> com.intuit.sdp.R.dimen._35sdp
        36 -> com.intuit.sdp.R.dimen._36sdp
        37 -> com.intuit.sdp.R.dimen._37sdp
        38 -> com.intuit.sdp.R.dimen._38sdp
        39 -> com.intuit.sdp.R.dimen._39sdp
        40 -> com.intuit.sdp.R.dimen._40sdp
        41 -> com.intuit.sdp.R.dimen._41sdp
        42 -> com.intuit.sdp.R.dimen._42sdp
        43 -> com.intuit.sdp.R.dimen._43sdp
        44 -> com.intuit.sdp.R.dimen._44sdp
        45 -> com.intuit.sdp.R.dimen._45sdp
        46 -> com.intuit.sdp.R.dimen._46sdp
        47 -> com.intuit.sdp.R.dimen._47sdp
        48 -> com.intuit.sdp.R.dimen._48sdp
        49 -> com.intuit.sdp.R.dimen._49sdp
        50 -> com.intuit.sdp.R.dimen._50sdp
        51 -> com.intuit.sdp.R.dimen._51sdp
        52 -> com.intuit.sdp.R.dimen._52sdp
        53 -> com.intuit.sdp.R.dimen._53sdp
        54 -> com.intuit.sdp.R.dimen._54sdp
        55 -> com.intuit.sdp.R.dimen._55sdp
        56 -> com.intuit.sdp.R.dimen._56sdp
        57 -> com.intuit.sdp.R.dimen._57sdp
        58 -> com.intuit.sdp.R.dimen._58sdp
        59 -> com.intuit.sdp.R.dimen._59sdp
        60 -> com.intuit.sdp.R.dimen._60sdp
        61 -> com.intuit.sdp.R.dimen._61sdp
        62 -> com.intuit.sdp.R.dimen._62sdp
        63 -> com.intuit.sdp.R.dimen._63sdp
        64 -> com.intuit.sdp.R.dimen._64sdp
        65 -> com.intuit.sdp.R.dimen._65sdp
        66 -> com.intuit.sdp.R.dimen._66sdp
        67 -> com.intuit.sdp.R.dimen._67sdp
        68 -> com.intuit.sdp.R.dimen._68sdp
        69 -> com.intuit.sdp.R.dimen._69sdp
        70 -> com.intuit.sdp.R.dimen._70sdp
        71 -> com.intuit.sdp.R.dimen._71sdp
        72 -> com.intuit.sdp.R.dimen._72sdp
        73 -> com.intuit.sdp.R.dimen._73sdp
        74 -> com.intuit.sdp.R.dimen._74sdp
        75 -> com.intuit.sdp.R.dimen._75sdp
        76 -> com.intuit.sdp.R.dimen._76sdp
        77 -> com.intuit.sdp.R.dimen._77sdp
        78 -> com.intuit.sdp.R.dimen._78sdp
        79 -> com.intuit.sdp.R.dimen._79sdp
        80 -> com.intuit.sdp.R.dimen._80sdp
        81 -> com.intuit.sdp.R.dimen._81sdp
        82 -> com.intuit.sdp.R.dimen._82sdp
        83 -> com.intuit.sdp.R.dimen._83sdp
        84 -> com.intuit.sdp.R.dimen._84sdp
        85 -> com.intuit.sdp.R.dimen._85sdp
        86 -> com.intuit.sdp.R.dimen._86sdp
        87 -> com.intuit.sdp.R.dimen._87sdp
        88 -> com.intuit.sdp.R.dimen._88sdp
        89 -> com.intuit.sdp.R.dimen._89sdp
        90 -> com.intuit.sdp.R.dimen._90sdp
        91 -> com.intuit.sdp.R.dimen._91sdp
        92 -> com.intuit.sdp.R.dimen._92sdp
        93 -> com.intuit.sdp.R.dimen._93sdp
        94 -> com.intuit.sdp.R.dimen._94sdp
        95 -> com.intuit.sdp.R.dimen._95sdp
        96 -> com.intuit.sdp.R.dimen._96sdp
        97 -> com.intuit.sdp.R.dimen._97sdp
        98 -> com.intuit.sdp.R.dimen._98sdp
        99 -> com.intuit.sdp.R.dimen._99sdp
        100 -> com.intuit.sdp.R.dimen._100sdp
        101 -> com.intuit.sdp.R.dimen._101sdp
        102 -> com.intuit.sdp.R.dimen._102sdp
        103 -> com.intuit.sdp.R.dimen._103sdp
        104 -> com.intuit.sdp.R.dimen._104sdp
        105 -> com.intuit.sdp.R.dimen._105sdp
        106 -> com.intuit.sdp.R.dimen._106sdp
        107 -> com.intuit.sdp.R.dimen._107sdp
        108 -> com.intuit.sdp.R.dimen._108sdp
        109 -> com.intuit.sdp.R.dimen._109sdp
        110 -> com.intuit.sdp.R.dimen._110sdp
        111 -> com.intuit.sdp.R.dimen._111sdp
        112 -> com.intuit.sdp.R.dimen._112sdp
        113 -> com.intuit.sdp.R.dimen._113sdp
        114 -> com.intuit.sdp.R.dimen._114sdp
        115 -> com.intuit.sdp.R.dimen._115sdp
        116 -> com.intuit.sdp.R.dimen._116sdp
        117 -> com.intuit.sdp.R.dimen._117sdp
        118 -> com.intuit.sdp.R.dimen._118sdp
        119 -> com.intuit.sdp.R.dimen._119sdp
        120 -> com.intuit.sdp.R.dimen._120sdp
        121 -> com.intuit.sdp.R.dimen._121sdp
        122 -> com.intuit.sdp.R.dimen._122sdp
        123 -> com.intuit.sdp.R.dimen._123sdp
        124 -> com.intuit.sdp.R.dimen._124sdp
        125 -> com.intuit.sdp.R.dimen._125sdp
        126 -> com.intuit.sdp.R.dimen._126sdp
        127 -> com.intuit.sdp.R.dimen._127sdp
        128 -> com.intuit.sdp.R.dimen._128sdp
        129 -> com.intuit.sdp.R.dimen._129sdp
        130 -> com.intuit.sdp.R.dimen._130sdp
        131 -> com.intuit.sdp.R.dimen._131sdp
        132 -> com.intuit.sdp.R.dimen._132sdp
        133 -> com.intuit.sdp.R.dimen._133sdp
        134 -> com.intuit.sdp.R.dimen._134sdp
        135 -> com.intuit.sdp.R.dimen._135sdp
        136 -> com.intuit.sdp.R.dimen._136sdp
        137 -> com.intuit.sdp.R.dimen._137sdp
        138 -> com.intuit.sdp.R.dimen._138sdp
        139 -> com.intuit.sdp.R.dimen._139sdp
        140 -> com.intuit.sdp.R.dimen._140sdp
        141 -> com.intuit.sdp.R.dimen._141sdp
        142 -> com.intuit.sdp.R.dimen._142sdp
        143 -> com.intuit.sdp.R.dimen._143sdp
        144 -> com.intuit.sdp.R.dimen._144sdp
        145 -> com.intuit.sdp.R.dimen._145sdp
        146 -> com.intuit.sdp.R.dimen._146sdp
        147 -> com.intuit.sdp.R.dimen._147sdp
        148 -> com.intuit.sdp.R.dimen._148sdp
        149 -> com.intuit.sdp.R.dimen._149sdp
        150 -> com.intuit.sdp.R.dimen._150sdp
        180 -> com.intuit.sdp.R.dimen._180sdp
        200 -> com.intuit.sdp.R.dimen._200sdp
        220 -> com.intuit.sdp.R.dimen._220sdp
        250 -> com.intuit.sdp.R.dimen._250sdp
        else -> com.intuit.sdp.R.dimen._10sdp
    }
    return dimensionResource(id = id)
}

/**
 * Utility to fetch Scalable SP (ssp) from resources.
 * Handles values from 1 to 150.
 */
@Composable
@ReadOnlyComposable
fun Int.ssp(): TextUnit {
    val id = when (this) {
        1 -> com.intuit.ssp.R.dimen._1ssp
        2 -> com.intuit.ssp.R.dimen._2ssp
        3 -> com.intuit.ssp.R.dimen._3ssp
        4 -> com.intuit.ssp.R.dimen._4ssp
        5 -> com.intuit.ssp.R.dimen._5ssp
        6 -> com.intuit.ssp.R.dimen._6ssp
        7 -> com.intuit.ssp.R.dimen._7ssp
        8 -> com.intuit.ssp.R.dimen._8ssp
        9 -> com.intuit.ssp.R.dimen._9ssp
        10 -> com.intuit.ssp.R.dimen._10ssp
        11 -> com.intuit.ssp.R.dimen._11ssp
        12 -> com.intuit.ssp.R.dimen._12ssp
        13 -> com.intuit.ssp.R.dimen._13ssp
        14 -> com.intuit.ssp.R.dimen._14ssp
        15 -> com.intuit.ssp.R.dimen._15ssp
        16 -> com.intuit.ssp.R.dimen._16ssp
        17 -> com.intuit.ssp.R.dimen._17ssp
        18 -> com.intuit.ssp.R.dimen._18ssp
        19 -> com.intuit.ssp.R.dimen._19ssp
        20 -> com.intuit.ssp.R.dimen._20ssp
        21 -> com.intuit.ssp.R.dimen._21ssp
        22 -> com.intuit.ssp.R.dimen._22ssp
        23 -> com.intuit.ssp.R.dimen._23ssp
        24 -> com.intuit.ssp.R.dimen._24ssp
        25 -> com.intuit.ssp.R.dimen._25ssp
        26 -> com.intuit.ssp.R.dimen._26ssp
        27 -> com.intuit.ssp.R.dimen._27ssp
        28 -> com.intuit.ssp.R.dimen._28ssp
        29 -> com.intuit.ssp.R.dimen._29ssp
        30 -> com.intuit.ssp.R.dimen._30ssp
        31 -> com.intuit.ssp.R.dimen._31ssp
        32 -> com.intuit.ssp.R.dimen._32ssp
        33 -> com.intuit.ssp.R.dimen._33ssp
        34 -> com.intuit.ssp.R.dimen._34ssp
        35 -> com.intuit.ssp.R.dimen._35ssp
        36 -> com.intuit.ssp.R.dimen._36ssp
        37 -> com.intuit.ssp.R.dimen._37ssp
        38 -> com.intuit.ssp.R.dimen._38ssp
        39 -> com.intuit.ssp.R.dimen._39ssp
        40 -> com.intuit.ssp.R.dimen._40ssp
        41 -> com.intuit.ssp.R.dimen._41ssp
        42 -> com.intuit.ssp.R.dimen._42ssp
        43 -> com.intuit.ssp.R.dimen._43ssp
        44 -> com.intuit.ssp.R.dimen._44ssp
        45 -> com.intuit.ssp.R.dimen._45ssp
        46 -> com.intuit.ssp.R.dimen._46ssp
        47 -> com.intuit.ssp.R.dimen._47ssp
        48 -> com.intuit.ssp.R.dimen._48ssp
        49 -> com.intuit.ssp.R.dimen._49ssp
        50 -> com.intuit.ssp.R.dimen._50ssp
        51 -> com.intuit.ssp.R.dimen._51ssp
        52 -> com.intuit.ssp.R.dimen._52ssp
        53 -> com.intuit.ssp.R.dimen._53ssp
        54 -> com.intuit.ssp.R.dimen._54ssp
        55 -> com.intuit.ssp.R.dimen._55ssp
        56 -> com.intuit.ssp.R.dimen._56ssp
        57 -> com.intuit.ssp.R.dimen._57ssp
        58 -> com.intuit.ssp.R.dimen._58ssp
        59 -> com.intuit.ssp.R.dimen._59ssp
        60 -> com.intuit.ssp.R.dimen._60ssp
        61 -> com.intuit.ssp.R.dimen._61ssp
        62 -> com.intuit.ssp.R.dimen._62ssp
        63 -> com.intuit.ssp.R.dimen._63ssp
        64 -> com.intuit.ssp.R.dimen._64ssp
        65 -> com.intuit.ssp.R.dimen._65ssp
        66 -> com.intuit.ssp.R.dimen._66ssp
        67 -> com.intuit.ssp.R.dimen._67ssp
        68 -> com.intuit.ssp.R.dimen._68ssp
        69 -> com.intuit.ssp.R.dimen._69ssp
        70 -> com.intuit.ssp.R.dimen._70ssp
        71 -> com.intuit.ssp.R.dimen._71ssp
        72 -> com.intuit.ssp.R.dimen._72ssp
        73 -> com.intuit.ssp.R.dimen._73ssp
        74 -> com.intuit.ssp.R.dimen._74ssp
        75 -> com.intuit.ssp.R.dimen._75ssp
        76 -> com.intuit.ssp.R.dimen._76ssp
        77 -> com.intuit.ssp.R.dimen._77ssp
        78 -> com.intuit.ssp.R.dimen._78ssp
        79 -> com.intuit.ssp.R.dimen._79ssp
        80 -> com.intuit.ssp.R.dimen._80ssp
        81 -> com.intuit.ssp.R.dimen._81ssp
        82 -> com.intuit.ssp.R.dimen._82ssp
        83 -> com.intuit.ssp.R.dimen._83ssp
        84 -> com.intuit.ssp.R.dimen._84ssp
        85 -> com.intuit.ssp.R.dimen._85ssp
        86 -> com.intuit.ssp.R.dimen._86ssp
        87 -> com.intuit.ssp.R.dimen._87ssp
        88 -> com.intuit.ssp.R.dimen._88ssp
        89 -> com.intuit.ssp.R.dimen._89ssp
        90 -> com.intuit.ssp.R.dimen._90ssp
        91 -> com.intuit.ssp.R.dimen._91ssp
        92 -> com.intuit.ssp.R.dimen._92ssp
        93 -> com.intuit.ssp.R.dimen._93ssp
        94 -> com.intuit.ssp.R.dimen._94ssp
        95 -> com.intuit.ssp.R.dimen._95ssp
        96 -> com.intuit.ssp.R.dimen._96ssp
        97 -> com.intuit.ssp.R.dimen._97ssp
        98 -> com.intuit.ssp.R.dimen._98ssp
        99 -> com.intuit.ssp.R.dimen._99ssp
        100 -> com.intuit.ssp.R.dimen._100ssp
        else -> com.intuit.ssp.R.dimen._14ssp
    }
    return dimensionResource(id = id).value.sp
}
