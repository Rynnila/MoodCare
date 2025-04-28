package com.healthapp.emotional.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var darkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var reminderTime by remember { mutableStateOf("20:00") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        // Aparência
        SettingsSection(
            title = "Aparência",
            icon = Icons.Default.Palette
        ) {
            SettingsSwitch(
                title = "Modo Escuro",
                subtitle = "Ativar tema escuro",
                icon = Icons.Default.DarkMode,
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }
        
        // Notificações
        SettingsSection(
            title = "Notificações",
            icon = Icons.Default.Notifications
        ) {
            SettingsSwitch(
                title = "Notificações",
                subtitle = "Receber lembretes e atualizações",
                icon = Icons.Default.NotificationsActive,
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            
            if (notificationsEnabled) {
                SettingsSwitch(
                    title = "Som",
                    subtitle = "Tocar sons nas notificações",
                    icon = Icons.Default.VolumeUp,
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
                
                SettingsSwitch(
                    title = "Vibração",
                    subtitle = "Vibrar nas notificações",
                    icon = Icons.Default.Vibration,
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
                
                SettingsTimePicker(
                    title = "Horário do Lembrete",
                    subtitle = "Definir horário para lembretes diários",
                    icon = Icons.Default.AccessTime,
                    time = reminderTime,
                    onTimeChange = { reminderTime = it }
                )
            }
        }
        
        // Privacidade
        SettingsSection(
            title = "Privacidade",
            icon = Icons.Default.Lock
        ) {
            SettingsButton(
                title = "Política de Privacidade",
                subtitle = "Ler nossa política de privacidade",
                icon = Icons.Default.PrivacyTip
            )
            
            SettingsButton(
                title = "Termos de Uso",
                subtitle = "Ler nossos termos de uso",
                icon = Icons.Default.Description
            )
        }
        
        // Sobre
        SettingsSection(
            title = "Sobre",
            icon = Icons.Default.Info
        ) {
            SettingsButton(
                title = "Versão do App",
                subtitle = "1.0.0",
                icon = Icons.Default.Android
            )
            
            SettingsButton(
                title = "Avaliar App",
                subtitle = "Deixe sua avaliação na Play Store",
                icon = Icons.Default.Star
            )
            
            SettingsButton(
                title = "Compartilhar App",
                subtitle = "Convide amigos para usar o app",
                icon = Icons.Default.Share
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            content()
        }
    }
}

@Composable
fun SettingsSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsButton(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { /* TODO: Implementar ação */ }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
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

@Composable
fun SettingsTimePicker(
    title: String,
    subtitle: String,
    icon: ImageVector,
    time: String,
    onTimeChange: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { showTimePicker = true }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
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
            
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    
    if (showTimePicker) {
        // TODO: Implementar TimePicker
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Selecionar Horário") },
            text = { Text("Implementar TimePicker aqui") },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            }
        )
    }
} 