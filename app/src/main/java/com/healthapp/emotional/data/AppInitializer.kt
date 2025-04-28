package com.healthapp.emotional.data

import android.content.Context
import com.healthapp.emotional.data.dao.TherapistDao
import com.healthapp.emotional.data.models.Therapist
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val therapistDao: TherapistDao
) {
    
    fun initializeDatabase() {
        // Executa a inicialização em uma coroutine
        CoroutineScope(Dispatchers.IO).launch {
            // Adiciona terapeutas de exemplo se não existirem
            createSampleTherapists()
        }
    }
    
    private suspend fun createSampleTherapists() {
        // Adiciona terapeutas de exemplo
        val therapists = TherapistData.therapists
        
        therapistDao.insertAll(therapists)
    }
} 