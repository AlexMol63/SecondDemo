package com.example.seconddemo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hoho.android.usbserial.driver.UsbSerialPort
import kotlinx.coroutines.launch
import android.util.Log
import com.example.seconddemo.UsbManagerHelper

@Composable
fun MainScreen(navController: NavController, isUsbConnectedState: MutableState<Boolean>) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var usbSerialPort by remember { mutableStateOf<UsbSerialPort?>(null) }
    val receivedData = remember { mutableStateListOf<String>() }

    // Crea un'istanza di UsbManagerHelper
    val usbManagerHelper = remember {
        UsbManagerHelper(
            context = context,
            onUsbConnected = { device ->
                Log.d("UsbReceiver", "Dispositivo connesso: ${device.deviceName}")
                coroutineScope.launch {
                    usbSerialPort = usbManagerHelper.connectToUsbDevice(device)
                    isUsbConnectedState.value = usbSerialPort != null
                }
            },
            onUsbDisconnected = { device ->
                Log.d("UsbReceiver", "Dispositivo scollegato: ${device.deviceName}")
                usbSerialPort = null
                isUsbConnectedState.value = false
            }
        )
    }

    // Registra il receiver USB
    DisposableEffect(Unit) {
        usbManagerHelper.registerReceiver()
        onDispose {
            usbManagerHelper.unregisterReceiver()
        }
    }

    // UI
    if (isUsbConnectedState.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "JWCoin Manager",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Button(onClick = { navController.navigate("setup") }) {
                    Text("Setup")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("diagnostic") }) {
                    Text("Diagnostic")
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Connettere il dispositivo alla porta USB",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
