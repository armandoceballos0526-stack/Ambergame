package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.data.PetTool
import com.example.data.PetType
import com.example.ui.theme.CandyPink
import com.example.ui.theme.CoralOrange
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

/**
 * Expressive Emotional States for Amber's Virtual Pet.
 */
enum class PetEmotion(
    val title: String,
    val emoji: String,
    val defaultSpeech: String
) {
    FELIZ("¡Muy Feliz!", "🥰", "¡Hola Amber! ¡Te amo mucho! 💖"),
    ENAMORADO("¡Lleno de Amor!", "😍", "¡Prrrr... qué ricos mimos! 💕"),
    HAMBRIENTO("¡Con Hambre!", "😋", "¿Me das comidita rica, Amber? 🍓"),
    COSQUILLAS("¡Cosquillas!", "😂", "¡Jajajaja, qué cosquillitas! 🌟"),
    SUEÑO("¡Con Sueño!", "😴", "Oaaah... ¡Tengo sueño, arrúllame! 🌙"),
    RADIANTE("¡Radiante!", "👑", "¡Mira cómo brillo como princesa! ✨")
}

/**
 * 3-Step Spa Salon for Bathing.
 */
enum class BathStep(val title: String, val toolName: String, val icon: String) {
    ENJABONAR("Paso 1: Enjabonar", "Esponja", "🧽"),
    ENJUAGAR("Paso 2: Enjuagar", "Ducha", "🚿"),
    SECAR("Paso 3: Secar", "Secador", "💨")
}

/**
 * Royal Accessories Amber can equip after brushing.
 */
enum class PetAccessory(val id: String, val emoji: String, val label: String) {
    TIARA("tiara", "👑", "Corona"),
    MONO("mono", "🎀", "Moño"),
    FLOR("flor", "🌸", "Flor"),
    GAFITAS("gafitas", "🕶️", "Lentes"),
    ESTRELLA("estrella", "⭐", "Estrella")
}

private data class FoodPiece(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color
)

private data class CrumbParticle(
    val x: Float,
    val y: Float,
    val color: Color
)

private data class SudBubble(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float
)

private data class SparkleTrail(
    val id: Long,
    val x: Float,
    val y: Float,
    val emoji: String
)

/**
 * 3D Interactive Virtual Pet Care Experience for Amber.
 * Realistically reacts to touch with multi-zone gestures and vivid emotions:
 * - Natural 3D head and body perspective tracking touching/food positions.
 * - Multi-zone touch reactions: Ear scratching (purrs & head tilt), cheek caress (blush & hearts),
 *   tummy tickling (spring leaps, squash-and-stretch physics, giggles).
 * - Continuous petting swipe detection with rising hearts and tactile purring.
 * - Multi-stage feeding with 3 bites, crumbs, chewing, satisfaction animations.
 * - Brushing/styling salon with star trails & unlockable royal accessories.
 * - 3-step bubble spa (sponge suds -> shower rinse -> blow dry fluff).
 * - Day/Night toggle with cozy bedtime blanket, soft snores, and rising Zzz bubbles.
 */
