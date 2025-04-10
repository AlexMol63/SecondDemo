package com.example.seconddemo

import android.content.Context
import android.hardware.usb.UsbManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun DiagnosticScreen(navController: NavController, isUsbConnectedState: MutableState<Boolean>) {
    val context = LocalContext.current
    val receivedData = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    if (!isUsbConnectedState.value) {
        ErrorScreen(message = "Connettere il dispositivo alla porta USB", navController = navController)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Diagnostic Page", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button3D(text = "Send UART Message") {
            coroutineScope.launch {
                // Logica per inviare messaggi tramite USB
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Dati ricevuti:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        for (line in receivedData) {
            Text(text = line)
        }
    }
}