package com.healthapp.emotional.data.repositories

import com.healthapp.emotional.data.dao.TherapySessionDao
import com.healthapp.emotional.data.models.TherapySession
import com.healthapp.emotional.data.models.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TherapySessionRepository @Inject constructor(
    private val sessionDao: TherapySessionDao
) {
    fun getAllSessions(): Flow<List<TherapySession>> {
        return sessionDao.getAllSessions()
    }

    fun getSessionsByUserId(userId: String): Flow<List<TherapySession>> {
        return sessionDao.getSessionsByUserId(userId)
    }

    fun getSessionsByStatus(status: SessionStatus): Flow<List<TherapySession>> {
        return sessionDao.getSessionsByStatus(status)
    }

    fun getSessionsByDate(date: LocalDate): Flow<List<TherapySession>> {
        return sessionDao.getSessionsByDate(date)
    }

    fun getSessionById(id: String): Flow<TherapySession?> {
        return sessionDao.getSessionById(id)
    }

    suspend fun insertSession(session: TherapySession) = withContext(Dispatchers.IO) {
        try {
            sessionDao.insertSession(session)
        } catch (e: Exception) {
            throw Exception("Erro ao salvar sessão: ${e.message}")
        }
    }

    suspend fun updateSession(session: TherapySession) = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("TherapySessionRepository", "Atualizando sessão: ${session.id}")
            android.util.Log.d("TherapySessionRepository", "Dados da sessão: data=${session.date}, hora=${session.startTime}, status=${session.status}")
            sessionDao.updateSession(session)
            android.util.Log.d("TherapySessionRepository", "Sessão atualizada com sucesso")
        } catch (e: Exception) {
            android.util.Log.e("TherapySessionRepository", "Erro ao atualizar sessão", e)
            throw Exception("Erro ao atualizar sessão: ${e.message}")
        }
    }

    suspend fun deleteSession(session: TherapySession) = withContext(Dispatchers.IO) {
        sessionDao.deleteSession(session)
    }
} 