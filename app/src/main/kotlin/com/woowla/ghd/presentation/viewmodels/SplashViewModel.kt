package com.woowla.ghd.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowla.ghd.AppFolderFactory
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {
    companion object {
        private const val MIN_LOADING_TIME = 1200
    }

    private val _splashFinished = MutableStateFlow(false)
    val splashFinished: StateFlow<Boolean> = _splashFinished

    init {
        viewModelScope.launch {
            val timeMillis = measureTimeMillis {
                AppFolderFactory.createFolder()
            }
            delay(MIN_LOADING_TIME - timeMillis)
            _splashFinished.value = true
        }
    }
}
