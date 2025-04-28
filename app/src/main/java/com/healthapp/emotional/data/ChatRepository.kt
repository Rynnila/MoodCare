package com.healthapp.emotional.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.healthapp.emotional.data.models.ChatMessage
import com.healthapp.emotional.data.converters.LocalDateTimeAdapter
import java.time.LocalDateTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_preferences")

@Singleton
class ChatRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
    
    private val chatMessagesKey = stringPreferencesKey("chat_messages")

    suspend fun saveMessages(messages: List<ChatMessage>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(messages)
            preferences[chatMessagesKey] = json
        }
    }

    val messages: Flow<List<ChatMessage>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[chatMessagesKey] ?: "[]"
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
} 