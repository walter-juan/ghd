package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.woowla.ghd.data.local.room.AppDatabase
import com.woowla.ghd.domain.synchronization.Synchronizer
import com.woowla.ghd.eventbus.Event as EventBusEvent
import com.woowla.ghd.eventbus.EventBus
import com.woowla.ghd.presentation.app.AppScreen
import kotlinx.coroutines.launch

class LoginViewModel(
    private val navController: NavController,
    private val appDatabase: AppDatabase = AppDatabase.getInstance(),
) : ViewModel() {
    fun resetDatabase() {
        viewModelScope.launch {
            appDatabase.databaseDao().resetDatabase()
        }
    }

    fun navigateHomeScreen() {
        Synchronizer.INSTANCE.initialize()
        EventBus.publish(EventBusEvent.APP_UNLOCKED)
        navController.navigate(AppScreen.Home.route) {
            popUpTo(AppScreen.Login.route) { inclusive = true }
        }
    }
}