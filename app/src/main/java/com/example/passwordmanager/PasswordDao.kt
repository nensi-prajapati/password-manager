package com.example.passwordmanager

import androidx.room.*

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords")
    suspend fun getAll(): List<PasswordEntry>

    @Insert
    suspend fun insert(entry: PasswordEntry)

    @Update
    suspend fun update(entry: PasswordEntry)

    @Delete
    suspend fun delete(entry: PasswordEntry)
} 