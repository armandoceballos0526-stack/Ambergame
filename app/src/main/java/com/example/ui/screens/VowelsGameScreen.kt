package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.VowelCatalog
import com.example.data.VowelItem
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.CandyPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.MagicPurple
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Educational Vowels Game for Amber (4 years old).
 * Prompt format: "¿Con qué letra empieza la palabra [palabra]?"
 * - Multiple rich variants for each vowel (A, E, I, O, U) that alternate.
 * - Positive reinforcement affirming the correct letter and congratulating Amber.
 * - Constructive, gentle error feedback when wrong.
 * - Continuous rounds: The dialog asking whether to continue or exit ONLY appears
 *   after each of the 5 vowels has passed exactly 2 times (10 rounds total).
 */
@Composable
fun VowelsGameScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteLevel: (stars: Int) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // 10-question sequence where each of the 5 vowels appears 2 times with different variants
    var roundQuestions by remember { mutableStateOf(VowelCatalog.generateTwoPassesRound()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isCelebrationVisible by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }
    var lastFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var isCorrectFeedback by remember { mutableStateOf(false) }

    val currentTarget = roundQuestions.getOrElse(currentQuestionIndex) { roundQuestions.first() }

    fun askCurrentVowel() {
        speechManager.speak("$childName, ¿con qué letra empieza la palabra ${currentTarget.word}?")
    }

    LaunchedEffect(currentQuestionIndex, roundQuestions) {
        lastFeedbackMessage = null
        isTransitioning = false
        askCurrentVowel()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFCE4EC), Color(0xFFF3E5F5), Color(0xFFE1F5FE))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header instruction & progress card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔤 Las Vocales Mágicas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MagicPurple
                        )

                        // 10 questions round indicator (each vowel 2 times)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CandyPink.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Pregunta ${currentQuestionIndex + 1} de 10",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyPink,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Progress dots (10 dots)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 10) {
                            val isPassed = i < currentQuestionIndex
                            val isCurrent = i == currentQuestionIndex
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isCurrent) 12.dp else 9.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> CoralOrange
                                            isPassed -> MintGreen
                                            else -> Color(0xFFCFD8DC)
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Central Big Illustration & Word Card
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big Animated Avatar Icon
                    Surface(
                        shape = CircleShape,
                        color = currentTarget.color.copy(alpha = 0.22f),
                        border = androidx.compose.foundation.BorderStroke(3.dp, currentTarget.color.copy(alpha = 0.5f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(118.dp)
                            .clickable {
                                soundFx.playPop()
                                askCurrentVowel()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = currentTarget.emoji,
                                fontSize = 66.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Word Display with first letter highlighted
                    val wordString = buildAnnotatedString {
                        if (currentTarget.word.isNotEmpty()) {
                            withStyle(
                                style = SpanStyle(
                                    color = currentTarget.color,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 32.sp
                                )
                            ) {
                                append(currentTarget.word.take(1))
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF263238),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 32.sp
                                )
                            ) {
                                append(currentTarget.word.drop(1))
                            }
                        }
                    }

                    Text(
                        text = wordString,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instruction prompt
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable { askCurrentVowel() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔊 ¿Con qué letra empieza la palabra ${currentTarget.word}?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Dynamic Feedback pill (Positive reinforcement or gentle error)
                    if (lastFeedbackMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCorrectFeedback) MintGreen.copy(alpha = 0.2f) else CoralOrange.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isCorrectFeedback) MintGreen else CoralOrange
                            )
                        ) {
                            Text(
                                text = lastFeedbackMessage!!,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCorrectFeedback) Color(0xFF2E7D32) else Color(0xFFD84315),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The 5 Vowels options: A, E, I, O, U (Chunky, tactile toddler buttons)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VowelCatalog.vowels.forEach { item ->
                    val isThisTarget = item.vowel.equals(currentTarget.vowel, ignoreCase = true)
                    val scale = remember { Animatable(1f) }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = item.color,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(86.dp)
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(2.5.dp, Color.White, RoundedCornerShape(22.dp))
                            .clickable(enabled = !isTransitioning) {
                                if (isThisTarget) {
                                    // CORRECT! Positive reinforcement: Affirms and congratulates
                                    isTransitioning = true
                                    isCorrectFeedback = true
                                    lastFeedbackMessage = "¡Sí, muy bien! ¡${currentTarget.word} empieza con la letra ${currentTarget.vowel}!"

                                    val isRoundFinished = currentQuestionIndex >= 9

                                    if (isRoundFinished) {
                                        soundFx.playStarFanfare()
                                        speechManager.speak("¡Increíble, $childName! ¡La palabra ${currentTarget.word} empieza con la ${currentTarget.vowel}! ¡Completaste todas las vocales dos veces!")
                                        onCompleteLevel(5)
                                        coroutineScope.launch {
                                            delay(1400)
                                            isCelebrationVisible = true
                                        }
                                    } else {
                                        soundFx.playSuccess()
                                        speechManager.speak("¡Sí, muy bien $childName! ¡${currentTarget.word} empieza con la letra ${currentTarget.vowel}! ¡Felicidades!")
                                        coroutineScope.launch {
                                            scale.animateTo(1.2f, tween(120))
                                            scale.animateTo(1f, tween(120))
                                            delay(1600)
                                            currentQuestionIndex++
                                        }
                                    }
                                } else {
                                    // INCORRECT: Gentle error response
                                    isCorrectFeedback = false
                                    lastFeedbackMessage = "¡Casi, $childName! Esa es la letra ${item.vowel}."
                                    soundFx.playGentleTryAgain()
                                    speechManager.speak("¡Casi, $childName! Esa es la letra ${item.vowel}. Intenta otra vez: ¿con qué letra empieza la palabra ${currentTarget.word}?")
                                    coroutineScope.launch {
                                        scale.animateTo(0.88f, tween(80))
                                        scale.animateTo(1.1f, tween(80))
                                        scale.animateTo(1f, tween(80))
                                    }
                                }
                            }
                            .scale(scale.value)
                            .testTag("vowel_choice_button_${item.vowel}")
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.vowel,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = item.emoji,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Celebration dialog: ONLY appears after each of the 5 vowels has passed 2 times (10 rounds total)
        if (isCelebrationVisible) {
            CelebrationOverlay(
                childName = childName,
                starsEarned = 5,
                titleText = "🌟 ¡VOCALES COMPLETADAS! 🌟",
                subtitleText = "¡Pasaste todas las vocales 2 veces con éxito!",
                continueButtonText = "¡Continuar Jugando!",
                exitButtonText = "Salir al Inicio",
                onPlayAgain = {
                    roundQuestions = VowelCatalog.generateTwoPassesRound()
                    currentQuestionIndex = 0
                    isCelebrationVisible = false
                },
                onGoHome = onGoHome
            )
        }
    }
}
