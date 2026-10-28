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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.Animal
import com.example.data.AnimalCatalog
import com.example.data.DifficultyLevel
import com.example.ui.components.CelebrationOverlay
import com.example.ui.theme.CandyPink
import com.example.ui.theme.MagicPurple
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class MemoryCard(
    val id: Int,
    val animal: Animal,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryGameScreen(
    childName: String,
    difficulty: DifficultyLevel,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteLevel: (stars: Int) -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pairsCount = difficulty.memoryPairs.coerceIn(2, 4)

    val cards = remember(pairsCount) {
        val selectedAnimals = AnimalCatalog.animals.shuffled().take(pairsCount)
        val cardList = mutableListOf<MemoryCard>()
        selectedAnimals.forEachIndexed { i, animal ->
            cardList.add(MemoryCard(id = i * 2, animal = animal))
            cardList.add(MemoryCard(id = i * 2 + 1, animal = animal))
        }
        mutableStateListOf<MemoryCard>().apply { addAll(cardList.shuffled()) }
    }

    var selectedFirst by remember { mutableStateOf<Int?>(null) }
    var selectedSecond by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }
    var isCelebrationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        speechManager.speak("$childName, voltea las cartas y encuentra los animalitos iguales.")
    }

    fun resetGame() {
        val selectedAnimals = AnimalCatalog.animals.shuffled().take(pairsCount)
        val cardList = mutableListOf<MemoryCard>()
        selectedAnimals.forEachIndexed { i, animal ->
            cardList.add(MemoryCard(id = i * 2, animal = animal))
            cardList.add(MemoryCard(id = i * 2 + 1, animal = animal))
        }
        cards.clear()
        cards.addAll(cardList.shuffled())
        selectedFirst = null
        selectedSecond = null
        isBusy = false
        isCelebrationVisible = false
        speechManager.speak("¡Nueva partida, $childName! Encuentra todas las parejitas.")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF3E5F5), Color(0xFFFFF0F5), Color(0xFFE8F5E9))
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
                        text = "🃏 Memoria de Animales",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MagicPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¡$childName, busca las parejitas!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Memory cards grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .navigationBarsPadding()
            ) {
                itemsIndexed(cards) { index, card ->
                    val isRevealed = card.isFaceUp || card.isMatched
                    val scale = remember { Animatable(1f) }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isRevealed) Color.White else MagicPurple,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .height(if (pairsCount == 2) 130.dp else 100.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(
                                width = 3.dp,
                                color = if (card.isMatched) SunnyYellow else Color.Transparent,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable(enabled = !isBusy && !isRevealed) {
                                soundFx.playPop()
                                coroutineScope.launch {
                                    scale.animateTo(1.08f, tween(100))
                                    scale.animateTo(1f, tween(100))
                                }

                                if (selectedFirst == null) {
                                    selectedFirst = index
                                    cards[index] = card.copy(isFaceUp = true)
                                } else if (selectedSecond == null) {
                                    selectedSecond = index
                                    cards[index] = card.copy(isFaceUp = true)
                                    isBusy = true

                                    val firstIdx = selectedFirst!!
                                    val secondIdx = index
                                    val firstCard = cards[firstIdx]
                                    val secondCard = cards[secondIdx]

                                    if (firstCard.animal.id == secondCard.animal.id) {
                                        // Match found!
                                        coroutineScope.launch {
                                            delay(300)
                                            cards[firstIdx] = firstCard.copy(isMatched = true)
                                            cards[secondIdx] = secondCard.copy(isMatched = true)
                                            soundFx.playSuccess()
                                            speechManager.speak("¡Bien $childName! ¡La pareja de ${firstCard.animal.name}!")
                                            selectedFirst = null
                                            selectedSecond = null
                                            isBusy = false

                                            // Check win condition
                                            if (cards.all { it.isMatched }) {
                                                delay(400)
                                                soundFx.playStarFanfare()
                                                speechManager.speakPraise(childName)
                                                isCelebrationVisible = true
                                                onCompleteLevel(2)
                                            }
                                        }
                                    } else {
                                        // No match
                                        coroutineScope.launch {
                                            delay(1000)
                                            cards[firstIdx] = firstCard.copy(isFaceUp = false)
                                            cards[secondIdx] = secondCard.copy(isFaceUp = false)
                                            selectedFirst = null
                                            selectedSecond = null
                                            isBusy = false
                                        }
                                    }
                                }
                            }
                            .scale(scale.value)
                            .testTag("memory_card_$index")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRevealed) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = card.animal.emoji,
                                        fontSize = if (pairsCount == 2) 46.sp else 36.sp
                                    )
                                    Text(
                                        text = card.animal.name.split(" ").first(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF37474F)
                                    )
                                }
                            } else {
                                Text(
                                    text = "🐾",
                                    fontSize = 32.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isCelebrationVisible) {
            CelebrationOverlay(
                childName = childName,
                starsEarned = 2,
                onPlayAgain = { resetGame() },
                onGoHome = onGoHome
            )
        }
    }
}
