package ru.tech.imageresizershrinker.presentation.root.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.LineArrow: ImageVector by lazy {
    Builder(
        name = "LineArrow", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(680.0f, 336.0f)
            lineTo(244.0f, 772.0f)
            quadToRelative(-11.0f, 11.0f, -28.0f, 11.0f)
            reflectiveQuadToRelative(-28.0f, -11.0f)
            quadToRelative(-11.0f, -11.0f, -11.0f, -28.0f)
            reflectiveQuadToRelative(11.0f, -28.0f)
            lineToRelative(436.0f, -436.0f)
            lineTo(400.0f, 280.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(360.0f, 240.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(400.0f, 200.0f)
            horizontalLineToRelative(320.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(760.0f, 240.0f)
            verticalLineToRelative(320.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(720.0f, 600.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(680.0f, 560.0f)
            verticalLineToRelative(-224.0f)
            close()
        }
    }
        .build()
}