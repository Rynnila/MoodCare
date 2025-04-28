package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

data class AppState(
    val userId: String = "",
    val currentMood: MoodEntry? = null,
    val moodLog: List<MoodEntry> = emptyList(),
    val showMoodRecordedMessage: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            // TODO: Implementar carregamento dos dados do usuário
            // Por enquanto, apenas atualiza o ID do usuário no estado
            _appState.value = _appState.value.copy(userId = userId)
        }
    }

    fun recordMood(emoji: String, label: String, note: String) {
        viewModelScope.launch {
            val newMood = MoodEntry(
                id = UUID.randomUUID().toString(),
                userId = _appState.value.userId,
                timestamp = System.currentTimeMillis(),
                emoji = emoji,
                label = label,
                intensity = getMoodLevelFromLabel(label),
                note = note
            )
            _appState.value = _appState.value.copy(
                currentMood = newMood,
                moodLog = _appState.value.moodLog + newMood,
                showMoodRecordedMessage = true
            )
        }
    }

    private fun getMoodLevelFromLabel(label: String): Int {
        return when (label.lowercase()) {
            "feliz" -> 5
            "calmo" -> 4
            "neutro" -> 3
            "triste" -> 2
            "irritado" -> 1
            else -> 3
        }
    }

    fun dismissMoodRecorded() {
        _appState.value = _appState.value.copy(showMoodRecordedMessage = false)
    }
} 