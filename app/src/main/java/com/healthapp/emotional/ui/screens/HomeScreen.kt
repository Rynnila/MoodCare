package com.healthapp.emotional.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthapp.emotional.data.models.TherapySession
import com.healthapp.emotional.data.models.SessionStatus
import com.healthapp.emotional.ui.viewmodels.MainViewModel
import com.healthapp.emotional.ui.viewmodels.SessionsViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = hiltViewModel()
    val sessionsViewModel: SessionsViewModel = hiltViewModel()
    
    var showMoodDialog by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("") }
    var moodNote by remember { mutableStateOf("") }
    
    val uiState by sessionsViewModel.uiState.collectAsState()
    val nextSession = uiState.upcomingSessions.firstOrNull()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Bem-vindo!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Como você está se sentindo hoje?",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // Card da Próxima Sessão
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Navegar para tela de sessões */ },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próxima Sessão",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = "Calendário",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (nextSession != null) {
                    SessionDetails(session = nextSession)
                } else {
                    AddSessionButton { /* Navegar para tela de sessões */ }
                }
            }
        }
        
        // Mood Tracking Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Acompanhamento de Humor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showMoodDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar Humor Agora")
                }
            }
        }
        
        // Daily Tip Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Dica do Dia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lembre-se de respirar fundo e se alongar durante o dia.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // Feelings Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("😊", "😢", "😡", "😴", "😌").forEach { emoji ->
                Text(
                    text = emoji,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            selectedEmoji = emoji
                            selectedLabel = when (emoji) {
                                "😊" -> "Feliz"
                                "😢" -> "Triste"
                                "😡" -> "Irritado"
                                "😴" -> "Cansado"
                                "😌" -> "Calmo"
                                else -> ""
                            }
                            showMoodDialog = true
                        }
                )
            }
        }
    }
    
    if (showMoodDialog) {
        AlertDialog(
            onDismissRequest = { 
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                moodNote = ""
            },
            title = { Text("Como você está se sentindo?") },
            text = {
                Column {
                    Text("$selectedEmoji $selectedLabel")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = moodNote,
                        onValueChange = { moodNote = it },
                        label = { Text("Observações") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.recordMood(selectedEmoji, selectedLabel, moodNote)
                        showMoodDialog = false
                        selectedEmoji = ""
                        selectedLabel = ""
                        moodNote = ""
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showMoodDialog = false
                        selectedEmoji = ""
                        selectedLabel = ""
                        moodNote = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SessionDetails(session: TherapySession) {
    Column {
        Text(
            text = session.therapistName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = session.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Status: ${session.status.name}",
            style = MaterialTheme.typography.bodySmall,
            color = when (session.status) {
                SessionStatus.SCHEDULED -> MaterialTheme.colorScheme.primary
                SessionStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                SessionStatus.CANCELLED -> MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun AddSessionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar sessão"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Agendar Nova Sessão")
    }
} 