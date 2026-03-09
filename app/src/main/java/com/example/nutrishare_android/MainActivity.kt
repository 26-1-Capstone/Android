package com.example.nutrishare_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.nutrishare_android.data.network.RetrofitClient
import com.example.nutrishare_android.navigation.NavGraph
import com.example.nutrishare_android.ui.theme.Nutrishare_androidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrofit 초기화 (Context 주입)
        RetrofitClient.init(applicationContext)

        setContent {
            Nutrishare_androidTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    // NavGraph: frontend App.jsx의 <Routes> 전체를 대체
                    NavGraph(navController = navController, context = applicationContext)
                }
            }
        }
    }
}