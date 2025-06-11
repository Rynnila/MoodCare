package com.healthapp.emotional.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable
import com.healthapp.emotional.data.ChatRepository
import com.healthapp.emotional.data.models.ChatMessage
import javax.inject.Inject
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRepository: ChatRepository
) {
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Prompt do sistema para definir o papel da Luna
    val systemPrompt = """
        Você é a Luna, uma assistente virtual especializada em saúde emocional e mental. 
        Seu objetivo é oferecer suporte emocional, orientação e recursos para ajudar as pessoas a cuidarem de sua saúde mental.
        
        Como Luna, você deve:
        1. Ser empática e acolhedora em todas as interações
        2. Oferecer suporte emocional sem julgamentos
        3. Sugerir técnicas de autocuidado e mindfulness quando apropriado
        4. Reconhecer quando uma situação requer ajuda profissional
        5. Manter um tom amigável e profissional
        6. Evitar dar diagnósticos médicos ou psicológicos
        7. Encorajar práticas saudáveis de bem-estar mental
        8. Usar emojis ocasionalmente para tornar a conversa mais descontraída
        9. Manter respostas curtas e diretas, focando no essencial
        
        Lembre-se: você é uma assistente virtual de saúde emocional, não um substituto para profissionais de saúde mental.
    """.trimIndent()
    
    // Inicializa o modelo Gemini
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = "AIzaSyBQJTofBOOzWDIUXSSST7Ly65Amgu7lVus"
        )
    }

    // Inicializa o chat com histórico
    val chat = remember {
        generativeModel.startChat(
            history = listOf(
                content {
                    role = "user"
                    text(systemPrompt)
                }
            )
        )
    }
    
    // Carrega as mensagens salvas
    LaunchedEffect(Unit) {
        chatRepository.messages.collect { savedMessages ->
            messages = if (savedMessages.isNotEmpty()) {
                savedMessages
            } else {
                // Adiciona mensagem inicial apenas se não houver mensagens salvas
                listOf(
                    ChatMessage(
                        text = "Olá! Eu sou a Luna, sua assistente virtual de saúde emocional. " +
                              "Estou aqui para conversar, oferecer suporte e ajudar você a cuidar da sua saúde mental. " +
                              "Como posso ajudar você hoje? 😊",
                        isUser = false
                    )
                )
            }
        }
    }

    // Salva as mensagens quando elas mudam
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            chatRepository.saveMessages(messages)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Lista de mensagens
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
        
        // Campo de entrada de mensagem
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Digite sua mensagem...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = {
                    if (messageText.isNotBlank() && !isLoading) {
                        val userMessage = ChatMessage(
                            text = messageText,
                            isUser = true
                        )
                        messages = messages + userMessage
                        val currentMessage = messageText
                        messageText = ""
                        
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                        
                        coroutineScope.launch {
                            try {
                                isLoading = true
                                Log.d("ChatScreen", "Enviando mensagem para o Gemini: $currentMessage")
                                
                                val response = chat.sendMessage(
                                    content {
                                        role = "user"
                                        text(currentMessage)
                                    }
                                )
                                
                                Log.d("ChatScreen", "Resposta recebida do Gemini: ${response.text}")
                                
                                val aiMessage = ChatMessage(
                                    text = response.text ?: "Desculpe, não consegui processar sua mensagem.",
                                    isUser = false
                                )
                                messages = messages + aiMessage
                                
                                listState.animateScrollToItem(messages.size - 1)
                            } catch (e: Exception) {
                                Log.e("ChatScreen", "Erro ao processar mensagem", e)
                                val errorMessage = ChatMessage(
                                    text = "Erro: ${e.message}",
                                    isUser = false
                                )
                                messages = messages + errorMessage
                                listState.animateScrollToItem(messages.size - 1)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar mensagem",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
} 
