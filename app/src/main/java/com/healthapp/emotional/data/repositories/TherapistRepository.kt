package com.healthapp.emotional.data.repositories

import com.healthapp.emotional.data.dao.TherapistDao
import com.healthapp.emotional.data.models.Therapist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TherapistRepository @Inject constructor(
    private val therapistDao: TherapistDao
) {
    fun getAllTherapists(): Flow<List<Therapist>> {
        return therapistDao.getAllTherapists()
    }

    fun getTherapistById(id: String): Flow<Therapist?> {
        return therapistDao.getTherapistById(id)
    }

    fun getAvailableTherapists(): Flow<List<Therapist>> {
        return therapistDao.getAllAvailableTherapists()
    }

    fun getTherapistsBySpecialization(specialization: String): Flow<List<Therapist>> {
        return therapistDao.getTherapistsBySpecialization(specialization)
    }

    suspend fun insertTherapist(therapist: Therapist) = withContext(Dispatchers.IO) {
        therapistDao.insertTherapist(therapist)
    }

    suspend fun insertAll(therapists: List<Therapist>) = withContext(Dispatchers.IO) {
        therapistDao.insertAll(therapists)
    }

    suspend fun updateTherapist(therapist: Therapist) = withContext(Dispatchers.IO) {
        therapistDao.updateTherapist(therapist)
    }

    suspend fun deleteTherapist(therapist: Therapist) = withContext(Dispatchers.IO) {
        therapistDao.deleteTherapist(therapist)
    }
} 