package com.healthapp.emotional.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val password: String,
    val name: String = ""
) 