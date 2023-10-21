package com.zan.coopt2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.zan.coopt2.Helper.ConnectionStatus
import com.zan.coopt2.Helper.currentConnectivityStatus
import com.zan.coopt2.Helper.observeConnectivityAsFlow
import com.zan.coopt2.Screens.AvailableScreen
import com.zan.coopt2.Screens.UnavailableScreen

/**
 * The main activity for the application. It sets up the Composable UI and checks connectivity status.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            checkConnectivityStatus()
        }
    }
}

/**
 * A Composable function to check the connectivity status and display the appropriate screen.
 */
@Composable
fun checkConnectivityStatus() {
    val connection by connectivityStatus()

    val isConnected = connection === ConnectionStatus.Available

    if (isConnected) {
        AvailableScreen()
    } else {
        UnavailableScreen()
    }
}

/**
 * A Composable function that provides the current connectivity status as a State object.
 * @return A State object containing the current connection status.
 */
@Composable
fun connectivityStatus(): State<ConnectionStatus> {
    val currentContext = LocalContext.current

    return produceState(initialValue = currentContext.currentConnectivityStatus) {
        currentContext.observeConnectivityAsFlow().collect { value = it }
    }
}