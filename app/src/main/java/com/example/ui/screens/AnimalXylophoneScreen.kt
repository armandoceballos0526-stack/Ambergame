package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.ui.theme.CandyPink
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class XyloKey(
    val noteName: String,
    val animalEmoji: String,
    val color: Color,
    val heightRatio: Float
)

@Composable
fun AnimalXylophoneScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val keys = remember {
        listOf(
            XyloKey("DO", "🐱", Color(0xFFFF5252), 1.0f),
            XyloKey("RE", "🐶", Color(0xFFFF9100), 0.95f),
            XyloKey("MI", "🦆", Color(0xFFFFD600), 0.90f),
            XyloKey("FA", "🐸", Color(0xFF00E676), 0.85f),
            XyloKey("SOL", "🐼", Color(0xFF00B0FF), 0.80f),
            XyloKey("LA", "🐻", Color(0xFF3D5AFE), 0.75f),
            XyloKey("SI", "🦄", Color(0xFFAA00FF), 0.70f),
            XyloKey("DO", "🦁", Color(0xFFFF4081), 0.65f)
        )
    }

    var highlightedIndex by remember { mutableStateOf<Int?>(null) }
    var isAutoPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        speechManager.speak("¡Amber, toca las teclas del xilófono para hacer música y ver a los animalitos saltar!")
    }

    fun playNurseryRhyme() {
        if (isAutoPlaying) return
        isAutoPlaying = true
        coroutineScope.launch {
            // Estrellita: Do Do Sol Sol La La Sol
            val melody = listOf(0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0)
            for (note in melody) {
                highlightedIndex = note
                soundFx.playXylophoneNote(note)
                delay(380)
            }
            highlightedIndex = null
            isAutoPlaying = false
            speechManager.speak("¡Bravo, $childName! ¡Qué hermosa canción!")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEDE7F6), Color(0xFFFFF0F5), Color(0xFFE1F5FE))
                )
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Card with Play Song Demo Button
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎵 Xilófono de Animales",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF673AB7)
                    )
                    Text(
                        text = "¡$childName, crea tus propias melodías!",
                        fontSize = 12.sp,
                        color = Color(0xFF546E7A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SunnyYellow,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .clickable(enabled = !isAutoPlaying) { playNurseryRhyme() }
                        .testTag("play_melody_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎶", fontSize = 16.sp)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = if (isAutoPlaying) "Tocando..." else "Canción",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Xylophone Keys Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            keys.forEachIndexed { index, key ->
                val isHighlighted = highlightedIndex == index
                val scale = remember { Animatable(1f) }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Dancing Animal above key
                    Box(
                        modifier = Modifier
                            .scale(if (isHighlighted) 1.35f else scale.value)
                            .size(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = key.animalEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rainbow Bar
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = key.color,
                        shadowElevation = if (isHighlighted) 10.dp else 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(key.heightRatio)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                soundFx.playXylophoneNote(index)
                                coroutineScope.launch {
                                    scale.animateTo(1.3f, tween(100))
                                    scale.animateTo(1f, tween(150))
                                }
                            }
                            .testTag("xylo_key_$index")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⚪",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = key.noteName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "⚪",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
