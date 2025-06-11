package com.healthapp.emotional.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthapp.emotional.data.models.SessionStatus
import com.healthapp.emotional.data.models.Therapist
import com.healthapp.emotional.data.models.TherapySession
import com.healthapp.emotional.ui.viewmodels.AuthViewModel
import com.healthapp.emotional.ui.viewmodels.SessionsViewModel
import com.healthapp.emotional.ui.viewmodels.SessionsUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.*
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionsViewModel = hiltViewModel()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val uiState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var sessionToDelete by remember { mutableStateOf<TherapySession?>(null) }
    var sessionToComplete by remember { mutableStateOf<TherapySession?>(null) }
    var sessionToEdit by remember { mutableStateOf<TherapySession?>(null) }
    var sessionToJoin by remember { mutableStateOf<TherapySession?>(null) }
    var filterDate by remember { mutableStateOf<LocalDate?>(null) }
    var showPastSessions by remember { mutableStateOf(false) }
    
    // Efeito para carregar dados quando o usuário estiver autenticado
    LaunchedEffect(authState.currentUser) {
        android.util.Log.d("SessionsScreen", "AuthState atualizado: ${authState.currentUser?.id}")
        authState.currentUser?.let { user ->
            // Carregar as sessões do usuário
            sessionViewModel.loadUserSessions(user.id)
            // Carregar terapeutas disponíveis
            sessionViewModel.loadTherapists()
        }
    }
    
    // Filtrar sessões por data, se uma data estiver selecionada
    val filteredUpcomingSessions = if (filterDate != null) {
        uiState.upcomingSessions.filter { session -> session.date.isEqual(filterDate) }
    } else {
        uiState.upcomingSessions
    }
    
    val filteredPastSessions = if (filterDate != null) {
        uiState.pastSessions.filter { session -> session.date.isEqual(filterDate) }
    } else {
        uiState.pastSessions
    }
    
    // Verificar se há sessões hoje
    val today = LocalDate.now()
    val hasSessionsToday = uiState.upcomingSessions.any { session -> session.date.isEqual(today) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabeçalho com saudação e ícone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Minhas Sessões",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    
                    val subtitleText = when {
                        hasSessionsToday -> "Você tem sessões hoje!"
                        uiState.upcomingSessions.isNotEmpty() -> "Próxima sessão em ${getDaysUntilNextSession(uiState.upcomingSessions.first().date)} dias"
                        else -> "Gerencie suas sessões de terapia"
                    }
                    
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Calendário rápido - mostra próximos 7 dias
            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // Adicionar opção "Todas"
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        // Espaço em branco para alinhar com o dia da semana
                        Text(
                            text = " ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Surface(
                            onClick = { filterDate = null },
                            shape = RoundedCornerShape(16.dp),
                            color = if (filterDate == null) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant,
                            border = if (filterDate == null) 
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary) 
                            else 
                                null,
                            modifier = Modifier
                                .width(72.dp)
                                .height(80.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Todas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (filterDate == null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (filterDate == null) 
                                        MaterialTheme.colorScheme.onPrimaryContainer 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Adicionar dias
                val today = LocalDate.now()
                items(7) { dayOffset ->
                    val date = today.plusDays(dayOffset.toLong())
                    val hasSessions = uiState.upcomingSessions.any { it.date.isEqual(date) }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR")),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (filterDate == date) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Surface(
                            onClick = { filterDate = if (filterDate == date) null else date },
                            shape = RoundedCornerShape(16.dp),
                            color = if (filterDate == date) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant,
                            border = if (date.isEqual(today)) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .width(72.dp)
                                .height(80.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = when {
                                        date.isEqual(today) -> "Hoje"
                                        date.isEqual(today.plusDays(1)) -> "Amanhã"
                                        else -> date.format(DateTimeFormatter.ofPattern("dd/MM"))
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (date.isEqual(today)) FontWeight.Bold else FontWeight.Normal
                                )
                                
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                if (hasSessions) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mostrar mensagem de erro, se houver
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(onClick = { sessionViewModel.clearErrorMessage() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (uiState.isLoading) {
                // Mostrar indicador de carregamento
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
            } else if (filteredUpcomingSessions.isEmpty() && filteredPastSessions.isEmpty()) {
                // Mostrar tela vazia com animação
                EmptySessionsView(
                    modifier = Modifier.weight(1f),
                    onAddClick = { sessionViewModel.showNewSessionDialog() },
                    filterDate = filterDate
                )
            } else {
                // Mostrar lista de sessões com animação e design aprimorado
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (filteredUpcomingSessions.isNotEmpty()) {
                        item {
                            CategoryHeader(
                                title = "Próximas Sessões",
                                count = filteredUpcomingSessions.size
                            )
                        }
                        
                        items(
                            items = filteredUpcomingSessions,
                            key = { session -> "${session.id}_${session.date}_${session.startTime}" }
                        ) { session ->
                            SessionCard(
                                session = session,
                                onCancelClick = { 
                                    sessionToDelete = session
                                    showDeleteConfirmDialog = true
                                },
                                onCompleteClick = {
                                    sessionToComplete = session
                                    showCompleteDialog = true
                                },
                                onEditClick = {
                                    sessionToEdit = session
                                    sessionViewModel.showEditSessionDialog(session)
                                },
                                onJoinClick = {
                                    sessionToJoin = session
                                }
                            )
                        }
                    }
                    
                    if (filteredPastSessions.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPastSessions = !showPastSessions }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryHeader(
                                    title = "Sessões Anteriores",
                                    count = filteredPastSessions.size
                                )
                                
                                Icon(
                                    imageVector = if (showPastSessions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (showPastSessions) "Recolher" else "Expandir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        if (showPastSessions) {
                            items(
                                items = filteredPastSessions,
                                key = { session -> session.id }
                            ) { session ->
                                SessionCard(
                                    session = session,
                                    isPast = true,
                                    onCancelClick = { },
                                    onCompleteClick = { },
                                    onEditClick = { },
                                    onJoinClick = { }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Botão flutuante para adicionar nova sessão
        FloatingActionButton(
            onClick = { sessionViewModel.showNewSessionDialog() },
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agendar Nova Sessão"
            )
        }
    }
    
    // Diálogo para agendar nova sessão
    if (uiState.showNewSessionDialog) {
        NewSessionDialog(
            uiState = uiState,
            onDismiss = { 
                android.util.Log.d("SessionsScreen", "Diálogo fechado")
                sessionViewModel.hideNewSessionDialog()
                sessionToEdit = null
            },
            onDateSelect = { 
                android.util.Log.d("SessionsScreen", "Data selecionada: $it")
                sessionViewModel.selectDate(it) 
            },
            onTherapistSelect = { 
                android.util.Log.d("SessionsScreen", "Terapeuta selecionado: ${it?.name}")
                sessionViewModel.selectTherapist(it) 
            },
            onTimeSelect = { 
                android.util.Log.d("SessionsScreen", "Horário selecionado: $it")
                sessionViewModel.selectTime(it) 
            },
            onNotesChange = { 
                android.util.Log.d("SessionsScreen", "Notas atualizadas: $it")
                sessionViewModel.updateNotes(it) 
            },
            onConfirm = {
                android.util.Log.d("SessionsScreen", "Botão de confirmar clicado")
                if (authState.currentUser == null) {
                    android.util.Log.e("SessionsScreen", "Usuário não encontrado, tentando recarregar...")
                    authViewModel.loadCurrentUser()
                    // Aguardar um momento para o usuário ser carregado
                    scope.launch {
                        kotlinx.coroutines.delay(1000) // 1 segundo
                        authState.currentUser?.let { user ->
                            android.util.Log.d("SessionsScreen", "Usuário encontrado após recarregar: ${user.id}")
                            if (sessionToEdit != null) {
                                android.util.Log.d("SessionsScreen", "Atualizando sessão existente")
                                sessionViewModel.updateExistingSession(sessionToEdit!!, uiState.notes)
                            } else {
                                android.util.Log.d("SessionsScreen", "Criando nova sessão")
                                sessionViewModel.addSession(user.id, uiState.notes)
                            }
                        } ?: run {
                            android.util.Log.e("SessionsScreen", "Usuário não encontrado após tentativa de recarregar")
                            sessionViewModel.showErrorMessage("Erro: Usuário não autenticado. Por favor, faça login novamente.")
                        }
                    }
                } else {
                    android.util.Log.d("SessionsScreen", "Usuário encontrado: ${authState.currentUser?.id}")
                    if (sessionToEdit != null) {
                        android.util.Log.d("SessionsScreen", "Atualizando sessão existente")
                        sessionViewModel.updateExistingSession(sessionToEdit!!, uiState.notes)
                    } else {
                        android.util.Log.d("SessionsScreen", "Criando nova sessão")
                        sessionViewModel.addSession(authState.currentUser!!.id, uiState.notes)
                    }
                }
            },
            sessionToEdit = sessionToEdit
        )
    }
    
    // Diálogo de confirmação para excluir sessão
    if (showDeleteConfirmDialog && sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmDialog = false
                sessionToDelete = null
            },
            title = { Text("Cancelar Sessão") },
            text = { 
                Text("Tem certeza que deseja cancelar sua sessão com ${sessionToDelete?.therapistName} em ${sessionToDelete?.date?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}?") 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        sessionToDelete?.let { session ->
                            authState.currentUser?.let { user ->
                                sessionViewModel.cancelSession(session.id, user.id)
                            }
                        }
                        showDeleteConfirmDialog = false
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar Sessão")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteConfirmDialog = false
                    sessionToDelete = null 
                }) {
                    Text("Voltar")
                }
            }
        )
    }
    
    // Diálogo de confirmação para marcar sessão como concluída
    if (showCompleteDialog && sessionToComplete != null) {
        AlertDialog(
            onDismissRequest = { 
                showCompleteDialog = false
                sessionToComplete = null
            },
            title = { Text("Concluir Sessão") },
            text = { 
                Text("Deseja marcar esta sessão como concluída?") 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        sessionToComplete?.let { session ->
                            authState.currentUser?.let { user ->
                                sessionViewModel.completeSession(session.id, user.id)
                            }
                        }
                        showCompleteDialog = false
                        sessionToComplete = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCompleteDialog = false
                    sessionToComplete = null 
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo para configuração de áudio/vídeo
    if (sessionToJoin != null) {
        AlertDialog(
            onDismissRequest = { sessionToJoin = null },
            title = { Text("Configuração de Áudio/Vídeo") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Preview da câmera
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Controles de áudio/vídeo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botão de câmera
                        IconButton(
                            onClick = { /* Toggle camera */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Câmera",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Botão de microfone
                        IconButton(
                            onClick = { /* Toggle microphone */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microfone",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Botão de alto-falante
                        IconButton(
                            onClick = { /* Toggle speaker */ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Alto-falante",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Seleção de câmera
                    OutlinedTextField(
                        value = "Câmera padrão",
                        onValueChange = { },
                        label = { Text("Câmera") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { /* Show camera options */ }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Selecionar câmera"
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Seleção de microfone
                    OutlinedTextField(
                        value = "Microfone padrão",
                        onValueChange = { },
                        label = { Text("Microfone") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { /* Show microphone options */ }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Selecionar microfone"
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Seleção de alto-falante
                    OutlinedTextField(
                        value = "Alto-falante padrão",
                        onValueChange = { },
                        label = { Text("Alto-falante") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { /* Show speaker options */ }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Selecionar alto-falante"
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { sessionToJoin = null },
                        colors = ButtonDefaults.outlinedButtonColors(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Voltar")
                    }
                    
                    Button(
                        onClick = { 
                            // Aqui você implementaria a lógica para entrar na sessão
                            sessionToJoin = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Entrar")
                    }
                }
            },
            dismissButton = { }
        )
    }
}

// Função auxiliar para calcular dias até a próxima sessão
private fun getDaysUntilNextSession(date: LocalDate): Int {
    val today = LocalDate.now()
    return ChronoUnit.DAYS.between(today, date).toInt()
}

@Composable
fun EmptySessionsView(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
    filterDate: LocalDate?
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val titleText = if (filterDate != null) {
                "Nenhuma sessão em ${filterDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
            } else {
                "Nenhuma sessão agendada"
            }
            
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val messageText = if (filterDate != null) {
                "Tente selecionar outra data ou agende uma nova sessão"
            } else {
                "Agende sua primeira sessão de terapia e comece sua jornada de bem-estar emocional"
            }
            
            Text(
                text = messageText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Agendar Nova Sessão")
            }
        }
    }
}

@Composable
fun CategoryHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SessionCard(
    session: TherapySession,
    isPast: Boolean = false,
    onCancelClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    
    var expanded by remember { mutableStateOf(false) }
    
    val cardColors = CardDefaults.cardColors(
        containerColor = when {
            session.status == SessionStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            session.status == SessionStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)
            isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = cardColors,
        shape = RoundedCornerShape(16.dp),
        border = when {
            session.status == SessionStatus.CANCELLED -> BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            session.status == SessionStatus.COMPLETED -> BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
            else -> null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when (session.status) {
                                SessionStatus.SCHEDULED -> MaterialTheme.colorScheme.primary
                                SessionStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                                SessionStatus.CANCELLED -> MaterialTheme.colorScheme.error
                            }
                        )
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // Title with therapist name
                    Text(
                        text = session.therapistName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Date and time
                    Text(
                        text = "${session.date.format(dateFormatter)} às ${session.startTime.format(timeFormatter)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Status badge
                SessionStatusBadge(status = session.status)
                
                // Expand/collapse icon
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Mostrar menos" else "Mostrar mais"
                    )
                }
            }
            
            // Expanded content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Session details
                    SessionDetailItem(
                        icon = Icons.Default.Place,
                        label = "Local",
                        value = session.location
                    )
                    
                    SessionDetailItem(
                        icon = Icons.Default.Timer,
                        label = "Duração",
                        value = "${session.duration} minutos"
                    )
                    
                    if (session.notes.isNotBlank()) {
                        SessionDetailItem(
                            icon = Icons.Default.Notes,
                            label = "Notas",
                            value = session.notes
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action buttons for upcoming sessions
                    if (!isPast && session.status == SessionStatus.SCHEDULED) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Join button - only for today's sessions
                            val today = LocalDate.now()
                            if (session.date.isEqual(today)) {
                                Button(
                                    onClick = onJoinClick,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VideoCall,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Entrar")
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            
                            // Complete button - only for today or past sessions
                            if (!session.date.isAfter(today)) {
                                Button(
                                    onClick = onCompleteClick,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Concluída")
                                }
                            } else {
                                // Edit button - only show if not completed
                                OutlinedButton(
                                    onClick = onEditClick,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Editar")
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // Cancel button for future sessions
                                Button(
                                    onClick = onCancelClick,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SessionStatusBadge(status: SessionStatus) {
    val (backgroundColor, contentColor, text) = when (status) {
        SessionStatus.SCHEDULED -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            "Agendada"
        )
        SessionStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.tertiary,
            "Concluída"
        )
        SessionStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            "Cancelada"
        )
    }
    
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionDialog(
    uiState: SessionsUiState,
    onDismiss: () -> Unit,
    onDateSelect: (LocalDate?) -> Unit,
    onTherapistSelect: (Therapist?) -> Unit,
    onTimeSelect: (String?) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    sessionToEdit: TherapySession? = null
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val today = LocalDate.now()
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog header
                Text(
                    text = if (sessionToEdit != null) "Editar Sessão" else "Agendar Nova Sessão",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Step indicator
                StepIndicator(
                    currentStep = when {
                        uiState.selectedTherapist == null -> 1
                        uiState.selectedDate == null -> 2
                        uiState.selectedTime == null -> 3
                        else -> 4
                    },
                    totalSteps = 4
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seleção de terapeuta
                Text(
                    text = "1. Escolha um Terapeuta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.availableTherapists.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Person,
                        message = "Nenhum terapeuta disponível no momento"
                    )
                } else {
                    // Lista de terapeutas com mais informações
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            uiState.availableTherapists.forEachIndexed { index, therapist ->
                                TherapistItem(
                                    therapist = therapist,
                                    isSelected = uiState.selectedTherapist?.id == therapist.id,
                                    onClick = { onTherapistSelect(therapist) }
                                )
                                
                                if (index < uiState.availableTherapists.size - 1) {
                                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seleção de data
                Text(
                    text = "2. Escolha uma Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.selectedTherapist == null) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.selectedTherapist == null) {
                    Text(
                        text = "Selecione um terapeuta primeiro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    // Calendário rápido na forma de grid para próximos 14 dias
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val dates = List(14) { today.plusDays(it.toLong()) }
                        items(dates) { date ->
                            DateSelectionChip(
                                date = date,
                                selected = uiState.selectedDate == date,
                                onClick = { onDateSelect(date) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Seleção de horário
                Text(
                    text = "3. Escolha um Horário",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.selectedDate == null) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.selectedDate == null) {
                    Text(
                        text = "Selecione uma data primeiro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else if (uiState.availableTimes.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Schedule,
                        message = "Não há horários disponíveis para esta data"
                    )
                } else {
                    // Grid de horários disponíveis
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.availableTimes,
                            key = { time -> time.toString() }
                        ) { time ->
                            val timeStr = "${time.hour}:${time.minute.toString().padStart(2, '0')}"
                            TimeSelectionChip(
                                time = timeStr,
                                selected = uiState.selectedTime == time,
                                onClick = { onTimeSelect(timeStr) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Notas
                Text(
                    text = "4. Adicione Notas (opcional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas da sessão") },
                    placeholder = { Text("Ex: Assuntos a discutir, perguntas, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Resumo da sessão, se todos os campos obrigatórios estiverem preenchidos
                if (uiState.selectedTherapist != null && uiState.selectedDate != null && uiState.selectedTime != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Resumo da Sessão",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = uiState.selectedTherapist?.name ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = uiState.selectedDate?.format(dateFormatter) ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                val formattedTime = uiState.selectedTime?.let {
                                    "${it.hour}:${it.minute.toString().padStart(2, '0')}"
                                } ?: ""
                                
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Botões de ação
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        enabled = uiState.selectedTherapist != null && 
                                 uiState.selectedDate != null && 
                                 uiState.selectedTime != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (sessionToEdit != null) "Atualizar" else "Agendar")
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..totalSteps) {
            val isCompleted = i < currentStep
            val isCurrent = i == currentStep
            
            val color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isCurrent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.outlineVariant
            }
            
            Box(
                modifier = Modifier
                    .size(if (isCurrent) 16.dp else 12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TherapistItem(
    therapist: Therapist,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto do terapeuta
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (therapist.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(therapist.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informações do terapeuta
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = therapist.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = therapist.specialization,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    Text(
                        text = String.format("%.1f", therapist.rating),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = if (therapist.isOnline) Icons.Default.VideoCall else Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    Text(
                        text = if (therapist.isOnline) "Online" else "Presencial",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) 
                        else 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "R$ ${String.format("%.2f", therapist.price)}/sessão",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.primary
                )
            }
            
            // Indicador de seleção
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DateSelectionChip(
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date.isEqual(today)
    
    val displayDate = when {
        isToday -> "Hoje"
        date.isEqual(today.plusDays(1)) -> "Amanhã"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, dd/MM", Locale("pt", "BR")))
    }
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TimeSelectionChip(
    time: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
} 