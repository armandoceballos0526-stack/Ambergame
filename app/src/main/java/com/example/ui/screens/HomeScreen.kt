package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ScreenRoute
import com.example.ui.theme.CandyPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.MagicPurple
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow

/**
 * Toddler-First Home Dashboard for 4-year-old Amber.
 * Amber cannot read text, so the interface is composed of large, chunky,
 * instantly-recognizable game buttons in a 2-column grid.
 * Zero redundant text paragraphs; pure visual clarity and joyful play.
 */
@Composable
fun HomeScreen(
    childName: String,
    starsCount: Int,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_bounce")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_anim"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Full Real Game Landscape Wallpaper
        Image(
            painter = painterResource(id = R.drawable.img_game_background),
            contentDescription = "Fondo de juego",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Soft pastel tint overlay to guarantee clean text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.45f))
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Compact Header for Amber (Avatar + Greeting + Star Counter)
            item(span = { GridItemSpan(2) }) {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_welcome_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = SunnyYellow,
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(54.dp)
                                    .scale(1f + (bounceY * 0.02f))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_pet_unicorn),
                                    contentDescription = "Avatar de Amber",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "¡Hola, $childName! ✨",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CandyPink
                                )
                                Text(
                                    text = "¡Toca un juego para jugar!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF546E7A)
                                )
                            }
                        }

                        // Star badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SunnyYellow.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SunnyYellow),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "⭐", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$starsCount",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }

            // 1. Números (1, 2, 3...)
            item {
                ToddlerGameTile(
                    label = "1 2 3",
                    gradientColors = listOf(Color(0xFFFFB300), Color(0xFFFF6D00)),
                    borderColor = Color(0xFFFFD54F),
                    testTag = "game_numbers_button",
                    onClick = { onNavigate(ScreenRoute.NUMBERS) }
                ) {
                    NumbersIconIllustration()
                }
            }

            // 2. Tamaños y Opuestos (Grande vs Pequeño)
            item {
                ToddlerGameTile(
                    label = "TAMAÑOS",
                    gradientColors = listOf(Color(0xFF00E676), Color(0xFF00B0FF)),
                    borderColor = Color(0xFF69F0AE),
                    testTag = "game_sizes_button",
                    onClick = { onNavigate(ScreenRoute.SIZES_OPPOSITES) }
                ) {
                    SizesIconIllustration()
                }
            }

            // 3. Vocales (A, E, I...)
            item {
                ToddlerGameTile(
                    label = "A B C",
                    gradientColors = listOf(Color(0xFFFF4081), Color(0xFFAB47BC)),
                    borderColor = Color(0xFFFF80AB),
                    testTag = "game_vowels_button",
                    onClick = { onNavigate(ScreenRoute.VOWELS) }
                ) {
                    VowelsIconIllustration()
                }
            }

            // 4. Figuras y Colores
            item {
                ToddlerGameTile(
                    label = "FORMAS",
                    gradientColors = listOf(Color(0xFF00B0FF), Color(0xFF651FFF)),
                    borderColor = Color(0xFF80D8FF),
                    testTag = "game_shapes_button",
                    onClick = { onNavigate(ScreenRoute.SHAPES) }
                ) {
                    ShapesIconIllustration()
                }
            }

            // 5. Memoria de Parejas
            item {
                ToddlerGameTile(
                    label = "PAREJAS",
                    gradientColors = listOf(Color(0xFFBA68C8), Color(0xFF7E57C2)),
                    borderColor = Color(0xFFE1BEE7),
                    testTag = "game_memory_button",
                    onClick = { onNavigate(ScreenRoute.MEMORY) }
                ) {
                    MemoryIconIllustration()
                }
            }

            // 6. Mascota Virtual (3D Pet Care)
            item {
                ToddlerGameTile(
                    label = "MASCOTA",
                    gradientColors = listOf(Color(0xFFFF80AB), Color(0xFFFF4081)),
                    borderColor = Color(0xFFFFCDD2),
                    testTag = "game_pet_button",
                    onClick = { onNavigate(ScreenRoute.PET_CARE) }
                ) {
                    PetCareIconIllustration()
                }
            }

            // 7. Burbujas Mágicas (Pop)
            item {
                ToddlerGameTile(
                    label = "BURBUJAS",
                    gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF0091EA)),
                    borderColor = Color(0xFF84FFFF),
                    testTag = "game_bubbles_button",
                    onClick = { onNavigate(ScreenRoute.BUBBLES) }
                ) {
                    BubblesIconIllustration()
                }
            }

            // 8. Pintura Mágica y Sellos
            item {
                ToddlerGameTile(
                    label = "PINTAR",
                    gradientColors = listOf(Color(0xFFFF9100), Color(0xFFFF3D00)),
                    borderColor = Color(0xFFFFE082),
                    testTag = "game_paint_button",
                    onClick = { onNavigate(ScreenRoute.MAGIC_PAINT) }
                ) {
                    PaintIconIllustration()
                }
            }

            // 9. Xilófono Musical
            item {
                ToddlerGameTile(
                    label = "MÚSICA",
                    gradientColors = listOf(Color(0xFF7E57C2), Color(0xFF512DA8)),
                    borderColor = Color(0xFFB39DDB),
                    testTag = "game_xylophone_button",
                    onClick = { onNavigate(ScreenRoute.XYLOPHONE) }
                ) {
                    XylophoneIconIllustration()
                }
            }

            // 10. Pegatinas y Premios
            item {
                ToddlerGameTile(
                    label = "PREMIOS",
                    gradientColors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)),
                    borderColor = Color(0xFFFFF59D),
                    testTag = "game_stickers_button",
                    onClick = { onNavigate(ScreenRoute.STICKER_ALBUM) }
                ) {
                    TrophyIconIllustration()
                }
            }

            // Bottom Safe Bar
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = "🔒 Aplicación 100% segura para niños • Sin anuncios",
                            fontSize = 11.sp,
                            color = Color(0xFF546E7A),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Chunky, tactile, large button for a 4-year-old child.
 * Minimum height of 136dp with big expressive artwork taking 75% of space,
 * accompanied by a single short bold pill label.
 */
