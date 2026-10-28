package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScreenRoute

/**
 * Modern, glossy 3D game badges designed specifically for Amber's game,
 * replacing plain unicode emojis with rich, multi-layered visual emblems.
 */
@Composable
fun ModernGameEmblem(
    route: ScreenRoute,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        modifier = modifier.size(64.dp)
    ) {
        when (route) {
            ScreenRoute.NUMBERS -> NumbersEmblem()
            ScreenRoute.SIZES_OPPOSITES -> SizesEmblem()
            ScreenRoute.VOWELS -> VowelsEmblem()
            ScreenRoute.SHAPES -> ShapesEmblem()
            ScreenRoute.MEMORY -> MemoryEmblem()
            ScreenRoute.PET_CARE -> PetCareEmblem()
            ScreenRoute.BUBBLES -> BubblesEmblem()
            ScreenRoute.MAGIC_PAINT -> PaintEmblem()
            ScreenRoute.XYLOPHONE -> XylophoneEmblem()
            ScreenRoute.STICKER_ALBUM -> StickersEmblem()
            else -> DefaultEmblem()
        }
    }
}

@Composable
private fun NumbersEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFB300), Color(0xFFFF6D00))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        // 3D Blocks representation
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(54.dp)) {
                // Block 1 (Blue)
                drawRoundRect(
                    color = Color(0xFF00B0FF),
                    topLeft = Offset(4f, 20f),
                    size = androidx.compose.ui.geometry.Size(26f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                // Block 2 (Green)
                drawRoundRect(
                    color = Color(0xFF00E676),
                    topLeft = Offset(24f, 4f),
                    size = androidx.compose.ui.geometry.Size(26f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
            Text(
                text = "123",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SizesEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Big sphere
            drawCircle(
                color = Color.White,
                radius = 18f,
                center = Offset(36f, 24f)
            )
            drawCircle(
                color = Color(0xFFFF4081),
                radius = 16f,
                center = Offset(36f, 24f)
            )
            // Small sphere
            drawCircle(
                color = Color.White,
                radius = 9f,
                center = Offset(14f, 34f)
            )
            drawCircle(
                color = Color(0xFFFFD600),
                radius = 7.5f,
                center = Offset(14f, 34f)
            )
        }
    }
}

@Composable
private fun VowelsEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF4081), Color(0xFFAB47BC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Text(
            text = "Aa",
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            color = Color.White
        )
    }
}

@Composable
private fun ShapesEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF00B0FF), Color(0xFF651FFF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(48.dp)) {
            // Star
            drawStar(
                cx = 28f,
                cy = 20f,
                outerR = 18f,
                color = Color(0xFFFFD600)
            )
            // Heart
            drawHeart(
                cx = 16f,
                cy = 30f,
                size = 14f,
                color = Color(0xFFFF4081)
            )
        }
    }
}

@Composable
private fun MemoryEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFAB47BC), Color(0xFF512DA8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Card 1
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(8f, 6f),
                size = androidx.compose.ui.geometry.Size(26f, 36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
            // Card 2 (top)
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(16f, 10f),
                size = androidx.compose.ui.geometry.Size(26f, 36f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
            drawCircle(
                color = Color(0xFFFF4081),
                radius = 6f,
                center = Offset(29f, 28f)
            )
        }
    }
}

@Composable
private fun PetCareEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF80AB), Color(0xFFFF4081))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Cute baby pet face silhouette with ears
            // Left ear
            val earPath1 = Path().apply {
                moveTo(14f, 18f)
                lineTo(18f, 4f)
                lineTo(24f, 18f)
                close()
            }
            drawPath(earPath1, Color(0xFFFFD54F))
            // Right ear
            val earPath2 = Path().apply {
                moveTo(26f, 18f)
                lineTo(32f, 4f)
                lineTo(36f, 18f)
                close()
            }
            drawPath(earPath2, Color(0xFFFFD54F))
            // Head
            drawCircle(
                color = Color.White,
                radius = 16f,
                center = Offset(25f, 28f)
            )
            // Eyes
            drawCircle(Color(0xFF263238), 2.5f, Offset(20f, 26f))
            drawCircle(Color(0xFF263238), 2.5f, Offset(30f, 26f))
            // Heart nose
            drawCircle(Color(0xFFFF4081), 2f, Offset(25f, 31f))
        }
    }
}

@Composable
private fun BubblesEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF00E5FF), Color(0xFF0091EA))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Large bubble
            drawCircle(
                color = Color.White.copy(alpha = 0.65f),
                radius = 16f,
                center = Offset(22f, 22f)
            )
            drawCircle(
                color = Color(0x66EA80FC),
                radius = 14f,
                center = Offset(22f, 22f)
            )
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(17f, 17f)
            )
            // Small bubble
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = 8f,
                center = Offset(36f, 34f)
            )
        }
    }
}

@Composable
private fun PaintEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF6D00), Color(0xFFFFD600))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Palette oval
            drawOval(
                color = Color.White,
                topLeft = Offset(6f, 8f),
                size = androidx.compose.ui.geometry.Size(38f, 34f)
            )
            // Paint spots
            drawCircle(Color(0xFFFF1744), 3.5f, Offset(16f, 18f))
            drawCircle(Color(0xFF00E676), 3.5f, Offset(25f, 15f))
            drawCircle(Color(0xFF00B0FF), 3.5f, Offset(34f, 22f))
            drawCircle(Color(0xFFAA00FF), 3.5f, Offset(22f, 32f))
        }
    }
}

@Composable
private fun XylophoneEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF7E57C2), Color(0xFFD500F9))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            // Rainbow bars
            val colors = listOf(Color(0xFFFF1744), Color(0xFFFF9100), Color(0xFFFFEA00), Color(0xFF00E676), Color(0xFF00B0FF))
            colors.forEachIndexed { i, col ->
                val x = 8f + i * 7f
                val h = 34f - i * 4f
                val y = 25f - h / 2f
                drawRoundRect(
                    color = col,
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(5.5f, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                )
            }
        }
    }
}

@Composable
private fun StickersEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFD600), Color(0xFFFF8F00))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Canvas(modifier = Modifier.size(50.dp)) {
            drawStar(
                cx = 25f,
                cy = 25f,
                outerR = 19f,
                color = Color.White
            )
            drawStar(
                cx = 25f,
                cy = 25f,
                outerR = 16f,
                color = Color(0xFFFF6D00)
            )
        }
    }
}

@Composable
private fun DefaultEmblem() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFFF4081)),
        contentAlignment = Alignment.Center
    ) {
        GlossyOverlay()
        Text(text = "⭐", fontSize = 24.sp)
    }
}

@Composable
private fun GlossyOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )
    )
}

private fun DrawScope.drawStar(cx: Float, cy: Float, outerR: Float, color: Color) {
    val path = Path()
    val innerR = outerR * 0.42f
    var angle = -Math.PI / 2.0
    val step = Math.PI / 5.0

    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val x = cx + (r * Math.cos(angle)).toFloat()
        val y = cy + (r * Math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        angle += step
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, cy + size * 0.7f)
        cubicTo(cx - size, cy, cx - size, cy - size * 0.8f, cx, cy - size * 0.2f)
        cubicTo(cx + size, cy - size * 0.8f, cx + size, cy, cx, cy + size * 0.7f)
        close()
    }
    drawPath(path, color)
}
