package com.example.seconddemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsbViewModel : ViewModel() {
    private val _isUsbConnected = MutableStateFlow(false)
    val isUsbConnected: StateFlow<Boolean> = _isUsbConnected

    fun updateUsbConnectionStatus(isConnected: Boolean) {
        viewModelScope.launch {
            _isUsbConnected.value = isConnected
        }
    }
}
