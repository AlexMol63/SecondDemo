package com.example.seconddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.seconddemo.ui.theme.SecondDemoTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isUsbConnectedState = mutableStateOf(false)

        setContent {
            SecondDemoTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController, isUsbConnectedState) }
                    composable("setup") { SetupScreen(navController, isUsbConnectedState) }
                    composable("diagnostic") { DiagnosticScreen(navController, isUsbConnectedState) }
                }
            }
        }
    }
}