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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.Sticker
import com.example.ui.theme.CandyPink
import com.example.ui.theme.StarGold
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.launch

@Composable
fun StickerAlbumScreen(
    childName: String,
    starsCount: Int,
    stickers: List<Sticker>,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val unlockedCount = stickers.count { it.unlocked }
        speechManager.speak("¡Amber, este es tu álbum de pegatinas mágicas! Tienes $unlockedCount pegatinas desbloqueadas.")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF9C4), Color(0xFFFFF0F5), Color(0xFFE8F5E9))
                )
            )
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌟", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Álbum de $childName",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = CandyPink
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tienes $starsCount ⭐ estrellas para desbloquear pegatinas mágicas",
                    fontSize = 13.sp,
                    color = Color(0xFF546E7A),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .navigationBarsPadding()
        ) {
            items(stickers) { sticker ->
                val scale = remember { Animatable(1f) }

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = if (sticker.unlocked) Color.White else Color(0xFFEEEEEE),
                    shadowElevation = if (sticker.unlocked) 4.dp else 1.dp,
                    modifier = Modifier
                        .height(115.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(
                            width = 2.dp,
                            color = if (sticker.unlocked) SunnyYellow else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable {
                            if (sticker.unlocked) {
                                soundFx.playPop()
                                speechManager.speak("¡Es tu pegatina de ${sticker.name}!")
                                coroutineScope.launch {
                                    scale.animateTo(1.2f, tween(100))
                                    scale.animateTo(1f, tween(120))
                                }
                            } else {
                                soundFx.playTap()
                                speechManager.speak("Necesitas ${sticker.starsRequired} estrellas para desbloquear esta pegatina. ¡Sigue jugando!")
                            }
                        }
                        .scale(scale.value)
                        .testTag("sticker_item_${sticker.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (sticker.unlocked) {
                            Text(text = sticker.emoji, fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sticker.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F),
                                textAlign = TextAlign.Center,
                                lineHeight = 12.sp,
                                maxLines = 2
                            )
                        } else {
                            Text(text = "🔒", fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${sticker.starsRequired} ⭐",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78909C)
                            )
                        }
                    }
                }
            }
        }
    }
}
