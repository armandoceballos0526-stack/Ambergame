package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundFxManager
import com.example.audio.SpeechManager
import com.example.ui.theme.CandyPink
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow

private data class PaintLine(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

private data class CanvasStamp(
    val id: Long,
    val x: Float,
    val y: Float,
    val emoji: String
)

@Composable
fun MagicPaintScreen(
    childName: String,
    soundFx: SoundFxManager,
    speechManager: SpeechManager,
    onCompleteDrawing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lines = remember { mutableStateListOf<PaintLine>() }
    val stamps = remember { mutableStateListOf<CanvasStamp>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }
    var nextStampId by remember { mutableStateOf(1L) }

    val colors = listOf(
        CandyPink,
        SunnyYellow,
        SkyBlue,
        MintGreen,
        Color(0xFFAA00FF), // Violet
        Color(0xFFFF6D00)  // Orange
    )
    var selectedColor by remember { mutableStateOf(colors[0]) }

    val availableStamps = listOf(
        Pair("🦄", "unicornio"),
        Pair("🐱", "gatito"),
        Pair("🐶", "perrito"),
        Pair("🐼", "panda"),
        Pair("⭐", "estrella"),
        Pair("🌸", "flor"),
        Pair("🍭", "paleta"),
        Pair("🌈", "arcoíris")
    )
    // selectedStamp is null when using Brush mode
    var selectedStamp by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        speechManager.speak("¡Amber, dibuja con tus colores mágicos o elige un sellito para estampar!")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDE7))
            .padding(12.dp)
    ) {
        // Top Toolbar: Colors, Brush selection, Undo, and Clear
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brush toggle button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedStamp == null) CandyPink else Color(0xFFF0F0F0),
                    modifier = Modifier
                        .clickable {
                            selectedStamp = null
                            soundFx.playTap()
                            speechManager.speak("Pincel para dibujar")
                        }
                        .testTag("brush_mode_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✏️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pincel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedStamp == null) Color.White else Color(0xFF37474F)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Color choices for brush
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(colors) { color ->
                        val isSelected = selectedColor == color && selectedStamp == null
                        Surface(
                            shape = CircleShape,
                            color = color,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 3.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF263238) else Color(0xFFE0E0E0),
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedColor = color
                                    selectedStamp = null
                                    soundFx.playTap()
                                }
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Undo button
                IconButton(
                    onClick = {
                        if (stamps.isNotEmpty()) {
                            stamps.removeAt(stamps.lastIndex)
                            soundFx.playPop()
                        } else if (lines.isNotEmpty()) {
                            lines.removeAt(lines.lastIndex)
                            soundFx.playTap()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE7F6))
                        .testTag("undo_canvas_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Deshacer",
                        tint = Color(0xFF5E35B1),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Clear canvas button
                IconButton(
                    onClick = {
                        lines.clear()
                        stamps.clear()
                        soundFx.playPop()
                        speechManager.speak("¡Lienzo limpio, $childName!")
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .testTag("clear_canvas_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpiar lienzo",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Drawing and Stamping Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Color.White)
                .border(3.dp, CandyPink.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
                .pointerInput(selectedStamp, selectedColor) {
                    if (selectedStamp != null) {
                        // In Stamp mode, tapping anywhere adds a stamp right at the touch location
                        detectTapGestures { offset ->
                            stamps.add(CanvasStamp(nextStampId++, offset.x, offset.y, selectedStamp!!))
                            soundFx.playPop()
                        }
                    } else {
                        // In Brush mode, dragging paints smooth colored lines
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                currentPoints.clear()
                                currentPoints.add(startOffset)
                                soundFx.playTap()
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints.add(change.position)
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    lines.add(
                                        PaintLine(
                                            points = currentPoints.toList(),
                                            color = selectedColor,
                                            strokeWidth = 24f
                                        )
                                    )
                                    currentPoints.clear()
                                }
                            }
                        )
                    }
                }
                .testTag("magic_paint_canvas")
        ) {
            // 1. Draw brush lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                lines.forEach { line ->
                    if (line.points.size > 1) {
                        for (i in 0 until line.points.size - 1) {
                            drawLine(
                                color = line.color,
                                start = line.points[i],
                                end = line.points[i + 1],
                                strokeWidth = line.strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    } else if (line.points.size == 1) {
                        drawCircle(
                            color = line.color,
                            radius = line.strokeWidth / 2,
                            center = line.points[0]
                        )
                    }
                }

                // Currently in-progress stroke
                if (currentPoints.size > 1) {
                    for (i in 0 until currentPoints.size - 1) {
                        drawLine(
                            color = selectedColor,
                            start = currentPoints[i],
                            end = currentPoints[i + 1],
                            strokeWidth = 24f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 2. Render stamps at exact pixel coordinates using IntOffset
            stamps.forEach { stamp ->
                Box(
                    modifier = Modifier
                        .offset { IntOffset((stamp.x - 32f).toInt(), (stamp.y - 32f).toInt()) }
                        .size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stamp.emoji,
                        fontSize = 38.sp
                    )
                }
            }

            // Hint when no content yet
            if (lines.isEmpty() && stamps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (selectedStamp != null) "Toca para estampar $selectedStamp" else "Dibuja con tu dedito aquí",
                        color = Color(0xFFB0BEC5),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Stamp Bar
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sellitos Mágicos (elige uno y toca el lienzo):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF455A64)
                    )
                    if (selectedStamp != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CandyPink.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Activo: $selectedStamp",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyPink,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableStamps) { (stampEmoji, stampName) ->
                        val isSelected = selectedStamp == stampEmoji
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SunnyYellow.copy(alpha = 0.35f) else Color(0xFFF5F5F5),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, CandyPink) else null,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (selectedStamp == stampEmoji) {
                                        // Toggle off back to brush
                                        selectedStamp = null
                                        soundFx.playTap()
                                    } else {
                                        selectedStamp = stampEmoji
                                        soundFx.playPop()
                                        speechManager.speak("Sello de $stampName. ¡Toca el lienzo para estampar!")
                                    }
                                }
                                .testTag("stamp_choice_$stampEmoji")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = stampEmoji, fontSize = 28.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