@Composable
private fun ToddlerGameTile(
    label: String,
    gradientColors: List<Color>,
    borderColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    illustration: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "tile_scale"
    )

    Surface(
        shape = RoundedCornerShape(26.dp),
        shadowElevation = if (isPressed) 2.dp else 7.dp,
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .height(138.dp)
            .scale(scale)
            .clip(RoundedCornerShape(26.dp))
            .border(3.dp, borderColor, RoundedCornerShape(26.dp))
            .background(Brush.verticalGradient(gradientColors))
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 3D Top Glossy Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.38f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Inner Content: Large illustration + Short Chunky Label
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Central High-Impact Toddler Graphic (fills available space)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    illustration()
                }

                // Chunky Bold Pill Identifier
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.94f),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// LARGE, HIGH-IMPACT TODDLER ILLUSTRATIONS
// ==========================================

@Composable
private fun NumbersIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 3D Block 1 (Cyan)
            drawRoundRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(w * 0.06f, h * 0.38f),
                size = Size(w * 0.42f, h * 0.42f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(w * 0.08f, h * 0.40f),
                size = Size(w * 0.38f, h * 0.14f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // 3D Block 2 (Yellow)
            drawRoundRect(
                color = Color(0xFFFFD600),
                topLeft = Offset(w * 0.46f, h * 0.32f),
                size = Size(w * 0.46f, h * 0.48f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(w * 0.48f, h * 0.34f),
                size = Size(w * 0.42f, h * 0.14f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // 3D Block 3 Top (Red)
            drawRoundRect(
                color = Color(0xFFFF1744),
                topLeft = Offset(w * 0.26f, h * 0.08f),
                size = Size(w * 0.44f, h * 0.38f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(w * 0.28f, h * 0.10f),
                size = Size(w * 0.40f, h * 0.12f),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }

        // Overlay Big Numbers
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1 2 3",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.shadow(4.dp)
            )
        }
    }
}

@Composable
private fun SizesIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Big Bear / Giant Ball (Magenta)
            val bigCenterX = w * 0.64f
            val bigCenterY = h * 0.52f
            val bigRadius = w * 0.34f

            // Ears
            drawCircle(Color(0xFFFF80AB), bigRadius * 0.38f, Offset(bigCenterX - bigRadius * 0.7f, bigCenterY - bigRadius * 0.75f))
            drawCircle(Color(0xFFFF80AB), bigRadius * 0.38f, Offset(bigCenterX + bigRadius * 0.7f, bigCenterY - bigRadius * 0.75f))

            // Body/Face
            drawCircle(Color(0xFFFF4081), bigRadius, Offset(bigCenterX, bigCenterY))
            drawCircle(Color.White.copy(alpha = 0.35f), bigRadius * 0.3f, Offset(bigCenterX - bigRadius * 0.35f, bigCenterY - bigRadius * 0.35f))

            // Eyes & Smile
            drawCircle(Color(0xFF263238), bigRadius * 0.12f, Offset(bigCenterX - bigRadius * 0.35f, bigCenterY - bigRadius * 0.1f))
            drawCircle(Color(0xFF263238), bigRadius * 0.12f, Offset(bigCenterX + bigRadius * 0.35f, bigCenterY - bigRadius * 0.1f))
            drawCircle(Color(0xFFE91E63), bigRadius * 0.1f, Offset(bigCenterX, bigCenterY + bigRadius * 0.12f))

            // Small Bear / Baby Ball (Yellow/Orange)
            val smallCenterX = w * 0.22f
            val smallCenterY = h * 0.68f
            val smallRadius = w * 0.18f

            // Small Ears
            drawCircle(Color(0xFFFFE082), smallRadius * 0.38f, Offset(smallCenterX - smallRadius * 0.7f, smallCenterY - smallRadius * 0.75f))
            drawCircle(Color(0xFFFFE082), smallRadius * 0.38f, Offset(smallCenterX + smallRadius * 0.7f, smallCenterY - smallRadius * 0.75f))

            // Small Face
            drawCircle(Color(0xFFFFD600), smallRadius, Offset(smallCenterX, smallCenterY))
            drawCircle(Color(0xFF263238), smallRadius * 0.12f, Offset(smallCenterX - smallRadius * 0.35f, smallCenterY - smallRadius * 0.1f))
            drawCircle(Color(0xFF263238), smallRadius * 0.12f, Offset(smallCenterX + smallRadius * 0.35f, smallCenterY - smallRadius * 0.1f))
        }
    }
}

