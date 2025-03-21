package com.xdroid.app.changewallpaper.ui.components


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val GooglePlay: ImageVector
    get() {
        if (_GooglePlay != null) {
            return _GooglePlay!!
        }
        _GooglePlay = ImageVector.Builder(
            name = "GooglePlay",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.222f, 9.374f)
                curveToRelative(1.037f, -0.61f, 1.037f, -2.137f, 0f, -2.748f)
                lineTo(11.528f, 5.04f)
                lineTo(8.32f, 8f)
                lineToRelative(3.207f, 2.96f)
                close()
                moveToRelative(-3.595f, 2.116f)
                lineTo(7.583f, 8.68f)
                lineTo(1.03f, 14.73f)
                curveToRelative(0.201f, 1.029f, 1.36f, 1.61f, 2.303f, 1.055f)
                close()
                moveTo(1f, 13.396f)
                verticalLineTo(2.603f)
                lineTo(6.846f, 8f)
                close()
                moveTo(1.03f, 1.27f)
                lineToRelative(6.553f, 6.05f)
                lineToRelative(3.044f, -2.81f)
                lineTo(3.333f, 0.215f)
                curveTo(2.39f, -0.341f, 1.231f, 0.24f, 1.03f, 1.27f)
            }
        }.build()
        return _GooglePlay!!
    }

private var _GooglePlay: ImageVector? = null
