package com.example.passwordmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "password-db"
    ).build()
    private val dao = db.passwordDao()

    private val _passwords = MutableStateFlow<List<PasswordEntry>>(emptyList())
    val passwords: StateFlow<List<PasswordEntry>> = _passwords

    init {
        loadPasswords()
    }

    fun loadPasswords() {
        viewModelScope.launch {
            _passwords.value = dao.getAll()
        }
    }

    fun addPassword(entry: PasswordEntry) {
        viewModelScope.launch {
            dao.insert(entry)
            loadPasswords()
        }
    }

    fun updatePassword(entry: PasswordEntry) {
        viewModelScope.launch {
            dao.update(entry)
            loadPasswords()
        }
    }

    fun deletePassword(entry: PasswordEntry) {
        viewModelScope.launch {
            dao.delete(entry)
            loadPasswords()
        }
    }
} 