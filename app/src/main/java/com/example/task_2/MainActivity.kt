package com.example.task_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.task_2.presentation.navigation.NavGraph
import com.example.task_2.ui.theme.Zad6_2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Zad6_2Theme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
