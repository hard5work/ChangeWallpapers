package com.xdroid.app.changewallpaper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.xdroid.app.changewallpaper.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val Lato = FontFamily(
    Font(R.font.lato_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(R.font.lato_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.lato_black, weight = FontWeight.Black, style = FontStyle.Normal),
    Font(R.font.lato_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(R.font.lato_thin, weight = FontWeight.Thin, style = FontStyle.Normal),
)



val latoLight12 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Light,
    fontSize = 12.sp
)
val latoLight10 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Light,
    fontSize = 10.sp
)
val latoLight8 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Light,
    fontSize = 8.sp
)
val latoBold8 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Bold,
    fontSize = 8.sp
)

val latoLight6 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Light,
    fontSize = 6.sp
)
val latoRegular16 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)
val latoRegular20 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp
)

val latoRegular12 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)
val latoBold16 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp
)
val latoBold18 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)


val latoBold14 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp
)

val latoMedium10 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp
)

val latoMedium16 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp
)

val latoBold24 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)

val latoSemiBold20 = TextStyle(
    fontFamily = Lato,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp
)

