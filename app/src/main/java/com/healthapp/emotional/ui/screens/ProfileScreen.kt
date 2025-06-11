package com.healthapp.emotional.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthapp.emotional.ui.viewmodels.AuthViewModel
import com.healthapp.emotional.ui.viewmodels.UserProfileViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val profileViewModel: UserProfileViewModel = hiltViewModel()
    
    val authState by authViewModel.authState.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()
    
    // Carrega o perfil do usuário atual
    LaunchedEffect(authState.currentUser) {
        authState.currentUser?.let { user ->
            profileViewModel.loadUserProfile(user.id)
        }
    }
    
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Ana Silva") }
    var email by remember { mutableStateOf("") }
    
    // Atualiza os estados locais quando o perfil é carregado
    LaunchedEffect(profileState.userProfile) {
        profileState.userProfile?.let { profile ->
            name = profile.name
            email = profile.email
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagem de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (profileState.userProfile?.name?.isNotEmpty() == true) {
                val initials = profileState.userProfile?.name
                    ?.split(" ")
                    ?.mapNotNull { it.firstOrNull()?.toString() }
                    ?.take(2)
                    ?.joinToString("") ?: ""
                
                Text(
                    text = initials,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Nome e Email
        if (editing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Email não pode ser alterado
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = { editing = false }) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = { 
                        authState.currentUser?.let { user ->
                            profileViewModel.updateUserProfile(
                                userId = user.id,
                                name = name
                            )
                            editing = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            }
        } else {
            Text(
                text = profileState.userProfile?.name ?: "Carregando...",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = profileState.userProfile?.email ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { editing = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Editar Perfil")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Menu de opções
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configurações da Conta",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificações",
                    subtitle = "Gerenciar notificações do aplicativo"
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Privacidade e Segurança",
                    subtitle = "Gerenciar configurações de privacidade"
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.Palette,
                    title = "Aparência",
                    subtitle = "Tema claro/escuro e cores do aplicativo"
                )
                
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "Sobre",
                    subtitle = "Informações do aplicativo e termos de uso"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botão de logout
        Button(
            onClick = { 
                authViewModel.processLogout()
                onNavigateToHome()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Sair da Conta")
        }
        
        if (profileState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = profileState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 