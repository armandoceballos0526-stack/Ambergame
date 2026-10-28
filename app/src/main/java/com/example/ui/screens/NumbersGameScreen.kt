package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.AnimalCatalog
import com.example.data.DifficultyLevel
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.CandyPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun NumbersGameScreen(
    childName: String,
    difficulty: DifficultyLevel,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteLevel: (stars: Int) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val maxCount = difficulty.maxNumber.coerceIn(3, 10)

    // Consecutive correct answers counter (up to 5 per set)
    var correctAnswersInBatch by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(Random.nextInt(1, maxCount + 1)) }
    var currentAnimal by remember { mutableStateOf(AnimalCatalog.animals.random()) }
    val tappedIndices = remember { mutableStateListOf<Int>() }
    var isCelebrationVisible by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }

    // Generate 3 choices including targetCount
    val numberChoices = remember(targetCount, maxCount) {
        val pool = (1..maxCount).filter { it != targetCount }.shuffled().take(2)
        (pool + targetCount).shuffled()
    }

    fun promptQuestion() {
        val animalNamePrompt = currentAnimal.pluralName
        speechManager.speak("$childName, ¿cuántos $animalNamePrompt hay? Tócalos para contar.")
    }

    fun startNextQuestion() {
        var newCount = Random.nextInt(1, maxCount + 1)
        while (newCount == targetCount && maxCount > 1) {
            newCount = Random.nextInt(1, maxCount + 1)
        }
        val pool = AnimalCatalog.animals.filter { it.id != currentAnimal.id }
        currentAnimal = pool.random()
        targetCount = newCount
        tappedIndices.clear()
        isTransitioning = false
        promptQuestion()
    }

    LaunchedEffect(Unit) {
        promptQuestion()
    }

    val spanishNumbers = listOf(
        "Cero", "Uno", "Dos", "Tres", "Cuatro",
        "Cinco", "Seis", "Siete", "Ocho", "Nueve", "Diez"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF9C4), Color(0xFFFFF3E0), Color(0xFFFFF0F5))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Instruction Card with batch progress (1 to 5)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                            text = "🔢 Pregunta ${correctAnswersInBatch + 1} de 5",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralOrange
                        )

                        // 5 stars progress bar for current set
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) { starIndex ->
                                val isDone = starIndex < correctAnswersInBatch
                                Text(
                                    text = if (isDone) "⭐" else "☆",
                                    fontSize = 18.sp,
                                    color = if (isDone) Color.Unspecified else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Mini animal preview badge
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = currentAnimal.color.copy(alpha = 0.25f),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val animResId = currentAnimal.drawableResId
                                if (animResId != null) {
                                    Image(
                                        painter = painterResource(id = animResId),
                                        contentDescription = currentAnimal.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(18.dp))
                                    )
                                } else {
                                    Text(text = currentAnimal.emoji, fontSize = 28.sp)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "¡$childName, cuenta cuántos ${currentAnimal.pluralName} hay!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = "Toca cada ${currentAnimal.simpleName} y luego elige el número abajo",
                                fontSize = 12.sp,
                                color = Color(0xFF546E7A)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animals counting grid area with 3D icon cards
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .border(2.dp, CoralOrange.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val columns = if (targetCount <= 4) 2 else if (targetCount <= 6) 3 else 4
                val itemSize = when {
                    targetCount <= 4 -> 96.dp
                    targetCount <= 6 -> 82.dp
                    else -> 70.dp
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(List(targetCount) { it }) { index, _ ->
                        val isTapped = tappedIndices.contains(index)
                        val orderTapped = if (isTapped) tappedIndices.indexOf(index) + 1 else 0

                        val scale = remember { Animatable(1f) }

                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = if (isTapped) Color(0xFFFFF8E1) else Color.White,
                            shadowElevation = if (isTapped) 8.dp else 4.dp,
                            border = if (isTapped) {
                                BorderStroke(3.dp, Brush.linearGradient(listOf(Color(0xFFFFB300), Color(0xFFFF5722))))
                            } else {
                                BorderStroke(2.dp, currentAnimal.color.copy(alpha = 0.5f))
                            },
                            modifier = Modifier
                                .size(itemSize)
                                .scale(scale.value)
                                .clip(RoundedCornerShape(22.dp))
                                .clickable {
                                    if (!isTapped) {
                                        tappedIndices.add(index)
                                        val countSoFar = tappedIndices.size
                                        soundFx.playPop()
                                        speechManager.speak(spanishNumbers.getOrElse(countSoFar) { "$countSoFar" })
                                        coroutineScope.launch {
                                            scale.animateTo(1.22f, tween(110))
                                            scale.animateTo(1f, tween(110))
                                        }
                                    } else {
                                        soundFx.playTap()
                                    }
                                }
                                .testTag("animal_count_item_$index")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val itemResId = currentAnimal.drawableResId
                                if (itemResId != null) {
                                    Image(
                                        painter = painterResource(id = itemResId),
                                        contentDescription = currentAnimal.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(if (targetCount <= 4) 4.dp else 2.dp)
                                            .clip(RoundedCornerShape(19.dp))
                                    )
                                } else {
                                    Text(
                                        text = currentAnimal.emoji,
                                        fontSize = if (targetCount <= 4) 44.sp else 34.sp
                                    )
                                }

                                // Number badge over touched animal
                                if (isTapped) {
                                    Surface(
                                        shape = CircleShape,
                                        color = CoralOrange,
                                        shadowElevation = 4.dp,
                                        border = BorderStroke(1.5.dp, Color.White),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(if (targetCount <= 4) 30.dp else 26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$orderTapped",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = if (targetCount <= 4) 15.sp else 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question prompt
            Text(
                text = "¿Cuántos hay en total?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF37474F)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Number choice buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val buttonColors = listOf(CandyPink, SkyBlue, MintGreen)
                numberChoices.forEachIndexed { i, choice ->
                    val btnColor = buttonColors.getOrElse(i) { CoralOrange }
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = btnColor,
                        shadowElevation = 8.dp,
                        border = BorderStroke(3.dp, Color.White.copy(alpha = 0.9f)),
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .clickable(enabled = !isTransitioning) {
                                if (choice == targetCount) {
                                    isTransitioning = true
                                    onCompleteLevel(1)
                                    val nextBatchCount = correctAnswersInBatch + 1
                                    correctAnswersInBatch = nextBatchCount

                                    val singleOrPlural = if (targetCount == 1) currentAnimal.simpleName else currentAnimal.pluralName

                                    if (nextBatchCount >= 5) {
                                        // Completed 5 questions! Show celebration with continue or exit
                                        soundFx.playStarFanfare()
                                        speechManager.speak("¡Excelente, $childName! ¡Completaste las 5 preguntas!")
                                        isCelebrationVisible = true
                                        isTransitioning = false
                                    } else {
                                        // Continuous round: Praise and smoothly advance to next question
                                        soundFx.playSuccess()
                                        speechManager.speak("¡Muy bien, $childName! ¡Hay $targetCount $singleOrPlural!")
                                        coroutineScope.launch {
                                            delay(1300)
                                            startNextQuestion()
                                        }
                                    }
                                } else {
                                    soundFx.playGentleTryAgain()
                                    speechManager.speak("¡Casi, $childName! Cuenta de nuevo tocando cada uno.")
                                }
                            }
                            .testTag("number_choice_btn_$choice")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$choice",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Celebration dialog after completing 5 rounds
        if (isCelebrationVisible) {
            CelebrationOverlay(
                childName = childName,
                starsEarned = 5,
                titleText = "🌟 ¡5 PREGUNTAS COMPLETADAS! 🌟",
                subtitleText = "¡Lograste 5 respuestas correctas consecutivas!",
                continueButtonText = "¡Continuar Jugando!",
                exitButtonText = "Salir al Inicio",
                onPlayAgain = {
                    correctAnswersInBatch = 0
                    isCelebrationVisible = false
                    startNextQuestion()
                },
                onGoHome = onGoHome
            )
        }
    }
}
