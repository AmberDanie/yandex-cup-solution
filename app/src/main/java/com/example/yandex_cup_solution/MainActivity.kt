package com.example.yandex_cup_solution

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yandex_cup_solution.di.ActivityComponent
import com.example.yandex_cup_solution.ui.MainScreen
import com.example.yandex_cup_solution.ui.theme.Yandex_cup_solutionTheme

class MainActivity : ComponentActivity() {
    private var activityComponent: ActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityComponent =
            (applicationContext as YandexCupApplication).appComponent.activityComponent.also {
                it.inject(
                    this@MainActivity
                )
            }

        val canvasViewModelFactory = activityComponent!!.canvasViewModelFactory

        setContent {
            Yandex_cup_solutionTheme {
                NavHost(navController = rememberNavController(), startDestination = "main") {
                    composable("main") { navBackStackEntry ->
                        val canvasViewModel =
                            viewModel { canvasViewModelFactory.create(navBackStackEntry.savedStateHandle) }
                        val uiState = canvasViewModel.uiState.collectAsState()

                        MainScreen(
                            uiState = uiState.value,
                            onViewModelEvent = { event -> canvasViewModel.onEvent(event) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}
