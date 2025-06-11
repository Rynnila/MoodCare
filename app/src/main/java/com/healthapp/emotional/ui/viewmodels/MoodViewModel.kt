package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.emotional.data.models.MoodEntry
import com.healthapp.emotional.data.repositories.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {

    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Carregar entradas de humor do usuário
    fun loadMoodEntries(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val entries = moodRepository.getMoodEntriesForUser(userId)
                _moodEntries.value = entries
            } catch (e: Exception) {
                _error.value = "Erro ao carregar registros de humor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Registrar novo humor
    fun recordMood(
        userId: String,
        emoji: String,
        label: String,
        intensity: Int,
        note: String = ""
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val newEntry = MoodEntry(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    timestamp = System.currentTimeMillis(),
                    emoji = emoji,
                    label = label,
                    intensity = intensity,
                    note = note
                )
                
                moodRepository.addMoodEntry(newEntry)
                
                // Recarregar lista após adicionar
                val updatedEntries = moodRepository.getMoodEntriesForUser(userId)
                _moodEntries.value = updatedEntries
            } catch (e: Exception) {
                _error.value = "Erro ao registrar humor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Atualizar registro de humor existente
    fun updateMoodEntry(moodEntry: MoodEntry) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                moodRepository.updateMoodEntry(moodEntry)
                
                // Recarregar lista após atualizar
                val updatedEntries = moodRepository.getMoodEntriesForUser(moodEntry.userId)
                _moodEntries.value = updatedEntries
            } catch (e: Exception) {
                _error.value = "Erro ao atualizar registro de humor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Excluir registro de humor
    fun deleteMoodEntry(moodEntry: MoodEntry) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                moodRepository.deleteMoodEntry(moodEntry)
                
                // Recarregar lista após excluir
                val updatedEntries = moodRepository.getMoodEntriesForUser(moodEntry.userId)
                _moodEntries.value = updatedEntries
            } catch (e: Exception) {
                _error.value = "Erro ao excluir registro de humor: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpar mensagem de erro
    fun clearError() {
        _error.value = null
    }
} 