package com.example.seconddemo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape // Aggiungi questo import
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Button3D(text: String, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val offsetY by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "")
    val offsetX by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "")
    val buttonShadow by animateDpAsState(targetValue = if (isPressed) 2.dp else 8.dp, label = "")

    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(50.dp)
            .offset(x = offsetX, y = offsetY)
            .shadow(buttonShadow, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        coroutineScope.launch {
                            isPressed = false
                            onClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}