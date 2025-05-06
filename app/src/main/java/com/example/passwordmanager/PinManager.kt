package com.example.passwordmanager

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class PinManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PinPrefs", Context.MODE_PRIVATE)

    fun isPinCreated(): Boolean {
        return sharedPreferences.contains("pin")
    }

    fun createPin(pin: String) {
        sharedPreferences.edit().apply {
            putString("pin", pin)
            putBoolean("isFirstTime", false)
            apply()
        }
    }

    fun verifyPin(pin: String): Boolean {
        val savedPin = sharedPreferences.getString("pin", null)
        return savedPin == pin
    }

    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean("isFirstTime", true)
    }
}

@Composable
fun rememberPinManager(): PinManager {
    val context = LocalContext.current
    return remember { PinManager(context) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinScreen(onPinCorrect: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val pinManager = rememberPinManager()
    val isFirstTime = pinManager.isFirstTime()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isFirstTime) "Create Your PIN" else "Enter Your PIN",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))
        TextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            label = { Text("PIN") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (pin.length == 4) {
                    if (isFirstTime) {
                        pinManager.createPin(pin)
                        onPinCorrect()
                    } else {
                        if (pinManager.verifyPin(pin)) {
                            onPinCorrect()
                        } else {
                            errorMessage = "Incorrect PIN"
                        }
                    }
                } else {
                    errorMessage = "PIN must be 4 digits"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkLime, contentColor = BottomSheetBackgroundColor)
        ) {
            Text(if (isFirstTime) "Create PIN" else "Submit", style = MaterialTheme.typography.titleMedium)
        }
    }
}