package com.example.seconddemo

import android.app.PendingIntent
import android.content.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.*
import java.io.IOException

class UsbManagerHelper(
    private val context: Context,
    private val onUsbConnected: (UsbDevice) -> Unit,
    private val onUsbDisconnected: (UsbDevice) -> Unit
) {

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        Log.d("UsbManagerHelper", "Dispositivo connesso: ${it.deviceName}")
                        onUsbConnected(it)
                    }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        Log.d("UsbManagerHelper", "Dispositivo scollegato: ${it.deviceName}")
                        onUsbDisconnected(it)
                    }
                }
            }
        }
    }

    fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(usbReceiver, filter)
    }

    fun unregisterReceiver() {
        context.unregisterReceiver(usbReceiver)
    }

    suspend fun connectToUsbDevice(device: UsbDevice): UsbSerialPort? {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent("com.example.seconddemo.USB_PERMISSION"),
            PendingIntent.FLAG_IMMUTABLE
        )

        while (!usbManager.hasPermission(device)) {
            usbManager.requestPermission(device, permissionIntent)
            delay(500)
        }

        val connection = usbManager.openDevice(device) ?: return null
        val port = UsbSerialProber.getDefaultProber().probeDevice(device)?.ports?.getOrNull(0)
        if (port == null) {
            Log.e("UsbManagerHelper", "Impossibile trovare la porta USB")
            return null
        }

        return try {
            port.open(connection)
            port.setParameters(115200, 8, 1, UsbSerialPort.PARITY_NONE)
            port
        } catch (e: Exception) {
            Log.e("UsbManagerHelper", "Errore durante l'apertura della porta", e)
            null
        }
    }

    fun readSerialData(
        port: UsbSerialPort?,
        receivedData: MutableList<String>,
        onError: (String) -> Unit
    ) {
        if (port == null) return
        val buffer = ByteArray(64)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    val len = port.read(buffer, 1000)
                    if (len > 0) {
                        val data = buffer.copyOf(len).toString(Charsets.UTF_8)
                        Log.d("UsbManagerHelper", "Ricevuto: $data")
                        withContext(Dispatchers.Main) {
                            receivedData.add(data)
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) { onError("Errore I/O con il dispositivo USB") }
            }
        }
    }
}