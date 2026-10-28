package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CandyPink
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.StarGold
import kotlin.random.Random

private data class ConfettiParticle(
    val xRatio: Float,
    val initialSpeed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun CelebrationOverlay(
    childName: String,
    starsEarned: Int = 1,
    titleText: String = "🌟 ¡FELICIDADES! 🌟",
    subtitleText: String? = null,
    continueButtonText: String = "¡Continuar Jugando!",
    exitButtonText: String = "Salir al Inicio",
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    val scaleAnim = remember { Animatable(0f) }
    val confettiAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(450, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(200)
        )
    }

    LaunchedEffect(Unit) {
        confettiAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFF1744), Color(0xFFFF9100), Color(0xFFFFEA00),
            Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFFD500F9)
        )
        List(40) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                initialSpeed = Random.nextFloat() * 0.7f + 0.3f,
                size = Random.nextFloat() * 12f + 8f,
                color = colors.random()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(enabled = false) {}, // absorb clicks
        contentAlignment = Alignment.Center
    ) {
        // Confetti Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            particles.forEach { p ->
                val y = ((confettiAnim.value * p.initialSpeed * 1.5f) % 1f) * (h + 50f) - 30f
                val x = p.xRatio * w + kotlin.math.sin(confettiAnim.value * 6.28 + p.xRatio * 10).toFloat() * 20f
                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = Offset(x, y)
                )
            }
        }

        // Celebration Card
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.92f)
                .scale(scaleAnim.value)
                .testTag("celebration_overlay")
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = CandyPink,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "¡Muy bien, $childName!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF37474F),
                    textAlign = TextAlign.Center
                )

                if (subtitleText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitleText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00897B),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bouncing trophy/reward row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(starsEarned.coerceAtLeast(1).coerceAtMost(5)) {
                        Text(
                            text = "⭐",
                            fontSize = 38.sp,
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¡Ganaste $starsEarned estrella${if (starsEarned > 1) "s" else ""} mágica${if (starsEarned > 1) "s" else ""}!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons: Continuar o Salir
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = CandyPink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { onPlayAgain() }
                            .testTag("celebration_continue_button"),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "▶️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = continueButtonText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = SkyBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable { onGoHome() }
                            .testTag("celebration_exit_button"),
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏠", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = exitButtonText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