@Composable
private fun VowelsIconIllustration() {
    Row(
        modifier = Modifier.size(76.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFFD600),
            shadowElevation = 3.dp,
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "A", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFD81B60))
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "E", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFFAB47BC))
            }
        }
    }
}

@Composable
private fun ShapesIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Star (Gold)
            drawStar(cx = w * 0.65f, cy = h * 0.38f, outerR = w * 0.32f, color = Color(0xFFFFD600))
            drawCircle(Color.White.copy(alpha = 0.5f), w * 0.08f, Offset(w * 0.62f, h * 0.34f))

            // Heart (Pink)
            drawHeart(cx = w * 0.30f, cy = h * 0.60f, size = w * 0.28f, color = Color(0xFFFF4081))

            // Circle (Cyan)
            drawCircle(Color(0xFF00E5FF), w * 0.14f, Offset(w * 0.28f, h * 0.26f))
        }
    }
}

@Composable
private fun MemoryIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Card 1 (Back angled)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset(w * 0.14f, h * 0.16f),
                size = Size(w * 0.40f, h * 0.62f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Card 2 (Front)
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.42f, h * 0.22f),
                size = Size(w * 0.42f, h * 0.64f),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Cute Paw Print on Card 2
            val pawCenterX = w * 0.63f
            val pawCenterY = h * 0.58f
            drawCircle(Color(0xFFFF4081), w * 0.08f, Offset(pawCenterX, pawCenterY))
            drawCircle(Color(0xFFFF4081), w * 0.035f, Offset(pawCenterX - w * 0.07f, pawCenterY - w * 0.08f))
            drawCircle(Color(0xFFFF4081), w * 0.038f, Offset(pawCenterX, pawCenterY - w * 0.10f))
            drawCircle(Color(0xFFFF4081), w * 0.035f, Offset(pawCenterX + w * 0.07f, pawCenterY - w * 0.08f))
        }
    }
}

