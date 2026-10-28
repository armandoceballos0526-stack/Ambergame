package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DifficultyLevel
import com.example.ui.theme.CandyPink
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunnyYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    childName: String,
    starsCount: Int,
    timePlayedMinutes: Int,
    totalGamesPlayed: Int,
    numbersCompleted: Int,
    vowelsCompleted: Int,
    shapesCompleted: Int,
    memoryCompleted: Int,
    bubblesPopped: Int,
    oppositesCompleted: Int = 0,
    petCareActionsCount: Int = 0,
    difficulty: DifficultyLevel,
    isVoiceEnabled: Boolean,
    isSoundFxEnabled: Boolean,
    speechRate: Float,
    onUpdateChildName: (String) -> Unit,
    onChangeDifficulty: (DifficultyLevel) -> Unit,
    onToggleVoice: (Boolean) -> Unit,
    onToggleSoundFx: (Boolean) -> Unit,
    onChangeSpeechRate: (Float) -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(childName) { mutableStateOf(childName) }
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF37474F)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍👩‍👧", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Panel de Seguimiento para Padres",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Monitorea el progreso de aprendizaje de $childName",
                            fontSize = 12.sp,
                            color = Color(0xFFB0BEC5)
                        )
                    }
                }
            }
        }

        // Child Profile Customization
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👤 Nombre de la niña",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            singleLine = true,
                            label = { Text("Nombre") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("child_name_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onUpdateChildName(nameInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = CandyPink),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("save_child_name_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guardar")
                        }
                    }
                }
            }
        }

        // Stats Overview Grid
        item {
            Text(
                text = "📊 Resumen de Actividad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Tiempo Jugado",
                    value = "$timePlayedMinutes min",
                    icon = "⏱️",
                    color = SkyBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Juegos Completados",
                    value = "$totalGamesPlayed",
                    icon = "🎯",
                    color = MintGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Estrellas Ganadas",
                    value = "$starsCount ⭐",
                    icon = "🌟",
                    color = SunnyYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Breakdown by Subject
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 Progreso por Área de Aprendizaje",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProgressRow(label = "Números y Conteo (1 al 10)", count = "$numbersCompleted rondas", icon = "🔢", color = Color(0xFFFF8F00))
                    ProgressRow(label = "Tamaños y Opuestos (Grande/Pequeño)", count = "$oppositesCompleted rondas", icon = "📏", color = MintGreen)
                    ProgressRow(label = "Las Vocales Mágicas (A E I O U)", count = "$vowelsCompleted rondas", icon = "🔤", color = CandyPink)
                    ProgressRow(label = "Figuras Geométricas y Colores", count = "$shapesCompleted rondas", icon = "🔶", color = SkyBlue)
                    ProgressRow(label = "Memoria Visual y Retención", count = "$memoryCompleted partidas", icon = "🃏", color = Color(0xFF8E24AA))
                    ProgressRow(label = "Cuidado de Mascotas y Mimos", count = "$petCareActionsCount cuidados", icon = "🐾", color = CandyPink)
                    ProgressRow(label = "Burbujas y Globos Explotados", count = "$bubblesPopped pops", icon = "🫧", color = MintGreen)
                }
            }
        }

        // Adaptive Difficulty Selector
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚙️ Nivel de Dificultad Adaptativo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Text(
                        text = "El juego sube el nivel conforme Amber acierta, o puedes fijarlo aquí:",
                        fontSize = 12.sp,
                        color = Color(0xFF546E7A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DifficultyLevel.values().forEach { level ->
                        val isSelected = difficulty == level
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SkyBlue.copy(alpha = 0.2f) else Color(0xFFF5F5F5),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SkyBlue) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onChangeDifficulty(level) }
                                .testTag("difficulty_option_${level.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = level.label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SkyBlue else Color(0xFF37474F)
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SkyBlue)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Voice and Sound Settings
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔊 Ajustes de Audio y Voz",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Instrucciones de voz en español", fontSize = 14.sp)
                        Switch(
                            checked = isVoiceEnabled,
                            onCheckedChange = { onToggleVoice(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CandyPink)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Efectos de sonido divertidos", fontSize = 14.sp)
                        Switch(
                            checked = isSoundFxEnabled,
                            onCheckedChange = { onToggleSoundFx(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = MintGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Velocidad de habla de las instrucciones (${String.format("%.2f", speechRate)}x):",
                        fontSize = 12.sp,
                        color = Color(0xFF546E7A)
                    )
                    Slider(
                        value = speechRate,
                        onValueChange = { onChangeSpeechRate(it) },
                        valueRange = 0.70f..1.10f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Safety Guarantee
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Entorno 100% Seguro para Amber",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Sin publicidad, sin compras dentro de la app, sin enlaces externos sin control parental.",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Reset progress option
        item {
            Button(
                onClick = { showResetConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("reset_progress_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar estadísticas de Amber", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showResetConfirm) {
        BasicAlertDialog(onDismissRequest = { showResetConfirm = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "¿Reiniciar progreso?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esto reiniciará las estrellas y estadísticas acumuladas de Amber. ¿Deseas continuar?",
                        fontSize = 13.sp,
                        color = Color(0xFF455A64)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showResetConfirm = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))
                        ) {
                            Text("Cancelar", color = Color(0xFF37474F))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onResetProgress()
                                showResetConfirm = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Reiniciar", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, icon: String, color: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF263238)
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF546E7A),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProgressRow(label: String, count: String, icon: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color(0xFF37474F),
                fontWeight = FontWeight.Medium
            )
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = color.copy(alpha = 0.2f)
        ) {
            Text(
                text = count,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
