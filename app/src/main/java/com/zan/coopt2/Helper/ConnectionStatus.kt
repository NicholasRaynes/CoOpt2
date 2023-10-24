package com.zan.coopt2.Helper

/**
 * Represents the different states of a network connection.
 * It has two subclasses: Available and Unavailable
 */
sealed class ConnectionStatus {
    object Available: ConnectionStatus()
    object Unavailable: ConnectionStatus()
}