@Composable
private fun PetCareIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_pet_unicorn),
            contentDescription = "Mascota",
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(2.5.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop
        )

        // Floating strawberry & crown badge
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.BottomEnd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🍓", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun BubblesIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Big central bubble
            drawCircle(
                color = Color.White.copy(alpha = 0.55f),
                radius = w * 0.32f,
                center = Offset(w * 0.46f, h * 0.46f)
            )
            drawCircle(
                color = Color(0x66EA80FC),
                radius = w * 0.28f,
                center = Offset(w * 0.46f, h * 0.46f)
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.08f,
                center = Offset(w * 0.36f, h * 0.36f)
            )

            // Small flying bubbles
            drawCircle(Color.White.copy(alpha = 0.8f), w * 0.14f, Offset(w * 0.78f, h * 0.24f))
            drawCircle(Color.White.copy(alpha = 0.7f), w * 0.10f, Offset(w * 0.18f, h * 0.74f))
            drawCircle(Color.White.copy(alpha = 0.9f), w * 0.06f, Offset(w * 0.75f, h * 0.70f))
        }
    }
}

@Composable
private fun PaintIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Wooden Palette
            drawOval(
                color = Color(0xFFFFE082),
                topLeft = Offset(w * 0.12f, h * 0.15f),
                size = Size(w * 0.76f, h * 0.65f)
            )

            // Paint blobs
            drawCircle(Color(0xFFFF1744), w * 0.08f, Offset(w * 0.30f, h * 0.36f))
            drawCircle(Color(0xFF00E676), w * 0.08f, Offset(w * 0.50f, h * 0.30f))
            drawCircle(Color(0xFF00B0FF), w * 0.08f, Offset(w * 0.70f, h * 0.44f))
            drawCircle(Color(0xFFAA00FF), w * 0.08f, Offset(w * 0.44f, h * 0.62f))

            // Thumb hole
            drawCircle(Color(0xFFFF9100), w * 0.06f, Offset(w * 0.24f, h * 0.60f))
        }
    }
}

@Composable
private fun XylophoneIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val colors = listOf(
                Color(0xFFFF1744), // Red
                Color(0xFFFF9100), // Orange
                Color(0xFFFFD600), // Yellow
                Color(0xFF00E676), // Green
                Color(0xFF00B0FF)  // Blue
            )

            val barCount = colors.size
            val barW = w * 0.12f
            val spacing = w * 0.04f
            val startX = (w - (barCount * barW + (barCount - 1) * spacing)) / 2f

            for (i in 0 until barCount) {
                val x = startX + i * (barW + spacing)
                val barH = h * (0.65f - i * 0.07f)
                val y = (h - barH) / 2f

                drawRoundRect(
                    color = colors[i],
                    topLeft = Offset(x, y),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        // Floating musical note
        Text(
            text = "🎵",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun TrophyIconIllustration() {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Golden Trophy Cup
            val cupPath = Path().apply {
                moveTo(w * 0.26f, h * 0.22f)
                lineTo(w * 0.74f, h * 0.22f)
                lineTo(w * 0.66f, h * 0.58f)
                cubicTo(w * 0.66f, h * 0.70f, w * 0.34f, h * 0.70f, w * 0.34f, h * 0.58f)
                close()
            }
            drawPath(cupPath, Color(0xFFFFD600))

            // Base
            drawRoundRect(
                color = Color(0xFFFFAB00),
                topLeft = Offset(w * 0.32f, h * 0.74f),
                size = Size(w * 0.36f, h * 0.14f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Star on Trophy
            drawStar(cx = w * 0.50f, cy = h * 0.40f, outerR = w * 0.12f, color = Color.White)
        }
    }
}

// Helpers for drawing geometric shapes
private fun DrawScope.drawStar(cx: Float, cy: Float, outerR: Float, color: Color) {
    val innerR = outerR * 0.45f
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = Math.toRadians((i * 36 - 90).toDouble())
        val x = (cx + r * kotlin.math.cos(angle)).toFloat()
        val y = (cy + r * kotlin.math.sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, color: Color) {
    val path = Path()
    path.moveTo(cx, cy + size * 0.6f)
    path.cubicTo(
        cx - size * 0.9f, cy - size * 0.2f,
        cx - size * 0.6f, cy - size * 0.9f,
        cx, cy - size * 0.3f
    )
    path.cubicTo(
        cx + size * 0.6f, cy - size * 0.9f,
        cx + size * 0.9f, cy - size * 0.2f,
        cx, cy + size * 0.6f
    )
    path.close()
    drawPath(path, color)
}
