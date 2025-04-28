package com.healthapp.emotional.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthapp.emotional.data.models.MoodEntry
import com.healthapp.emotional.ui.viewmodels.AuthViewModel
import com.healthapp.emotional.ui.viewmodels.MoodViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun MoodScreen(
    modifier: Modifier = Modifier,
    moodEntries: List<MoodEntry> = emptyList()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val moodViewModel: MoodViewModel = hiltViewModel()
    
    val dbMoodEntries by moodViewModel.moodEntries.collectAsState()
    val isLoading by moodViewModel.isLoading.collectAsState()
    
    // Estado para formulário de registro de humor
    var showMoodDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<MoodEntry?>(null) }
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("") }
    var selectedMoodEntry by remember { mutableStateOf<MoodEntry?>(null) }
    
    // Carregar entradas de humor quando o usuário estiver autenticado
    val currentUser = authViewModel.authState.collectAsState().value.currentUser
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            moodViewModel.loadMoodEntries(user.id)
        }
    }
    
    // Calcular estatísticas de humor
    val averageMood by remember(dbMoodEntries) {
        val average = if (dbMoodEntries.isNotEmpty()) {
            dbMoodEntries.map { it.intensity }.average()
        } else 0.0
        mutableStateOf(average)
    }
    
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.background
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabeçalho com estatísticas
            MoodHeader(
                averageMood = averageMood,
                entriesCount = dbMoodEntries.size,
                onAddClick = { showMoodDialog = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Visão geral semanal
            WeeklyMoodOverview(
                moodEntries = dbMoodEntries,
                days = 7
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de entradas de humor
            Text(
                text = "Registro de Humor",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (dbMoodEntries.isEmpty()) {
                EmptyMoodState(onAddClick = { showMoodDialog = true })
            } else {
                // Coletar datas únicas e entradas organizadas por data
                val groupedEntries = remember(dbMoodEntries) {
                    dbMoodEntries.groupBy { entry ->
                        LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(entry.timestamp),
                            ZoneId.systemDefault()
                        ).toLocalDate()
                    }.toSortedMap(compareByDescending { it })
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val sortedDates = groupedEntries.keys.toList()
                    sortedDates.forEach { date ->
                        item {
                            DateHeader(date = date)
                        }
                        
                        val entriesForDate = groupedEntries[date] ?: emptyList()
                        itemsIndexed(entriesForDate) { _, entry ->
                            MoodEntryCard(
                                entry = entry,
                                onClick = { 
                                    selectedMoodEntry = entry
                                    selectedEmoji = entry.emoji
                                    selectedLabel = entry.label
                                    showMoodDialog = true 
                                },
                                onDeleteClick = {
                                    entryToDelete = entry
                                    showDeleteConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Botão flutuante para adicionar novo registro de humor
        FloatingActionButton(
            onClick = { showMoodDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar humor")
        }
    }
    
    // Diálogo para registrar humor
    if (showMoodDialog) {
        EnhancedMoodDialog(
            initialEmoji = selectedEmoji,
            initialLabel = selectedLabel,
            initialNote = selectedMoodEntry?.note ?: "",
            isEditing = selectedMoodEntry != null,
            onDismiss = { 
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                selectedMoodEntry = null
            },
            onConfirm = { emoji, label, note, intensity ->
                currentUser?.let { user ->
                    if (selectedMoodEntry != null) {
                        // Editar entrada existente
                        val updatedEntry = selectedMoodEntry!!.copy(
                            emoji = emoji,
                            label = label,
                            intensity = intensity.toInt(),
                            note = note
                        )
                        moodViewModel.updateMoodEntry(updatedEntry)
                    } else {
                        // Criar nova entrada
                        moodViewModel.recordMood(
                            userId = user.id,
                            emoji = emoji,
                            label = label,
                            intensity = intensity.toInt(),
                            note = note
                        )
                    }
                }
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                selectedMoodEntry = null
            }
        )
    }
    
    // Diálogo de confirmação para excluir entrada
    if (showDeleteConfirmDialog && entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmDialog = false
                entryToDelete = null
            },
            title = { Text("Excluir Registro") },
            text = { 
                Text("Tem certeza que deseja excluir este registro de humor?") 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        entryToDelete?.let { entry ->
                            moodViewModel.deleteMoodEntry(entry)
                        }
                        showDeleteConfirmDialog = false
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { 
                        showDeleteConfirmDialog = false
                        entryToDelete = null 
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MoodHeader(
    averageMood: Double,
    entriesCount: Int,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meu Humor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Registrar")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estatísticas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Humor médio
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Humor médio",
                value = if (averageMood > 0) getMoodTextFromIntensity(averageMood.toFloat()) else "N/A",
                icon = Icons.Rounded.Face,
                backgroundColor = if (averageMood > 0) getMoodColor(averageMood.toFloat()) else MaterialTheme.colorScheme.surfaceVariant
            )
            
            // Contagem de registros
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Registros",
                value = entriesCount.toString(),
                icon = Icons.Rounded.Article,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = backgroundColor,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeeklyMoodOverview(
    moodEntries: List<MoodEntry>,
    days: Int = 7
) {
    val today = LocalDate.now()
    val daysToShow = (0 until days).map { today.minusDays(it.toLong()) }.reversed()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Visão Semanal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysToShow.forEach { date ->
                    val dayEntries = moodEntries.filter {
                        val entryDate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(it.timestamp),
                            ZoneId.systemDefault()
                        ).toLocalDate()
                        entryDate == date
                    }
                    
                    val averageMood = if (dayEntries.isNotEmpty()) {
                        dayEntries.map { it.intensity }.average().toFloat()
                    } else null
                    
                    DayMoodIndicator(
                        date = date,
                        mood = averageMood,
                        isToday = date == today,
                        entriesCount = dayEntries.size
                    )
                }
            }
        }
    }
}

@Composable
fun DayMoodIndicator(
    date: LocalDate,
    mood: Float?,
    isToday: Boolean,
    entriesCount: Int
) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
    val dayOfMonth = date.dayOfMonth.toString()
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.bodySmall,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Indicador do dia
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayOfMonth,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Indicador de humor
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (mood != null) getMoodColor(mood).copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (mood != null) {
                val emoji = getMoodEmojiFromIntensity(mood)
                Text(
                    text = emoji,
                    fontSize = 18.sp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Número de entradas
        Text(
            text = if (entriesCount > 0) entriesCount.toString() else "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    
    val formattedDate = when {
        date.isEqual(today) -> "Hoje"
        date.isEqual(yesterday) -> "Ontem"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pt", "BR")))
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(0.2f)
                .padding(end = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun MoodEntryCard(
    entry: MoodEntry,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val entryTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(entry.timestamp),
        ZoneId.systemDefault()
    )
    val formattedTime = entryTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji e intensidade
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(getMoodColor(entry.intensity.toFloat()).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.emoji,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Detalhes do humor
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (entry.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            // Botão de exclusão
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyMoodState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MoodBad,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Nenhum registro de humor",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Acompanhe como você está se sentindo ao longo do tempo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Registrar meu humor agora")
        }
    }
}

@Composable
fun EnhancedMoodDialog(
    initialEmoji: String = "",
    initialLabel: String = "",
    initialNote: String = "",
    isEditing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (emoji: String, label: String, note: String, intensity: Float) -> Unit
) {
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    var moodLabel by remember { mutableStateOf(initialLabel) }
    var selectedIntensity by remember { mutableStateOf(if (initialLabel.isNotEmpty()) getMoodIntensityFromLabel(initialLabel) else 3f) }
    var moodNote by remember { mutableStateOf(initialNote) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditing) "Editar Registro" else "Como você está se sentindo?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Emoji selector
                val emojis = listOf("😍", "😊", "🙂", "😐", "🙁", "😢", "😡")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(emojis) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.2f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) getMoodColor(getMoodIntensityFromEmoji(emoji)).copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                .clickable {
                                    selectedEmoji = emoji
                                    // Update intensity and label based on emoji
                                    selectedIntensity = getMoodIntensityFromEmoji(emoji)
                                    moodLabel = getMoodLabelFromEmoji(emoji)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 30.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Mood label
                OutlinedTextField(
                    value = moodLabel,
                    onValueChange = { moodLabel = it },
                    label = { Text("Como você se sente") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getMoodColor(selectedIntensity),
                        focusedLabelColor = getMoodColor(selectedIntensity)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Mood note
                OutlinedTextField(
                    value = moodNote,
                    onValueChange = { moodNote = it },
                    label = { Text("Observações (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getMoodColor(selectedIntensity),
                        focusedLabelColor = getMoodColor(selectedIntensity)
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = {
                            onConfirm(
                                selectedEmoji,
                                moodLabel,
                                moodNote,
                                selectedIntensity
                            )
                        },
                        enabled = selectedEmoji.isNotEmpty() && moodLabel.isNotEmpty(),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getMoodColor(selectedIntensity)
                        )
                    ) {
                        Text(if (isEditing) "Atualizar" else "Salvar")
                    }
                }
            }
        }
    }
}

// Funções de utilidade para cores e etiquetas de humor
fun getMoodColor(intensity: Float): Color {
    return when {
        intensity >= 6f -> Color(0xFF4CAF50) // Excelente/Muito feliz - Verde
        intensity >= 5f -> Color(0xFF8BC34A) // Ótimo/Feliz - Verde claro
        intensity >= 4f -> Color(0xFFCDDC39) // Bom/Contente - Verde amarelado
        intensity >= 3f -> Color(0xFFFFC107) // Neutro/Ok - Amarelo
        intensity >= 2f -> Color(0xFFFF9800) // Ruim/Triste - Laranja
        intensity >= 1f -> Color(0xFFFF5722) // Péssimo/Muito triste - Laranja avermelhado
        else -> Color(0xFFF44336) // Terrível/Angústiado - Vermelho
    }
}

fun getMoodTextFromIntensity(intensity: Float): String {
    return when {
        intensity >= 6f -> "Excelente"
        intensity >= 5f -> "Ótimo"
        intensity >= 4f -> "Bom"
        intensity >= 3f -> "Neutro"
        intensity >= 2f -> "Ruim"
        intensity >= 1f -> "Péssimo"
        else -> "Terrível"
    }
}

fun getMoodEmojiFromIntensity(intensity: Float): String {
    return when {
        intensity >= 6f -> "😍"
        intensity >= 5f -> "😊"
        intensity >= 4f -> "🙂"
        intensity >= 3f -> "😐"
        intensity >= 2f -> "🙁"
        intensity >= 1f -> "😢"
        else -> "😡"
    }
}

fun getMoodIntensityFromEmoji(emoji: String): Float {
    return when (emoji) {
        "😍" -> 6.5f
        "😊" -> 5.5f
        "🙂" -> 4.5f
        "😐" -> 3.5f
        "🙁" -> 2.5f
        "😢" -> 1.5f
        "😡" -> 0.5f
        else -> 3.5f
    }
}

fun getMoodLabelFromEmoji(emoji: String): String {
    return when (emoji) {
        "😍" -> "Muito feliz"
        "😊" -> "Feliz"
        "🙂" -> "Contente"
        "😐" -> "Neutro"
        "🙁" -> "Triste"
        "😢" -> "Muito triste"
        "😡" -> "Frustrado"
        else -> ""
    }
}

fun getMoodIntensityFromLabel(label: String): Float {
    return when (label.lowercase()) {
        "muito feliz" -> 6.5f
        "feliz" -> 5.5f
        "contente" -> 4.5f
        "neutro" -> 3.5f
        "triste" -> 2.5f
        "muito triste" -> 1.5f
        "frustrado" -> 0.5f
        else -> 3.5f
    }
} 