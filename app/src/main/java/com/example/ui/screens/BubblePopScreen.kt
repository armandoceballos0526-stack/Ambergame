package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.ui.theme.CandyPink
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

private data class FloatingBubble(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float,
    val speed: Float,
    val color: Color,
    val surpriseEmoji: String,
    var isPopped: Boolean = false,
    var popAnimProgress: Float = 0f
)

@Composable
fun BubblePopScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onBubblesPopped: (count: Int) -> Unit,
    onEarnStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var poppedCount by remember { mutableIntStateOf(0) }
    val bubbles = remember { mutableStateListOf<FloatingBubble>() }

    val surprises = listOf("⭐", "🍭", "🧸", "🦋", "🌸", "🦄", "🌈", "🎈", "🐱", "✨")
    val bubbleColors = listOf(
        Color(0x88FF80AB), Color(0x8880D8FF), Color(0x88B9F6CA),
        Color(0x88FFFF8D), Color(0x88EA80FC), Color(0x88FFD180)
    )

    LaunchedEffect(Unit) {
        speechManager.speak("¡Amber, explota todas las burbujas y globos con tu dedito!")
    }

    // Game loop that floats bubbles up and spawns new ones
    LaunchedEffect(Unit) {
        var nextId = 1L
        while (true) {
            delay(16) // ~60fps tick

            // Spawn if less than 8 bubbles
            if (bubbles.size < 8 && Random.nextFloat() < 0.15f) {
                bubbles.add(
                    FloatingBubble(
                        id = nextId++,
                        x = Random.nextFloat(), // ratio 0..1
                        y = 1.15f,              // start below screen
                        radius = Random.nextFloat() * 25f + 45f,
                        speed = Random.nextFloat() * 0.0035f + 0.002f,
                        color = bubbleColors.random(),
                        surpriseEmoji = surprises.random()
                    )
                )
            }

            // Update positions
            val iterator = bubbles.listIterator()
            while (iterator.hasNext()) {
                val b = iterator.next()
                if (b.isPopped) {
                    b.popAnimProgress += 0.12f
                    if (b.popAnimProgress >= 1f) {
                        iterator.remove()
                    }
                } else {
                    b.y -= b.speed
                    if (b.y < -0.15f) {
                        // Flew off top
                        iterator.remove()
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE0F7FA), Color(0xFFE8F5E9), Color(0xFFFFF8E1))
                )
            )
            .testTag("bubble_pop_canvas_area")
    ) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()

        // Tap listener for whole area to pop nearby bubbles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        var hit = false
                        for (b in bubbles.filter { !it.isPopped }) {
                            val bx = b.x * w
                            val by = b.y * h
                            val dist = hypot(tapOffset.x - bx, tapOffset.y - by)
                            if (dist <= b.radius * 1.35f) { // Generous toddler tap radius
                                b.isPopped = true
                                hit = true
                                poppedCount++
                                soundFx.playPop()
                                onBubblesPopped(1)

                                if (poppedCount % 10 == 0) {
                                    soundFx.playSuccess()
                                    onEarnStar()
                                    speechManager.speak("¡Genial $childName! ¡10 burbujas explotadas, ganaste una estrella!")
                                }
                                break
                            }
                        }
                        if (!hit) {
                            soundFx.playTap()
                        }
                    }
                }
        ) {
            // Render bubbles on Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                bubbles.forEach { b ->
                    val cx = b.x * w
                    val cy = b.y * h
                    if (!b.isPopped) {
                        // Outer glowing bubble
                        drawCircle(
                            color = b.color,
                            radius = b.radius,
                            center = Offset(cx, cy)
                        )
                        // Shiny highlight reflection
                        drawCircle(
                            color = Color.White.copy(alpha = 0.65f),
                            radius = b.radius * 0.28f,
                            center = Offset(cx - b.radius * 0.35f, cy - b.radius * 0.35f)
                        )
                    } else {
                        // Pop explosion particles
                        val p = b.popAnimProgress
                        val expandRadius = b.radius * (1f + p * 0.6f)
                        val alpha = (1f - p).coerceIn(0f, 1f)
                        drawCircle(
                            color = b.color.copy(alpha = alpha * 0.5f),
                            radius = expandRadius,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            val density = androidx.compose.ui.platform.LocalDensity.current
            // Render emojis inside bubbles using standard text overlays
            bubbles.forEach { b ->
                if (!b.isPopped) {
                    val xDp = with(density) { (b.x * w - 18f).toDp() }
                    val yDp = with(density) { (b.y * h - 18f).toDp() }
                    Box(
                        modifier = Modifier
                            .padding(start = xDp.coerceAtLeast(0.dp), top = yDp.coerceAtLeast(0.dp))
                    ) {
                        Text(text = b.surpriseEmoji, fontSize = 28.sp)
                    }
                }
            }
        }

        // Top counter info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🫧", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Burbujas: $poppedCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00897B)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Pop!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandyPink
                    )
                }
            }
        }
    }
}
