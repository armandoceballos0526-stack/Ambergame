package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.OppositeCatalog
import com.example.data.OppositeItem
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.CandyPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SizesGameScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteLevel: (stars: Int) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentBatchCount by remember { mutableIntStateOf(0) }
    var currentChallenge by remember { mutableStateOf(OppositeCatalog.challenges.random()) }
    var isCelebrationVisible by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }

    // Randomize whether Big is on the Left or Right
    var bigOnLeft by remember { mutableStateOf(kotlin.random.Random.nextBoolean()) }

    fun promptVoice() {
        val question = currentChallenge.questionVoice.replace("Amber", childName)
        speechManager.speak(question)
    }

    fun startNextQuestion() {
        val remaining = OppositeCatalog.challenges.filter { it.id != currentChallenge.id }
        currentChallenge = remaining.random()
        bigOnLeft = kotlin.random.Random.nextBoolean()
        isTransitioning = false
        promptVoice()
    }

    LaunchedEffect(currentBatchCount) {
        promptVoice()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Real game landscape wallpaper
        Image(
            painter = painterResource(id = R.drawable.img_game_background),
            contentDescription = "Fondo de juego",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Soft frosted overlay so contents pop with high contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.5f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header instruction card with batch progress
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📏 Pregunta ${currentBatchCount + 1} de 5",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralOrange
                        )

                        // 5 Stars indicator
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) { i ->
                                val isDone = i < currentBatchCount
                                Text(
                                    text = if (isDone) "⭐" else "☆",
                                    fontSize = 18.sp,
                                    color = if (isDone) Color.Unspecified else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentChallenge.promptText.replace("Amber", childName),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Two Size Comparison Cards (Large vs Small)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leftIsBig = bigOnLeft
                val rightIsBig = !bigOnLeft

                // Left Option Card
                SizeOptionCard(
                    isBig = leftIsBig,
                    emoji = currentChallenge.emoji,
                    targetIsBig = currentChallenge.targetIsBig,
                    childName = childName,
                    soundFx = soundFx,
                    speechManager = speechManager,
                    isTransitioning = isTransitioning,
                    onCorrect = {
                        isTransitioning = true
                        onCompleteLevel(1)
                        val nextCount = currentBatchCount + 1
                        currentBatchCount = nextCount

                        if (nextCount >= 5) {
                            soundFx.playStarFanfare()
                            speechManager.speak("¡Increíble, $childName! ¡Completaste las 5 preguntas de tamaños!")
                            isCelebrationVisible = true
                            isTransitioning = false
                        } else {
                            soundFx.playSuccess()
                            speechManager.speak(currentChallenge.successVoice.replace("Amber", childName))
                            coroutineScope.launch {
                                delay(1300)
                                startNextQuestion()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("size_option_left")
                )

                // Right Option Card
                SizeOptionCard(
                    isBig = rightIsBig,
                    emoji = currentChallenge.emoji,
                    targetIsBig = currentChallenge.targetIsBig,
                    childName = childName,
                    soundFx = soundFx,
                    speechManager = speechManager,
                    isTransitioning = isTransitioning,
                    onCorrect = {
                        isTransitioning = true
                        onCompleteLevel(1)
                        val nextCount = currentBatchCount + 1
                        currentBatchCount = nextCount

                        if (nextCount >= 5) {
                            soundFx.playStarFanfare()
                            speechManager.speak("¡Increíble, $childName! ¡Completaste las 5 preguntas de tamaños!")
                            isCelebrationVisible = true
                            isTransitioning = false
                        } else {
                            soundFx.playSuccess()
                            speechManager.speak(currentChallenge.successVoice.replace("Amber", childName))
                            coroutineScope.launch {
                                delay(1300)
                                startNextQuestion()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("size_option_right")
                )
            }
        }

        // 5-Round Celebration Overlay
        if (isCelebrationVisible) {
            CelebrationOverlay(
                childName = childName,
                starsEarned = 5,
                titleText = "🌟 ¡5 PREGUNTAS COMPLETADAS! 🌟",
                subtitleText = "¡Eres una experta en tamaños y opuestos!",
                continueButtonText = "¡Continuar Jugando!",
                exitButtonText = "Salir al Inicio",
                onPlayAgain = {
                    currentBatchCount = 0
                    isCelebrationVisible = false
                    startNextQuestion()
                },
                onGoHome = onGoHome
            )
        }
    }
}

@Composable
private fun SizeOptionCard(
    isBig: Boolean,
    emoji: String,
    targetIsBig: Boolean,
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    isTransitioning: Boolean,
    onCorrect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }
    val isCorrectChoice = isBig == targetIsBig

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = modifier
            .height(260.dp)
            .clip(RoundedCornerShape(32.dp))
            .border(
                width = 3.dp,
                color = if (isBig) CandyPink.copy(alpha = 0.4f) else SkyBlue.copy(alpha = 0.4f),
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(enabled = !isTransitioning) {
                if (isCorrectChoice) {
                    coroutineScope.launch {
                        scaleAnim.animateTo(1.25f, tween(120, easing = FastOutSlowInEasing))
                        scaleAnim.animateTo(1f, tween(150))
                    }
                    onCorrect()
                } else {
                    soundFx.playGentleTryAgain()
                    val targetWord = if (targetIsBig) "más grande" else "más pequeño"
                    speechManager.speak("¡Casi $childName! Busca el que es $targetWord.")
                    coroutineScope.launch {
                        scaleAnim.animateTo(0.9f, tween(100))
                        scaleAnim.animateTo(1f, tween(100))
                    }
                }
            }
            .scale(scaleAnim.value)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Visual element with dramatic size difference for 4-year-olds
            Text(
                text = emoji,
                fontSize = if (isBig) 88.sp else 38.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isBig) CandyPink.copy(alpha = 0.18f) else SkyBlue.copy(alpha = 0.18f)
            ) {
                Text(
                    text = if (isBig) "GRANDE" else "PEQUEÑO",
                    fontSize = if (isBig) 16.sp else 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isBig) CandyPink else SkyBlue,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
