package com.healthapp.emotional.data.dao

import androidx.room.*
import com.healthapp.emotional.data.models.Therapist
import kotlinx.coroutines.flow.Flow

@Dao
interface TherapistDao {
    @Query("SELECT * FROM therapists")
    fun getAllTherapists(): Flow<List<Therapist>>

    @Query("SELECT * FROM therapists WHERE id = :id")
    fun getTherapistById(id: String): Flow<Therapist?>

    @Query("SELECT * FROM therapists WHERE isAvailable = 1")
    fun getAllAvailableTherapists(): Flow<List<Therapist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTherapist(therapist: Therapist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(therapists: List<Therapist>)

    @Update
    suspend fun updateTherapist(therapist: Therapist)

    @Delete
    suspend fun deleteTherapist(therapist: Therapist)

    @Query("SELECT * FROM therapists WHERE specialization LIKE '%' || :specialization || '%'")
    fun getTherapistsBySpecialization(specialization: String): Flow<List<Therapist>>
} 