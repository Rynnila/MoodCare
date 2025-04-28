package com.healthapp.emotional.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthapp.emotional.data.models.SessionStatus
import com.healthapp.emotional.data.models.TherapySession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TherapySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TherapySession): Long
    
    @Update
    suspend fun update(session: TherapySession)
    
    @Delete
    suspend fun delete(session: TherapySession)
    
    @Query("SELECT * FROM therapy_sessions WHERE userId = :userId ORDER BY date DESC, time DESC")
    fun getSessionsByUserId(userId: Long): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE userId = :userId AND status = :status ORDER BY date ASC, time ASC")
    fun getSessionsByStatus(userId: Long, status: SessionStatus): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE userId = :userId AND date >= :date ORDER BY date ASC, time ASC")
    fun getUpcomingSessions(userId: Long, date: LocalDate): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): TherapySession?
    
    @Query("SELECT COUNT(*) FROM therapy_sessions WHERE userId = :userId AND status = :status")
    suspend fun getSessionCountByStatus(userId: Long, status: SessionStatus): Int
    
    @Query("UPDATE therapy_sessions SET status = :status WHERE id = :sessionId")
    suspend fun updateSessionStatus(sessionId: Long, status: SessionStatus)
} 