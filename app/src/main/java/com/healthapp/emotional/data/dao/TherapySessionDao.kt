package com.healthapp.emotional.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthapp.emotional.data.models.TherapySession
import com.healthapp.emotional.data.models.SessionStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TherapySessionDao {
    @Query("SELECT * FROM therapy_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getSessionsByUserId(userId: String): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE status = :status ORDER BY date DESC")
    fun getSessionsByStatus(status: SessionStatus): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE date = :date ORDER BY startTime ASC")
    fun getSessionsByDate(date: LocalDate): Flow<List<TherapySession>>
    
    @Query("SELECT * FROM therapy_sessions WHERE id = :id")
    fun getSessionById(id: String): Flow<TherapySession?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TherapySession)
    
    @Update
    suspend fun updateSession(session: TherapySession)
    
    @Delete
    suspend fun deleteSession(session: TherapySession)
} 