@Composable
fun PetCareScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCareAction: () -> Unit,
    onEarnStar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedPet by remember { mutableStateOf(PetType.UNICORNIO) }
    var selectedTool by remember { mutableStateOf(PetTool.COMIDA) }

    // Dynamic Emotional State
    var currentEmotion by remember { mutableStateOf(PetEmotion.FELIZ) }
    var emotionFeedbackText by remember { mutableStateOf("¡Hola, $childName! ¡Qué lindo verte! 💖") }

    // 3D Perspective Physics Rig
    val rotY = remember { Animatable(0f) }
    val rotX = remember { Animatable(0f) }
    val rotZ = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }
    val petOffsetY = remember { Animatable(0f) }
    val earTwitchAngle = remember { Animatable(0f) }

    // Character Facial & State Flags
    var isMouthOpen by remember { mutableStateOf(false) }
    var isChewing by remember { mutableStateOf(false) }
    var isEyesHappyClosed by remember { mutableStateOf(false) }
    var isBlushing by remember { mutableStateOf(false) }
    var isSleeping by remember { mutableStateOf(false) }
    var equippedAccessory by remember { mutableStateOf<PetAccessory?>(PetAccessory.TIARA) }

    // Natural periodic eye blink (only when awake)
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(isSleeping) {
        if (!isSleeping) {
            while (true) {
                delay(Random.nextLong(2600, 4200))
                isBlinking = true
                delay(160)
                isBlinking = false
            }
        }
    }

    // Belly fullness (0.0 to 1.0)
    var bellyFullness by remember { mutableFloatStateOf(0.45f) }
    var groomProgress by remember { mutableFloatStateOf(0.3f) }

    // Feeding sub-process
    val foodList = remember {
        listOf(
            FoodPiece("fresa", "Fresa", "🍓", Color(0xFFFF1744)),
            FoodPiece("manzana", "Manzana", "🍎", Color(0xFFD50000)),
            FoodPiece("galleta", "Galleta", "🍪", Color(0xFFFF8F00)),
            FoodPiece("zanahoria", "Zanahoria", "🥕", Color(0xFFFF6D00)),
            FoodPiece("cupcake", "Cupcake", "🧁", Color(0xFFFF4081)),
            FoodPiece("leche", "Biberón", "🍼", Color(0xFF00B0FF))
        )
    }
    var activeFood by remember { mutableStateOf<FoodPiece?>(foodList[0]) }
    var foodPos by remember { mutableStateOf<Offset?>(null) }
    val crumbs = remember { mutableStateListOf<CrumbParticle>() }

    // Bath sub-process
    var currentBathStep by remember { mutableStateOf(BathStep.ENJABONAR) }
    var soapProgress by remember { mutableFloatStateOf(0f) }
    val soapSuds = remember { mutableStateListOf<SudBubble>() }
    var nextBubbleId by remember { mutableStateOf(1L) }

    // Interactive Particle Trails (Hearts, Sparkles, Zzz)
    val floatingParticles = remember { mutableStateListOf<SparkleTrail>() }
    var nextParticleId by remember { mutableStateOf(1L) }

    // Toy Ball Mini-game
    var ballPos by remember { mutableStateOf(Offset(220f, 320f)) }
    var ballBounces by remember { mutableIntStateOf(0) }

    var careScore by remember { mutableIntStateOf(0) }

    // Idle organic breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "idle_pet_life")
    val idleBreathe by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    fun rewardAction(message: String, newEmotion: PetEmotion = PetEmotion.FELIZ) {
        soundFx.playSuccess()
        careScore++
        currentEmotion = newEmotion
        emotionFeedbackText = message
        onCareAction()
        if (careScore % 4 == 0) {
            onEarnStar()
            speechManager.speak("¡Felicidades, $childName! ¡Ganaste una estrella por cuidar con tanto amor a tu mascota!")
        } else {
            speechManager.speak(message)
        }
    }

    // Feeding action trigger
    fun feedCurrentFood(targetFood: FoodPiece, mouthCenterX: Float, mouthCenterY: Float) {
        coroutineScope.launch {
            isMouthOpen = false
            isChewing = true
            currentEmotion = PetEmotion.HAMBRIENTO
            emotionFeedbackText = "¡Masticando la rica ${targetFood.name}! 😋"

            // Bite 1
            soundFx.playMunch()
            repeat(8) {
                crumbs.add(
                    CrumbParticle(
                        x = mouthCenterX + Random.nextInt(-35, 35),
                        y = mouthCenterY + Random.nextInt(-20, 20),
                        color = targetFood.color
                    )
                )
            }
            delay(320)

            // Bite 2
            soundFx.playMunch()
            repeat(8) {
                crumbs.add(
                    CrumbParticle(
                        x = mouthCenterX + Random.nextInt(-35, 35),
                        y = mouthCenterY + Random.nextInt(-20, 20),
                        color = targetFood.color
                    )
                )
            }
            delay(320)

            // Bite 3 & Finish food
            soundFx.playMunch()
            foodPos = null
            scaleAnim.animateTo(1.2f, spring(dampingRatio = 0.4f))
            scaleAnim.animateTo(1f, spring(dampingRatio = 0.5f))
            petOffsetY.animateTo(-25f, tween(150))
            petOffsetY.animateTo(0f, spring(dampingRatio = 0.5f))

            bellyFullness = (bellyFullness + 0.25f).coerceAtMost(1f)
            delay(500)
            isChewing = false
            crumbs.clear()

            rewardAction(
                "¡Ñam, ñam, qué delicia! A tu ${selectedPet.petName} le encantó la ${targetFood.name}.",
                PetEmotion.FELIZ
            )
        }
    }

    // Spawn floating particles helper
    fun spawnParticles(origin: Offset, emoji: String, count: Int = 2) {
        repeat(count) {
            val offsetX = Random.nextInt(-30, 30).toFloat()
            val offsetY = Random.nextInt(-30, 30).toFloat()
            floatingParticles.add(
                SparkleTrail(
                    id = nextParticleId++,
                    x = origin.x + offsetX,
                    y = origin.y + offsetY,
                    emoji = emoji
                )
            )
        }
        if (floatingParticles.size > 25) {
            repeat(floatingParticles.size - 25) {
                if (floatingParticles.isNotEmpty()) floatingParticles.removeAt(0)
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenW = constraints.maxWidth.toFloat()
        val screenH = constraints.maxHeight.toFloat()
        val petCenterX = screenW * 0.5f
        val petCenterY = screenH * 0.38f

        // Dynamic Background (Cozy Daytime Nursery or Starry Bedtime Sky)
        if (isSleeping) {
            // Nighttime Deep Sky with Stars & Moon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0D1B2A),
                                Color(0xFF1B263B),
                                Color(0xFF415A77)
                            )
                        )
                    )
            ) {
                // Moon
                Text(
                    text = "🌙",
                    fontSize = 44.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 70.dp, end = 26.dp)
                )
                Text(
                    text = "✨",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 90.dp, start = 40.dp)
                )
                Text(
                    text = "⭐",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp)
                )
            }
        } else {
            // Daytime Nursery Wallpaper
            Image(
                painter = painterResource(id = R.drawable.img_game_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.52f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header Bar: Pet Switcher, Emotion Badge & Belly Meter
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSleeping) Color(0xFF1E293B).copy(alpha = 0.92f)
                    else Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Pet selection buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PetType.values().forEach { pet ->
                            val isChosen = selectedPet == pet
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isChosen) CandyPink else if (isSleeping) Color(0xFF334155) else Color(0xFFF0F0F0),
                                border = if (isChosen) BorderStroke(2.dp, Color.White) else null,
                                shadowElevation = if (isChosen) 3.dp else 0.dp,
                                modifier = Modifier
                                    .clickable {
                                        selectedPet = pet
                                        soundFx.playTap()
                                        speechManager.speak("¡Hola! Estás cuidando a tu ${pet.petName}.")
                                    }
                                    .testTag("pet_switch_${pet.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = pet.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = pet.petName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isChosen) Color.White else if (isSleeping) Color.White else Color(0xFF37474F)
                                    )
                                }
                            }
                        }
                    }

                    // Emotion & Belly meters
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Current Emotion Badge
                        Surface(
                            shape = CircleShape,
                            color = SunnyYellow.copy(alpha = 0.35f),
                            border = BorderStroke(1.5.dp, SunnyYellow),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable {
                                    speechManager.speak("Tu mascota está ${currentEmotion.title}")
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = currentEmotion.emoji, fontSize = 20.sp)
                            }
                        }

                        // Belly fullness
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💖", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDDDDDD))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(bellyFullness)
                                        .height(12.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(CandyPink, CoralOrange)
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // CENTRAL 3D INTERACTIVE NURSERY STAGE
            // ==========================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        if (isSleeping) {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E293B).copy(alpha = 0.85f),
                                    Color(0xFF0F172A).copy(alpha = 0.95f)
                                )
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFF0F5),
                                    Color(0xFFE8F5E9).copy(alpha = 0.7f),
                                    Color(0xFFFFEBEE)
                                )
                            )
                        }
                    )
                    .border(
                        3.dp,
                        if (isSleeping) SkyBlue.copy(alpha = 0.4f) else CandyPink.copy(alpha = 0.35f),
                        RoundedCornerShape(32.dp)
                    )
                    .pointerInput(selectedTool, currentBathStep, isSleeping) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val touchPos = change.position
                                val deltaX = (touchPos.x - petCenterX) / petCenterX
                                val deltaY = (touchPos.y - petCenterY) / petCenterY

                                // 3D Head and Body tracking towards finger
                                coroutineScope.launch {
                                    rotY.snapTo((deltaX * 26f).coerceIn(-28f, 28f))
                                    rotX.snapTo((-deltaY * 18f).coerceIn(-22f, 22f))
                                }

                                if (isSleeping) {
                                    // Gentle night caress
                                    if (Random.nextFloat() < 0.25f) {
                                        spawnParticles(touchPos, "💤")
                                        soundFx.playSnore()
                                    }
                                    return@detectDragGestures
                                }

                                when (selectedTool) {
                                    PetTool.PEINAR -> {
                                        // Brushing pet fur with magic stars
                                        if (Random.nextFloat() < 0.35f) {
                                            spawnParticles(touchPos, "✨")
                                            soundFx.playMagicChime()
                                            groomProgress = (groomProgress + 0.04f).coerceAtMost(1f)
                                            if (groomProgress >= 1f && currentEmotion != PetEmotion.RADIANTE) {
                                                currentEmotion = PetEmotion.RADIANTE
                                                soundFx.playStarFanfare()
                                                speechManager.speak("¡Pelaje súper peinado y reluciente, $childName! ¡Elige un accesorio de princesa!")
                                            }
                                        }
                                    }
                                    PetTool.BANIO -> {
                                        if (currentBathStep == BathStep.ENJABONAR) {
                                            if (soapSuds.size < 40) {
                                                soapSuds.add(
                                                    SudBubble(
                                                        id = nextBubbleId++,
                                                        x = touchPos.x + Random.nextInt(-25, 25),
                                                        y = touchPos.y + Random.nextInt(-25, 25),
                                                        size = Random.nextFloat() * 24f + 20f
                                                    )
                                                )
                                                soundFx.playTap()
                                                soapProgress = (soapProgress + 0.05f).coerceAtMost(1f)
                                                if (soapProgress >= 1f && currentBathStep == BathStep.ENJABONAR) {
                                                    currentBathStep = BathStep.ENJUAGAR
                                                    soundFx.playSuccess()
                                                    speechManager.speak("¡Todo enjabonado con burbujas! Ahora enjuaga con la ducha.")
                                                }
                                            }
                                        } else if (currentBathStep == BathStep.ENJUAGAR) {
                                            val iterator = soapSuds.iterator()
                                            var rinsed = false
                                            while (iterator.hasNext()) {
                                                val b = iterator.next()
                                                if (hypot(touchPos.x - b.x, touchPos.y - b.y) < 70f) {
                                                    iterator.remove()
                                                    rinsed = true
                                                }
                                            }
                                            if (rinsed) {
                                                soundFx.playPop()
                                                if (soapSuds.isEmpty()) {
                                                    currentBathStep = BathStep.SECAR
                                                    soundFx.playSuccess()
                                                    speechManager.speak("¡Burbujas enjuagadas! Ahora usa el secador para secar su pelaje.")
                                                }
                                            }
                                        }
                                    }
                                    PetTool.MIMOS -> {
                                        // Continuous Petting with Purr & Hearts
                                        if (Random.nextFloat() < 0.35f) {
                                            spawnParticles(touchPos, "💖")
                                            soundFx.playPurr()
                                            isBlushing = true
                                            isEyesHappyClosed = true
                                            currentEmotion = PetEmotion.ENAMORADO
                                        }
                                    }
                                    PetTool.COMIDA -> {
                                        // Track drag towards food
                                    }
                                }
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    rotY.animateTo(0f, spring(dampingRatio = 0.5f))
                                    rotX.animateTo(0f, spring(dampingRatio = 0.5f))
                                    delay(400)
                                    if (!isSleeping) {
                                        isBlushing = false
                                        isEyesHappyClosed = false
                                    }
                                }
                            }
                        )
                    }
                    .testTag("interactive_pet_stage"),
                contentAlignment = Alignment.Center
            ) {
                // 3D Perspective Ground Shadow (grows & shrinks with breathing/jumping)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 26.dp)
                        .width(180.dp * (scaleAnim.value * idleBreathe))
                        .height(30.dp)
                        .clip(CircleShape)
                        .background(if (isSleeping) Color(0x35000000) else Color(0x25000000))
                )

                // 3D Living Pet Character Rig
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset { IntOffset(0, petOffsetY.value.toInt()) }
                        .graphicsLayer {
                            rotationY = rotY.value
                            rotationX = rotX.value
                            rotationZ = rotZ.value
                            scaleX = scaleAnim.value * idleBreathe
                            scaleY = scaleAnim.value * idleBreathe
                            cameraDistance = 14f * density
                        }
                ) {
                    // Status Crown / Reaction Emoticon Floating above Head
                    Box(modifier = Modifier.height(38.dp), contentAlignment = Alignment.BottomCenter) {
                        val activeAcc = equippedAccessory
                        if (isSleeping) {
                            Text(text = "💤", fontSize = 32.sp)
                        } else if (activeAcc != null && currentEmotion == PetEmotion.RADIANTE) {
                            Text(text = activeAcc.emoji, fontSize = 36.sp)
                        } else if (isChewing) {
                            Text(text = "😋", fontSize = 32.sp)
                        } else if (isMouthOpen) {
                            Text(text = "😮", fontSize = 32.sp)
                        } else if (isBlushing) {
                            Text(text = "🥰", fontSize = 32.sp)
                        } else if (currentEmotion == PetEmotion.COSQUILLAS) {
                            Text(text = "😂", fontSize = 32.sp)
                        } else {
                            Text(text = currentEmotion.emoji, fontSize = 28.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // 3D Character Body with Multi-Zone Touch Reaction Rig
                    Box(
                        modifier = Modifier
                            .size(210.dp, 210.dp)
                            .testTag("pet_body_rig"),
                        contentAlignment = Alignment.Center
                    ) {
                        // 3D Visual Character Asset
                        val petResId = if (selectedPet == PetType.UNICORNIO) R.drawable.img_pet_unicorn else R.drawable.img_pet_kitten
                        Image(
                            painter = painterResource(id = petResId),
                            contentDescription = selectedPet.petName,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(42.dp))
                                .graphicsLayer {
                                    if (isSleeping) {
                                        alpha = 0.92f
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )

                        // Equipped Accessory on the Character
                        val currentAcc = equippedAccessory
                        if (currentAcc != null && !isSleeping) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = 12.dp)
                            ) {
                                Text(text = currentAcc.emoji, fontSize = 40.sp)
                            }
                        }

                        // Dynamic Animated Facial Overlays
                        // 1. Happy Blushing Cheeks
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val blushAlpha = if (isBlushing) 0.85f else 0.40f
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4081).copy(alpha = blushAlpha))
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4081).copy(alpha = blushAlpha))
                            )
                        }

                        // 2. Dynamic Mouth Layer
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMouthOpen) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD81B60),
                                    border = BorderStroke(2.dp, Color(0xFFFF80AB)),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "😮", fontSize = 24.sp)
                                    }
                                }
                            } else if (isChewing) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFE91E63),
                                    modifier = Modifier.size(36.dp, 20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "😋", fontSize = 16.sp)
                                    }
                                }
                            }
                        }

                        // 3. Eye Blinking / Sleeping / Closed Happiness Overlay
                        if (isBlinking || isEyesHappyClosed || isSleeping) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 46.dp)
                                    .align(Alignment.Center)
                                    .offset(y = (-20).dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "◠",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = "◠",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF263238)
                                )
                            }
                        }

                        // 4. Cozy Bedtime Blanket when sleeping
                        if (isSleeping) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 24.dp, bottomEnd = 24.dp),
                                color = SkyBlue.copy(alpha = 0.92f),
                                border = BorderStroke(2.dp, Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                                    .align(Alignment.BottomCenter)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(text = "⭐ 🌙 ⭐", fontSize = 22.sp)
                                }
                            }
                        }

                        // ==========================================
                        // INVISIBLE TACTILE TOUCH ZONES ON CHARACTER
                        // ==========================================
                        // Zone A: Head & Ears Scratching (Top half)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .align(Alignment.TopCenter)
                                .clickable {
                                    if (isSleeping) {
                                        soundFx.playSnore()
                                        speechManager.speak("Shhh... tu mascota está descansando.")
                                        return@clickable
                                    }
                                    soundFx.playPurr()
                                    isBlushing = true
                                    isEyesHappyClosed = true
                                    currentEmotion = PetEmotion.ENAMORADO
                                    coroutineScope.launch {
                                        rotZ.animateTo(-8f, tween(120))
                                        rotZ.animateTo(8f, tween(120))
                                        rotZ.animateTo(0f, tween(120))
                                        delay(800)
                                        isBlushing = false
                                        isEyesHappyClosed = false
                                    }
                                    val earMessage = if (selectedPet == PetType.UNICORNIO) {
                                        "¡Iiii-jiji! ¡A tu unicornio le encanta que le toques su cuerno mágico, $childName!"
                                    } else {
                                        "¡Prrrr... miau! ¡Qué rico rascado en las orejitas, $childName!"
                                    }
                                    rewardAction(earMessage, PetEmotion.ENAMORADO)
                                }
                                .testTag("touch_zone_ears")
                        )

                        // Zone B: Tummy Tickling (Bottom half)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(115.dp)
                                .align(Alignment.BottomCenter)
                                .clickable {
                                    if (isSleeping) {
                                        soundFx.playSnore()
                                        return@clickable
                                    }
                                    // Burst into Giggles & Spring Squash-Stretch!
                                    soundFx.playGiggle()
                                    currentEmotion = PetEmotion.COSQUILLAS
                                    coroutineScope.launch {
                                        // Leaps and wiggles
                                        petOffsetY.animateTo(-45f, tween(120))
                                        rotZ.animateTo(14f, tween(80))
                                        rotZ.animateTo(-14f, tween(80))
                                        rotZ.animateTo(0f, tween(80))
                                        petOffsetY.animateTo(0f, spring(dampingRatio = 0.45f))
                                        scaleAnim.animateTo(1.18f, spring(dampingRatio = 0.35f))
                                        scaleAnim.animateTo(1f, spring(dampingRatio = 0.5f))
                                    }
                                    rewardAction(
                                        "¡Jajajaja! ¡Cosquillas en la pancita, $childName! ¡Qué risa!",
                                        PetEmotion.COSQUILLAS
                                    )
                                }
                                .testTag("touch_zone_tummy")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Speech bubble feedback
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSleeping) Color(0xFF1E293B).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f),
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, CandyPink.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = emotionFeedbackText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSleeping) SkyBlue else CandyPink,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }

                // Render Floating Particles (Hearts, Stars, Zzz, Shimmers)
                floatingParticles.forEach { particle ->
                    Box(
                        modifier = Modifier.offset {
                            IntOffset(particle.x.toInt() - 18, particle.y.toInt() - 18)
                        }
                    ) {
                        Text(text = particle.emoji, fontSize = 28.sp)
                    }
                }

                // Render Soap Suds
                soapSuds.forEach { sud ->
                    Box(
                        modifier = Modifier
                            .offset { IntOffset((sud.x - sud.size / 2).toInt(), (sud.y - sud.size / 2).toInt()) }
                            .size(sud.size.dp)
                            .clip(CircleShape)
                            .background(Color(0xDDFFFFFF))
                            .border(1.5.dp, Color(0x9980D8FF), CircleShape)
                    )
                }

                // Render Crumbs during feeding
                crumbs.forEach { c ->
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(c.x.toInt(), c.y.toInt()) }
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(c.color)
                    )
                }

                // Render Toy Ball in MIMOS mode
                if (selectedTool == PetTool.MIMOS && !isSleeping) {
                    Surface(
                        shape = CircleShape,
                        color = SunnyYellow,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .offset { IntOffset(ballPos.x.toInt() - 28, ballPos.y.toInt() - 28) }
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable {
                                soundFx.playPop()
                                ballBounces++
                                coroutineScope.launch {
                                    ballPos = Offset(petCenterX + Random.nextInt(-40, 40), petCenterY - 100f)
                                    petOffsetY.animateTo(-50f, spring(dampingRatio = 0.4f))
                                    delay(400)
                                    petOffsetY.animateTo(0f, spring(dampingRatio = 0.6f))
                                    ballPos = Offset(Random.nextInt(100, 480).toFloat(), screenH * 0.42f)
                                }
                                rewardAction("¡Rebote número $ballBounces! ¡Atrapada genial $childName!", PetEmotion.FELIZ)
                            }
                            .testTag("pet_toy_ball")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🏐", fontSize = 32.sp)
                        }
                    }
                }

                // Interactive Draggable / Tappable Food during FEED mode
                if (selectedTool == PetTool.COMIDA && activeFood != null && !isSleeping) {
                    val currentFoodOffset = foodPos ?: Offset(petCenterX, screenH * 0.46f)
                    Box(
                        modifier = Modifier
                            .offset { IntOffset((currentFoodOffset.x - 38).toInt(), (currentFoodOffset.y - 38).toInt()) }
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.95f))
                            .border(3.dp, CoralOrange, CircleShape)
                            .pointerInput(activeFood) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        foodPos = currentFoodOffset + offset
                                        soundFx.playTap()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val newPos = (foodPos ?: currentFoodOffset) + dragAmount
                                        foodPos = newPos

                                        // 3D head tracks the moving food
                                        val deltaX = (newPos.x - petCenterX) / petCenterX
                                        val deltaY = (newPos.y - petCenterY) / petCenterY
                                        coroutineScope.launch {
                                            rotY.snapTo((deltaX * 24f).coerceIn(-25f, 25f))
                                            rotX.snapTo((-deltaY * 18f).coerceIn(-20f, 20f))
                                        }

                                        // Anticipatory mouth opening when food is near
                                        val distToMouth = hypot(newPos.x - petCenterX, newPos.y - (petCenterY + 10f))
                                        isMouthOpen = distToMouth < 140f
                                    },
                                    onDragEnd = {
                                        val finalPos = foodPos ?: currentFoodOffset
                                        val distToMouth = hypot(finalPos.x - petCenterX, finalPos.y - (petCenterY + 10f))
                                        if (distToMouth < 150f) {
                                            feedCurrentFood(activeFood!!, petCenterX, petCenterY + 10f)
                                        } else {
                                            foodPos = null
                                            isMouthOpen = false
                                        }
                                        coroutineScope.launch {
                                            rotY.animateTo(0f, spring(dampingRatio = 0.5f))
                                            rotX.animateTo(0f, spring(dampingRatio = 0.5f))
                                        }
                                    }
                                )
                            }
                            .clickable {
                                // Tap to feed directly for small toddlers who struggle with dragging
                                feedCurrentFood(activeFood!!, petCenterX, petCenterY + 10f)
                            }
                            .testTag("draggable_food_item"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = activeFood!!.emoji, fontSize = 42.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // TOOL-SPECIFIC INTERACTIVE CONTROLS TRAY
            // ==========================================
            when (selectedTool) {
                PetTool.COMIDA -> {
                    // Food selector tray (big chunky icons)
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            foodList.forEach { food ->
                                val isSelected = activeFood?.id == food.id
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) SunnyYellow.copy(alpha = 0.5f) else Color(0xFFF5F5F5),
                                    border = if (isSelected) BorderStroke(2.5.dp, CoralOrange) else null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            activeFood = food
                                            foodPos = null
                                            soundFx.playTap()
                                            speechManager.speak("Toca o arrastra la ${food.name} a la boca.")
                                        }
                                        .testTag("food_tray_${food.id}")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = food.emoji, fontSize = 30.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                PetTool.PEINAR -> {
                    // Peinar / Styling Salon with Accessories
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🪮", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Cepilla acariciando la pantalla",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF37474F)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SunnyYellow.copy(alpha = 0.35f),
                                    modifier = Modifier.clickable {
                                        soundFx.playMagicChime()
                                        spawnParticles(Offset(screenW * 0.5f, screenH * 0.35f), "✨", 6)
                                        groomProgress = 1f
                                        currentEmotion = PetEmotion.RADIANTE
                                        rewardAction("¡Pelaje súper peinado y reluciente, $childName!", PetEmotion.RADIANTE)
                                    }
                                ) {
                                    Text(
                                        text = "✨ ¡Peinar!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF795548),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Accessories selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PetAccessory.values().forEach { acc ->
                                    val isEquipped = equippedAccessory == acc
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isEquipped) CandyPink else Color(0xFFF0F0F0),
                                        border = if (isEquipped) BorderStroke(2.dp, Color.White) else null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clickable {
                                                equippedAccessory = if (isEquipped) null else acc
                                                soundFx.playPop()
                                                if (!isEquipped) {
                                                    speechManager.speak("¡Pusiste la ${acc.label}!")
                                                }
                                            }
                                            .testTag("acc_btn_${acc.id}")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = acc.emoji, fontSize = 22.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                PetTool.BANIO -> {
                    // 3-Step Spa Salon Buttons
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BathStep.values().forEach { step ->
                                val isActive = currentBathStep == step
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isActive) SkyBlue else Color(0xFFF5F5F5),
                                    modifier = Modifier
                                        .clickable {
                                            currentBathStep = step
                                            soundFx.playTap()
                                            if (step == BathStep.SECAR) {
                                                soapSuds.clear()
                                                soundFx.playStarFanfare()
                                                rewardAction("¡Qué hermosa quedó limpia y reluciente, $childName!", PetEmotion.RADIANTE)
                                            }
                                        }
                                        .testTag("bath_step_${step.name}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = step.icon, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = step.toolName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isActive) Color.White else Color(0xFF37474F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                PetTool.MIMOS -> {
                    // Affection & Bedtime Actions
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Night Nap Switch
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSleeping) Color(0xFF1E293B) else SkyBlue.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable {
                                        isSleeping = !isSleeping
                                        soundFx.playTap()
                                        if (isSleeping) {
                                            currentEmotion = PetEmotion.SUEÑO
                                            soundFx.playSnore()
                                            speechManager.speak("Buenas noches, $childName. Tu mascota va a descansar con su mantita.")
                                        } else {
                                            currentEmotion = PetEmotion.FELIZ
                                            soundFx.playSuccess()
                                            speechManager.speak("¡Buenos días, $childName! ¡Tu mascota despertó feliz!")
                                        }
                                    }
                                    .testTag("toggle_sleep_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = if (isSleeping) "☀️" else "🌙", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isSleeping) "Despertar" else "Arrullar",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSleeping) Color.White else Color(0xFF0288D1)
                                    )
                                }
                            }

                            // Big Hug Button
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CandyPink.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable {
                                        if (isSleeping) {
                                            soundFx.playSnore()
                                            return@clickable
                                        }
                                        soundFx.playPurr()
                                        spawnParticles(Offset(screenW * 0.5f, screenH * 0.35f), "💖", 6)
                                        coroutineScope.launch {
                                            rotZ.animateTo(14f, tween(100))
                                            rotZ.animateTo(-14f, tween(100))
                                            rotZ.animateTo(0f, tween(100))
                                        }
                                        rewardAction("¡Abrazo gigante y amoroso para tu mascota!", PetEmotion.ENAMORADO)
                                    }
                                    .testTag("hug_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🤗", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "¡Abrazo!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CandyPink
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // PRIMARY TOOLS SELECTOR (CHUNKY BOTTOM TABS)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val toolColors = listOf(CoralOrange, SunnyYellow, SkyBlue, CandyPink)
                PetTool.values().forEachIndexed { index, tool ->
                    val isSelected = selectedTool == tool
                    val btnColor = toolColors[index % toolColors.size]

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) btnColor else btnColor.copy(alpha = 0.22f),
                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .padding(horizontal = 3.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable {
                                selectedTool = tool
                                soundFx.playTap()
                                when (tool) {
                                    PetTool.COMIDA -> speechManager.speak("Elige una fruta rica para alimentar a tu mascota.")
                                    PetTool.PEINAR -> speechManager.speak("Usa el cepillo mágico para peinar su hermoso pelaje.")
                                    PetTool.BANIO -> {
                                        currentBathStep = BathStep.ENJABONAR
                                        soapProgress = 0f
                                        soapSuds.clear()
                                        speechManager.speak("Frota con la esponja para hacer muchas burbujas.")
                                    }
                                    PetTool.MIMOS -> speechManager.speak("Acaricia a tu mascota o juega con la pelota.")
                                }
                            }
                            .testTag("pet_main_tool_${tool.name}")
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = tool.emoji, fontSize = 24.sp)
                            Text(
                                text = tool.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else Color(0xFF263238)
                            )
                        }
                    }
                }
            }
        }
    }
}
