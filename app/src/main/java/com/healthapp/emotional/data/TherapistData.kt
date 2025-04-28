package com.healthapp.emotional.data

import com.healthapp.emotional.data.models.Therapist
import java.util.UUID

object TherapistData {
    val therapists = listOf(
        Therapist(
            id = "1",
            name = "Dra. Maria Silva",
            specialization = "Psicologia Clínica",
            bio = "Especialista em ansiedade e depressão com 10 anos de experiência",
            photoUrl = "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=200&h=200&fit=crop",
            isOnline = true,
            price = 150.0,
            rating = 4.8f
        ),
        Therapist(
            id = "2",
            name = "Dr. João Santos",
            specialization = "TCC",
            bio = "Especializado em fobias e transtornos alimentares",
            photoUrl = "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=200&h=200&fit=crop",
            isOnline = false,
            price = 180.0,
            rating = 4.7f
        ),
        Therapist(
            id = "3",
            name = "Dra. Ana Oliveira",
            specialization = "Neuropsicologia",
            bio = "Experiência no tratamento de traumas e TEPT",
            photoUrl = "https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=200&h=200&fit=crop",
            isOnline = true,
            price = 160.0,
            rating = 4.9f
        )
    )
} 