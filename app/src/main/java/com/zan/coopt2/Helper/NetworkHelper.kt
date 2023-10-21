package com.zan.coopt2.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Extension property to get the current connectivity status of the device.
 * @return The current connection status (available or unavailable).
 */
val Context.currentConnectivityStatus: ConnectionStatus
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityStatus(connectivityManager)
    }

/**
 * Gets the current connectivity status based on the provided connectivity manager instance.
 * @param connectivityManager The connectivity manager instance.
 * @return The current connection status.
 */
fun getCurrentConnectivityStatus(connectivityManager: ConnectivityManager): ConnectionStatus {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    return if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
        ConnectionStatus.Available
    } else {
        ConnectionStatus.Unavailable
    }
}

/**
 * Creates a flow that observes changes in connectivity status and emits them.
 * @return A flow that emits connection status changes.
 */
fun Context.observeConnectivityAsFlow() = callbackFlow {
    // Gets the ConnectivityManager service to manage network connectivity.
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Creates a callback to handle network status changes and emit them to the flow.
    val callback = NetworkCallback { connectionState -> trySend(connectionState) }

    // Defines a network request that specifies the desired network capabilities (e.g., INTERNET).
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    // Registers the network callback with the ConnectivityManager to listen for network changes.
    connectivityManager.registerNetworkCallback(networkRequest, callback)

    // Gets the current connectivity status and immediately emit it to the flow.
    val currentState = getCurrentConnectivityStatus(connectivityManager)
    trySend(currentState)

    // Closes the flow when it's no longer needed by unregistering the network callback.
    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

/**
 * Creates a network callback instance that maps network events to connection status updates.
 * @param callback The callback function to handle connection status updates.
 * @return A network callback instance.
 */
fun NetworkCallback(callback: (ConnectionStatus) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            callback(ConnectionStatus.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionStatus.Unavailable)
        }
    }
}
