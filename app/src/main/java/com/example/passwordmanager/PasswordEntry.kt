package com.example.passwordmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountType: String,
    val username: String,
    val password: String
) 