package com.healthapp.emotional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthapp.emotional.ui.navigation.BottomNavItem
import com.healthapp.emotional.ui.screens.*
import com.healthapp.emotional.ui.theme.MoodCareTheme
import com.healthapp.emotional.ui.viewmodels.*
import com.healthapp.emotional.data.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.Instant
import java.util.UUID
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    @Inject lateinit var chatRepository: ChatRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Adicionar um tratamento de erro global
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val errorMessage = "ERRO: ${throwable.message}\n${throwable.stackTraceToString()}"
            android.util.Log.e("ERRO_APP", errorMessage)
            
            // Tenta salvar o erro em um arquivo para recuperação posterior
            try {
                val errorFile = java.io.File(getExternalFilesDir(null), "error_log.txt")
                errorFile.appendText("${java.util.Date()}\n$errorMessage\n\n")
            } catch (e: Exception) {
                android.util.Log.e("ERRO_APP", "Não foi possível salvar o log de erro", e)
            }
            
            // O handler padrão ainda será chamado
            android.os.Handler(mainLooper).post {
                android.widget.Toast.makeText(
                    this,
                    "Ocorreu um erro na aplicação. Veja os logs para mais detalhes.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
        
        setContent {
            MoodCareTheme {
                val navController = rememberNavController()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "main" else "login"
                ) {
                    // Tela de Login
                    composable("login") {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onNavigateToMain = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    // Tela de Cadastro
                    composable("register") {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            onNavigateToMain = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    // Tela Principal
                    composable("main") {
                        MainScreen(
                            viewModel = mainViewModel,
                            authViewModel = authViewModel,
                            chatRepository = chatRepository,
                            onLogout = {
                                authViewModel.processLogout()
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Sessions,
        BottomNavItem.Mood,
        BottomNavItem.Chat,
        BottomNavItem.Explore
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    chatRepository: ChatRepository,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("profile") {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configurações") },
                    label = { Text("Configurações") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("settings") {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Sair") },
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MoodCare") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                com.healthapp.emotional.ui.navigation.NavGraph(
                    navController = navController,
                    chatRepository = chatRepository,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    appState: com.healthapp.emotional.ui.viewmodels.AppState
) {
    var showMoodDialog by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("") }
    var moodNote by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WelcomeCard(currentMood = appState.currentMood)
        UpcomingSessionCard()
        MoodTrackingCard(onRecordNowClick = { showMoodDialog = true })
        DailyTipCard()
        FeelingsRow(
            onFeelingSelected = { emoji, label ->
                selectedEmoji = emoji
                selectedLabel = label
                showMoodDialog = true
            }
        )
    }
    
    if (showMoodDialog) {
        MoodDialog(
            initialEmoji = selectedEmoji,
            initialLabel = selectedLabel,
            onDismiss = { 
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                moodNote = ""
            },
            onConfirm = { emoji, label, note ->
                viewModel.recordMood(emoji, label, note)
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                moodNote = ""
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDialog(
    initialEmoji: String = "",
    initialLabel: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var selectedEmoji by remember { mutableStateOf(initialEmoji) }
    var selectedLabel by remember { mutableStateOf(initialLabel) }
    var note by remember { mutableStateOf("") }
    
    val emojiOptions = listOf("😃", "😊", "😐", "😔", "😡", "😰")
    val labelOptions = listOf("Feliz", "Bom", "Normal", "Triste", "Irritado", "Ansioso")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Como você está se sentindo?") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Selecione um emoji:")
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(emojiOptions) { emoji ->
                        EmojiOption(
                            emoji = emoji,
                            isSelected = selectedEmoji == emoji,
                            onClick = { 
                                selectedEmoji = emoji
                                selectedLabel = labelOptions[emojiOptions.indexOf(emoji)]
                            }
                        )
                    }
                }
                
                Text("Selecione um sentimento:")
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(labelOptions) { label ->
                        LabelChip(
                            label = label,
                            isSelected = selectedLabel == label,
                            onClick = { 
                                selectedLabel = label
                                selectedEmoji = emojiOptions[labelOptions.indexOf(label)]
                            }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Anotação (opcional)") },
                    placeholder = { Text("Como você se sente hoje?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedEmoji, selectedLabel, note) },
                enabled = selectedEmoji.isNotEmpty() && selectedLabel.isNotEmpty()
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EmojiOption(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun LabelChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun WelcomeCard(currentMood: MoodEntry?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bem-vindo!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (currentMood != null) {
                        Text(
                            text = "Seu humor atual: ${currentMood.emoji} ${currentMood.label}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(
                            text = "Como você está se sentindo hoje?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingSessionCard() {
    var showDetails by remember { mutableStateOf(false) }
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Próxima Sessão",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dra. Silva",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Quinta-feira, 15:00",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(
                    onClick = { showDetails = !showDetails }
                ) {
                    Text(if (showDetails) "Ocultar" else "Detalhes")
                }
            }
            
            AnimatedVisibility(
                visible = showDetails,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Local: Online via videochamada",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Duração: 50 minutos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Notas: Trazer registros de humor da semana",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { /* TODO: Implement reschedule */ }
                        ) {
                            Text("Reagendar")
                        }
                        
                        Button(
                            onClick = { /* TODO: Implement cancel */ }
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodTrackingCard(onRecordNowClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Acompanhamento de Humor",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Registre como você se sente todos os dias para acompanhar seu progresso emocional.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRecordNowClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Registrar agora")
            }
        }
    }
}

@Composable
fun DailyTipCard() {
    var expanded by remember { mutableStateOf(false) }
    val tipHeight by animateDpAsState(
        targetValue = if (expanded) 180.dp else 100.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Dica do Dia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A prática da gratidão diária pode melhorar significativamente seu bem-estar mental. Reserve alguns minutos para refletir sobre coisas pelas quais você é grato hoje.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.height(tipHeight),
                maxLines = if (expanded) Int.MAX_VALUE else 3
            )
            
            Text(
                text = if (expanded) "Toque para recolher" else "Toque para expandir",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun FeelingsRow(onFeelingSelected: (String, String) -> Unit) {
    Column {
        Text(
            text = "Como você está se sentindo?",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeelingItem("😊", "Feliz", selected = false, onClick = onFeelingSelected)
            FeelingItem("😐", "Normal", selected = false, onClick = onFeelingSelected)
            FeelingItem("😔", "Triste", selected = false, onClick = onFeelingSelected)
            FeelingItem("😠", "Irritado", selected = false, onClick = onFeelingSelected)
            FeelingItem("😰", "Ansioso", selected = false, onClick = onFeelingSelected)
        }
    }
}

@Composable
fun FeelingItem(
    emoji: String,
    label: String,
    selected: Boolean = false,
    onClick: (String, String) -> Unit = { _, _ -> }
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick(emoji, label) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SessionsScreen(modifier: Modifier = Modifier, sessionViewModel: SessionsViewModel) {
    var showNewSessionDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Suas Sessões",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Você não tem sessões agendadas.\nAgende uma nova sessão agora!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = { showNewSessionDialog = true }) {
                Text("Agendar Sessão")
            }
        }
        
        FloatingActionButton(
            onClick = { showNewSessionDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agendar")
        }
    }
    
    if (showNewSessionDialog) {
        AlertDialog(
            onDismissRequest = { showNewSessionDialog = false },
            title = { Text("Agendar Nova Sessão") },
            text = { 
                Text("Funcionalidade em desenvolvimento. Em breve você poderá agendar novas sessões por aqui!") 
            },
            confirmButton = {
                TextButton(onClick = { showNewSessionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun MoodScreen(
    modifier: Modifier = Modifier,
    moodLog: List<com.healthapp.emotional.data.models.MoodEntry>
) {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    
    // Obter instância do AuthViewModel e MoodViewModel
    val authViewModel: AuthViewModel = hiltViewModel()
    val moodViewModel: MoodViewModel = hiltViewModel()
    
    // Estado para formulário de registro de humor
    var showMoodDialog by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("") }
    var moodNote by remember { mutableStateOf("") }
    
    // Observar entradas de humor do banco de dados
    val dbMoodEntries by moodViewModel.moodEntries.collectAsState()
    val isLoading by moodViewModel.isLoading.collectAsState()
    
    // Carregar entradas de humor quando o usuário estiver autenticado
    val currentUser = authViewModel.authState.collectAsState().value.currentUser
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            moodViewModel.loadMoodEntries(user.id)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registro de Humor",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Botão para adicionar nova entrada de humor
        Button(
            onClick = { showMoodDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Registrar Humor Atual")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Carregando registros de humor...")
            }
        } else if (dbMoodEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum registro de humor ainda.\nComece a registrar como se sente!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dbMoodEntries.forEach { entry ->
                    MoodEntryItem(entry, formatter)
                }
            }
        }
    }
    
    // Diálogo para registrar humor
    if (showMoodDialog) {
        MoodDialog(
            initialEmoji = selectedEmoji,
            initialLabel = selectedLabel,
            onDismiss = { 
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                moodNote = ""
            },
            onConfirm = { emoji, label, note ->
                currentUser?.let { user ->
                    moodViewModel.recordMood(
                        userId = user.id,
                        emoji = emoji,
                        label = label,
                        intensity = getMoodLevelFromLabel(label),
                        note = note
                    )
                }
                showMoodDialog = false
                selectedEmoji = ""
                selectedLabel = ""
                moodNote = ""
            }
        )
    }
}

// Função para converter label para nível de humor (1-5)
private fun getMoodLevelFromLabel(label: String): Int {
    return when (label.lowercase()) {
        "feliz" -> 5
        "bom" -> 4
        "normal" -> 3
        "triste" -> 2
        "irritado", "ansioso" -> 1
        else -> 3
    }
}

@Composable
fun MoodEntryItem(entry: com.healthapp.emotional.data.models.MoodEntry, formatter: DateTimeFormatter) {
    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(entry.timestamp),
        ZoneId.systemDefault()
    ).toLocalDate()
    
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = entry.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = formatter.format(date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                if (entry.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    var showContent by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explorar Conteúdos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Button(onClick = { showContent = !showContent }) {
            Text(if (showContent) "Esconder Conteúdos" else "Ver Conteúdos Disponíveis")
        }
        
        AnimatedVisibility(
            visible = showContent,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContentItem("Meditação Guiada", "Aprenda técnicas de meditação para reduzir o estresse e ansiedade.")
                ContentItem("Terapia Cognitivo-Comportamental", "Entenda como seus pensamentos afetam suas emoções.")
                ContentItem("Técnicas de Respiração", "Exercícios respiratórios para momentos de crise de ansiedade.")
                ContentItem("Diário de Gratidão", "Como a prática da gratidão pode melhorar seu bem-estar.")
            }
        }
    }
}

@Composable
fun ContentItem(title: String, description: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Acessar")
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Ana Silva") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AS",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (editing) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = { editing = !editing }
        ) {
            Text(if (editing) "Salvar" else "Editar Perfil")
            Icon(
                if (editing) Icons.Default.Close else Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        ProfileMenuItem("Configurações de Privacidade")
        ProfileMenuItem("Notificações")
        ProfileMenuItem("Temas")
        ProfileMenuItem("Ajuda e Suporte")
        ProfileMenuItem("Sobre o App")
        
        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Sair")
        }
    }
}

@Composable
fun ProfileMenuItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* TODO */ }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MoodCareTheme {
        HomeScreen(
            viewModel = MainViewModel(),
            appState = AppState()
        )
    }
}

// Update recordMood function
fun recordMood(moodViewModel: MoodViewModel, authViewModel: AuthViewModel, emoji: String, label: String, intensity: Int, note: String = "") {
    val currentUser = authViewModel.authState.value.currentUser
    
    currentUser?.let { user ->
        moodViewModel.recordMood(
            userId = user.id,
            emoji = emoji,
            label = label,
            intensity = intensity,
            note = note
        )
    }
}

// Update scheduleSession function
fun scheduleSession(sessionViewModel: SessionsViewModel, authViewModel: AuthViewModel, notes: String) {
    val currentUser = authViewModel.authState.value.currentUser
    
    currentUser?.let { user ->
        sessionViewModel.addSession(
            userId = user.id,
            notes = notes
        )
    }
} 