package com.healthapp.emotional.data.models

import java.time.LocalDateTime
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now()
) 