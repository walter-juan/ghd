package com.woowla.ghd.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.woowla.ghd.presentation.app.AppIconsPainter
import com.woowla.ghd.presentation.app.Launcher
import com.woowla.ghd.presentation.viewmodels.SplashViewModel

object SplashScreen {
    @Composable
    fun Content(navController: NavController) {
        val viewModel = viewModel { SplashViewModel(navController) }
        val splashState by viewModel.state.collectAsState()
        var visible by remember { mutableStateOf(false) }
        visible = when(splashState) {
            SplashViewModel.State.Initializing -> false
            SplashViewModel.State.Started -> true
        }

        Scaffold {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 600)) + scaleIn(animationSpec = tween(durationMillis = 600)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 600)) + scaleOut(animationSpec = tween(durationMillis = 600))
                ) {
                    Image(
                        painter = AppIconsPainter.Launcher,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(80.dp),
                    )
                }
            }
        }
    }
}