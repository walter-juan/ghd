package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.data.local.room.AppDatabase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    fun resetDatabase() {
        viewModelScope.launch {
            appDatabase.databaseDao().resetDatabase()
        }
    }
}