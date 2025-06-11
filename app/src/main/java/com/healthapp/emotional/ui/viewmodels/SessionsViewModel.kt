package com.healthapp.emotional.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthapp.emotional.data.TherapistData
import com.healthapp.emotional.data.models.SessionStatus
import com.healthapp.emotional.data.models.Therapist
import com.healthapp.emotional.data.models.TherapySession
import com.healthapp.emotional.data.repositories.TherapistRepository
import com.healthapp.emotional.data.repositories.TherapySessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

data class SessionsUiState(
    val isLoading: Boolean = false,
    val upcomingSessions: List<TherapySession> = emptyList(),
    val pastSessions: List<TherapySession> = emptyList(),
    val availableTherapists: List<Therapist> = emptyList(),
    val selectedTherapist: Therapist? = null,
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val availableTimes: List<LocalTime> = emptyList(),
    val notes: String = "",
    val showNewSessionDialog: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val therapySessionRepository: TherapySessionRepository,
    private val therapistRepository: TherapistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    // Carregar sessões do usuário
    fun loadUserSessions(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Buscando sessões do usuário no repositório
                therapySessionRepository.getSessionsByUserId(userId).collect { sessions ->
                    // Filtrando sessões futuras e passadas
                    val now = LocalDate.now()
                    val upcoming = sessions.filter { 
                        (it.date.isAfter(now) || it.date.isEqual(now)) && 
                        it.status == SessionStatus.SCHEDULED
                    }.sortedBy { it.date }
                    
                    val past = sessions.filter { 
                        it.date.isBefore(now) || 
                        it.status == SessionStatus.CANCELLED ||
                        it.status == SessionStatus.COMPLETED
                    }.sortedByDescending { it.date }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            upcomingSessions = upcoming,
                            pastSessions = past
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar sessões: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    // Carregar terapeutas disponíveis
    fun loadTherapists() {
        viewModelScope.launch {
            try {
                // Usar apenas os dados de exemplo
                val sampleTherapists = TherapistData.therapists
                _uiState.update { 
                    it.copy(availableTherapists = sampleTherapists)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Erro ao carregar terapeutas: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    // Mostrar diálogo para nova sessão
    fun showNewSessionDialog() {
        _uiState.update {
            it.copy(
                showNewSessionDialog = true,
                selectedTherapist = null,
                selectedDate = null,
                selectedTime = null,
                notes = ""
            )
        }
    }
    
    // Ocultar diálogo
    fun hideNewSessionDialog() {
        _uiState.update {
            it.copy(
                showNewSessionDialog = false,
                selectedTherapist = null,
                selectedDate = null,
                selectedTime = null,
                notes = ""
            )
        }
    }
    
    // Selecionar terapeuta
    fun selectTherapist(therapist: Therapist?) {
        _uiState.update {
            it.copy(selectedTherapist = therapist)
        }
        updateAvailableTimes()
    }
    
    // Selecionar data
    fun selectDate(date: LocalDate?) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
        updateAvailableTimes()
    }
    
    // Selecionar horário
    fun selectTime(timeString: String?) {
        val time = timeString?.let {
            val parts = it.split(":")
            if (parts.size == 2) {
                LocalTime.of(parts[0].toInt(), parts[1].toInt())
            } else {
                null
            }
        }
        
        _uiState.update {
            it.copy(selectedTime = time)
        }
    }
    
    // Atualizar notas
    fun updateNotes(notes: String) {
        _uiState.update {
            it.copy(notes = notes)
        }
    }
    
    // Atualizar horários disponíveis com base no terapeuta e data selecionados
    private fun updateAvailableTimes() {
        val therapist = _uiState.value.selectedTherapist
        val date = _uiState.value.selectedDate
        
        if (therapist != null && date != null) {
            // Verificar horários disponíveis para o terapeuta na data selecionada
            // Se o terapeuta tiver disponibilidade registrada, usamos ela
            val availableTimes = therapist.availability[date] ?: listOf(
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
            )
            
            _uiState.update {
                it.copy(availableTimes = availableTimes)
            }
        } else {
            _uiState.update {
                it.copy(availableTimes = emptyList())
            }
        }
    }
    
    // Adicionar nova sessão
    fun addSession(userId: String, notes: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("SessionsViewModel", "Iniciando agendamento de sessão")
                android.util.Log.d("SessionsViewModel", "UserId: $userId")
                android.util.Log.d("SessionsViewModel", "Notas: $notes")
                
                val therapist = _uiState.value.selectedTherapist
                val date = _uiState.value.selectedDate
                val time = _uiState.value.selectedTime
                
                android.util.Log.d("SessionsViewModel", "Terapeuta: ${therapist?.name}")
                android.util.Log.d("SessionsViewModel", "Data: $date")
                android.util.Log.d("SessionsViewModel", "Horário: $time")
                
                if (therapist != null && date != null && time != null) {
                    android.util.Log.d("SessionsViewModel", "Criando nova sessão")
                    
                    // Verificar se já existe uma sessão com os mesmos dados
                    val existingSession = _uiState.value.upcomingSessions.find { session ->
                        session.therapistName == therapist.name &&
                        session.date == date &&
                        session.startTime == time
                    }
                    
                    if (existingSession != null) {
                        android.util.Log.d("SessionsViewModel", "Sessão já existe, atualizando...")
                        // Atualizar a sessão existente
                        val updatedSession = existingSession.copy(
                            notes = notes,
                            updatedAt = System.currentTimeMillis()
                        )
                        therapySessionRepository.updateSession(updatedSession)
                    } else {
                        // Criar nova sessão
                        val newSession = TherapySession(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            therapistName = therapist.name,
                            date = date,
                            startTime = time,
                            duration = 50, // 50 minutos por padrão
                            notes = notes,
                            location = if (therapist.isOnline) "Online" else "Consultório",
                            mode = if (therapist.isOnline) "online" else "in-person",
                            status = SessionStatus.SCHEDULED
                        )
                        
                        android.util.Log.d("SessionsViewModel", "Salvando nova sessão no repositório")
                        therapySessionRepository.insertSession(newSession)
                    }
                    
                    android.util.Log.d("SessionsViewModel", "Sessão salva com sucesso")
                    
                    // Atualizar a lista de sessões
                    loadUserSessions(userId)
                    
                    // Fechar o diálogo e limpar os campos
                    _uiState.update {
                        it.copy(
                            showNewSessionDialog = false,
                            selectedTherapist = null,
                            selectedDate = null,
                            selectedTime = null,
                            notes = "",
                            errorMessage = null
                        )
                    }
                    
                    android.util.Log.d("SessionsViewModel", "Diálogo fechado e campos limpos")
                } else {
                    android.util.Log.d("SessionsViewModel", "Campos obrigatórios não preenchidos")
                    _uiState.update {
                        it.copy(
                            errorMessage = "Por favor, preencha todos os campos obrigatórios"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SessionsViewModel", "Erro ao agendar sessão", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao agendar sessão: ${e.message}"
                    )
                }
            }
        }
    }
    
    // Cancelar sessão
    fun cancelSession(sessionId: String, userId: String) {
        viewModelScope.launch {
            try {
                // Encontrar sessão
                val session = _uiState.value.upcomingSessions.find { it.id == sessionId }
                
                if (session != null) {
                    // Criar sessão cancelada
                    val cancelledSession = session.copy(
                        status = SessionStatus.CANCELLED,
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Atualizar a sessão no repositório
                    therapySessionRepository.updateSession(cancelledSession)
                    
                    // Recarregar sessões para atualizar as listas
                    loadUserSessions(userId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao cancelar sessão: ${e.message}"
                    )
                }
            }
        }
    }
    
    // Marcar sessão como concluída
    fun completeSession(sessionId: String, userId: String) {
        viewModelScope.launch {
            try {
                // Encontrar sessão
                val session = _uiState.value.upcomingSessions.find { it.id == sessionId }
                
                if (session != null) {
                    // Criar sessão concluída
                    val completedSession = session.copy(
                        status = SessionStatus.COMPLETED,
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Atualizar a sessão no repositório
                    therapySessionRepository.updateSession(completedSession)
                    
                    // Recarregar sessões para atualizar as listas
                    loadUserSessions(userId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao marcar sessão como concluída: ${e.message}"
                    )
                }
            }
        }
    }
    
    // Excluir sessão
    fun deleteSession(sessionId: String, userId: String) {
        viewModelScope.launch {
            try {
                // Encontrar sessão
                val session = _uiState.value.upcomingSessions.find { it.id == sessionId }
                    ?: _uiState.value.pastSessions.find { it.id == sessionId }
                
                if (session != null) {
                    // Excluir a sessão do repositório
                    therapySessionRepository.deleteSession(session)
                    
                    // Recarregar sessões para atualizar as listas
                    loadUserSessions(userId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao excluir sessão: ${e.message}"
                    )
                }
            }
        }
    }
    
    // Mostrar mensagem de erro
    fun showErrorMessage(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
    
    // Limpar mensagem de erro
    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
    
    // Métodos para criar dados de exemplo
    private fun createSampleSessions(userId: String): List<TherapySession> {
        val today = LocalDate.now()
        
        return listOf(
            // Sessões futuras
            TherapySession(
                id = "1",
                userId = userId,
                therapistName = "Dra. Maria Silva",
                date = today.plusDays(3),
                startTime = LocalTime.of(14, 0),
                duration = 50,
                notes = "Primeira consulta para avaliar ansiedade",
                status = SessionStatus.SCHEDULED,
                location = "Online",
                mode = "online"
            ),
            TherapySession(
                id = "2",
                userId = userId,
                therapistName = "Dr. João Santos",
                date = today.plusDays(10),
                startTime = LocalTime.of(10, 0),
                duration = 50,
                notes = "Acompanhamento mensal",
                status = SessionStatus.SCHEDULED,
                location = "Consultório",
                mode = "in-person"
            ),
            
            // Sessões passadas
            TherapySession(
                id = "3",
                userId = userId,
                therapistName = "Dra. Maria Silva",
                date = today.minusDays(7),
                startTime = LocalTime.of(15, 0),
                duration = 50,
                notes = "Sessão inicial",
                status = SessionStatus.COMPLETED,
                location = "Online",
                mode = "online"
            ),
            TherapySession(
                id = "4",
                userId = userId,
                therapistName = "Dra. Ana Oliveira",
                date = today.minusDays(14),
                startTime = LocalTime.of(9, 0),
                duration = 50,
                notes = "Discussão sobre técnicas de relaxamento",
                status = SessionStatus.COMPLETED,
                location = "Online",
                mode = "online"
            ),
            TherapySession(
                id = "5",
                userId = userId,
                therapistName = "Dr. João Santos",
                date = today.minusDays(3),
                startTime = LocalTime.of(16, 0),
                duration = 50,
                notes = "Cancelado devido a conflito de agenda",
                status = SessionStatus.CANCELLED,
                location = "Consultório",
                mode = "in-person"
            )
        )
    }
    
    private fun createSampleTherapists(): List<Therapist> {
        return listOf(
            Therapist(
                id = UUID.randomUUID().toString(),
                name = "Dra. Maria Silva",
                specialization = "Psicologia Clínica",
                bio = "Especialista em ansiedade e depressão com 10 anos de experiência",
                photoUrl = "https://example.com/maria.jpg",
                isOnline = true,
                price = 150.0,
                rating = 4.8f
            ),
            Therapist(
                id = UUID.randomUUID().toString(),
                name = "Dr. João Santos",
                specialization = "Terapia Cognitivo-Comportamental",
                bio = "Especializado em fobias e transtornos alimentares",
                photoUrl = "https://example.com/joao.jpg",
                isOnline = false,
                price = 180.0,
                rating = 4.7f
            ),
            Therapist(
                id = UUID.randomUUID().toString(),
                name = "Dra. Ana Oliveira",
                specialization = "Neuropsicologia",
                bio = "Experiência no tratamento de traumas e TEPT",
                photoUrl = "https://example.com/ana.jpg",
                isOnline = true,
                price = 160.0,
                rating = 4.9f
            )
        )
    }

    fun updateSession(session: TherapySession) {
        viewModelScope.launch {
            try {
                android.util.Log.d("SessionsViewModel", "Iniciando atualização de sessão")
                android.util.Log.d("SessionsViewModel", "Session ID: ${session.id}")
                android.util.Log.d("SessionsViewModel", "Data: ${session.date}")
                android.util.Log.d("SessionsViewModel", "Horário: ${session.startTime}")
                
                // Atualizar a sessão no repositório
                therapySessionRepository.updateSession(session)
                
                // Atualizar a lista de sessões
                loadUserSessions(session.userId)
                
                android.util.Log.d("SessionsViewModel", "Sessão atualizada com sucesso")
            } catch (e: Exception) {
                android.util.Log.e("SessionsViewModel", "Erro ao atualizar sessão", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao atualizar sessão: ${e.message}"
                    )
                }
            }
        }
    }

    // Mostrar diálogo para editar sessão
    fun showEditSessionDialog(session: TherapySession) {
        android.util.Log.d("SessionsViewModel", "Mostrando diálogo de edição")
        android.util.Log.d("SessionsViewModel", "Session ID: ${session.id}")
        android.util.Log.d("SessionsViewModel", "Terapeuta: ${session.therapistName}")
        android.util.Log.d("SessionsViewModel", "Data: ${session.date}")
        android.util.Log.d("SessionsViewModel", "Horário: ${session.startTime}")
        
        _uiState.update {
            it.copy(
                showNewSessionDialog = true,
                selectedTherapist = it.availableTherapists.find { therapist -> therapist.name == session.therapistName },
                selectedDate = session.date,
                selectedTime = session.startTime,
                notes = session.notes
            )
        }
        
        // Atualizar horários disponíveis
        updateAvailableTimes()
    }

    fun updateExistingSession(session: TherapySession, notes: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("SessionsViewModel", "Atualizando sessão existente")
                android.util.Log.d("SessionsViewModel", "Session ID: ${session.id}")
                android.util.Log.d("SessionsViewModel", "Notas: $notes")
                
                val therapist = _uiState.value.selectedTherapist
                val date = _uiState.value.selectedDate
                val time = _uiState.value.selectedTime
                
                android.util.Log.d("SessionsViewModel", "Terapeuta: ${therapist?.name}")
                android.util.Log.d("SessionsViewModel", "Data: $date")
                android.util.Log.d("SessionsViewModel", "Horário: $time")
                
                if (therapist != null && date != null && time != null) {
                    val updatedSession = session.copy(
                        therapistName = therapist.name,
                        date = date,
                        startTime = time,
                        notes = notes,
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    // Atualizar a sessão no repositório
                    therapySessionRepository.updateSession(updatedSession)
                    
                    // Atualizar a lista de sessões
                    loadUserSessions(session.userId)
                    
                    // Fechar o diálogo e limpar os campos
                    _uiState.update {
                        it.copy(
                            showNewSessionDialog = false,
                            selectedTherapist = null,
                            selectedDate = null,
                            selectedTime = null,
                            notes = "",
                            errorMessage = null
                        )
                    }
                    
                    android.util.Log.d("SessionsViewModel", "Sessão atualizada com sucesso")
                } else {
                    android.util.Log.d("SessionsViewModel", "Campos obrigatórios não preenchidos")
                    _uiState.update {
                        it.copy(
                            errorMessage = "Por favor, preencha todos os campos obrigatórios"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SessionsViewModel", "Erro ao atualizar sessão", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao atualizar sessão: ${e.message}"
                    )
                }
            }
        }
    }
} 