package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.ShapeCatalog
import com.example.data.ShapeColorItem
import com.example.data.ShapeType
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.CandyPink
import com.example.ui.theme.SkyBlue
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShapesGameScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteLevel: (stars: Int) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentRound by remember { mutableIntStateOf(1) }
    var targetItem by remember { mutableStateOf(ShapeCatalog.sampleShapes.random()) }
    var isCelebrationVisible by remember { mutableStateOf(false) }

    val choices = remember(targetItem, currentRound) {
        val pool = ShapeCatalog.sampleShapes.filter { it != targetItem }.shuffled().take(3)
        (pool + targetItem).shuffled()
    }

    fun promptTarget() {
        speechManager.speak("$childName, ¿dónde está el ${targetItem.shape.spanishName} ${targetItem.colorName}?")
    }

    LaunchedEffect(currentRound) {
        promptTarget()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE1F5FE), Color(0xFFF3E5F5), Color(0xFFFFF0F5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔶 Figuras y Colores Mágicos",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¡$childName, busca el ${targetItem.shape.spanishName} ${targetItem.colorName}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Silhouette Reference Display
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size(130.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(90.dp)) {
                        drawGeometricShape(targetItem.shape, targetItem.color)
                    }
                    Text(text = "👀", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4 Big Shape Options in a 2x2 grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .navigationBarsPadding()
            ) {
                items(choices) { item ->
                    val isCorrect = item == targetItem
                    val scale = remember { Animatable(1f) }

                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White,
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .height(130.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .clickable {
                                if (isCorrect) {
                                    soundFx.playStarFanfare()
                                    speechManager.speak("¡Excelente, $childName! ¡Encontraste el ${item.shape.spanishName} ${item.colorName}!")
                                    isCelebrationVisible = true
                                    onCompleteLevel(1)
                                } else {
                                    soundFx.playGentleTryAgain()
                                    speechManager.speak("Ese es un ${item.shape.spanishName} ${item.colorName}. Busca el ${targetItem.shape.spanishName} ${targetItem.colorName}.")
                                    coroutineScope.launch {
                                        scale.animateTo(1.15f, tween(100))
                                        scale.animateTo(1f, tween(100))
                                    }
                                }
                            }
                            .scale(scale.value)
                            .testTag("shape_option_${item.shape.name}_${item.colorName}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.size(68.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(64.dp)) {
                                    drawGeometricShape(item.shape, item.color)
                                }
                                Text(text = "✨", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${item.shape.spanishName}\n${item.colorName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (isCelebrationVisible) {
            CelebrationOverlay(
                childName = childName,
                starsEarned = 1,
                onPlayAgain = {
                    val pool = ShapeCatalog.sampleShapes.filter { it != targetItem }
                    targetItem = pool.random()
                    currentRound++
                    isCelebrationVisible = false
                },
                onGoHome = onGoHome
            )
        }
    }
}

private fun DrawScope.drawGeometricShape(shape: ShapeType, color: Color) {
    val w = size.width
    val h = size.height
    when (shape) {
        ShapeType.CIRCULO -> {
            drawCircle(color = color, radius = w * 0.45f, center = Offset(w / 2f, h / 2f))
        }
        ShapeType.CUADRADO -> {
            val pad = w * 0.1f
            drawRoundRect(
                color = color,
                topLeft = Offset(pad, pad),
                size = Size(w - pad * 2, h - pad * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
        }
        ShapeType.TRIANGULO -> {
            val path = Path().apply {
                moveTo(w / 2f, h * 0.08f)
                lineTo(w * 0.92f, h * 0.92f)
                lineTo(w * 0.08f, h * 0.92f)
                close()
            }
            drawPath(path = path, color = color)
        }
        ShapeType.ESTRELLA -> {
            val path = Path()
            val cx = w / 2f
            val cy = h / 2f
            val outerRadius = w * 0.48f
            val innerRadius = outerRadius * 0.42f
            val points = 5
            var angle = -PI / 2.0
            val step = PI / points

            for (i in 0 until points * 2) {
                val r = if (i % 2 == 0) outerRadius else innerRadius
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                angle += step
            }
            path.close()
            drawPath(path = path, color = color)
        }
        ShapeType.CORAZON -> {
            val path = Path().apply {
                val cx = w / 2f
                val cy = h * 0.38f
                moveTo(cx, h * 0.88f)
                // Left cubic
                cubicTo(w * 0.05f, h * 0.55f, w * 0.05f, h * 0.15f, cx, cy)
                // Right cubic
                cubicTo(w * 0.95f, h * 0.15f, w * 0.95f, h * 0.55f, cx, h * 0.88f)
                close()
            }
            drawPath(path = path, color = color)
        }
    }
}
