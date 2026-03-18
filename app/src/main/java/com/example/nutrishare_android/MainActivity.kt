package com.example.nutrishare_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.nutrishare_android.navigation.NavGraph
import com.example.nutrishare_android.navigation.Screen
import com.example.nutrishare_android.ui.components.LoadingScreen
import com.example.nutrishare_android.ui.theme.Nutrishare_androidTheme
import com.example.nutrishare_android.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Nutrishare_androidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: MainViewModel = hiltViewModel()
                    val authState = viewModel.authState.collectAsStateWithLifecycle().value
                    val navController = rememberNavController()
                    if (authState.isLoading) {
                        LoadingScreen()
                    } else {
                        val startDestination = if (authState.isAuthenticated) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        }
                        // NavGraph: frontend App.jsx의 <Routes> 구성
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
