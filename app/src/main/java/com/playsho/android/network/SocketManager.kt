package com.playsho.android.network

import android.util.Log
import com.playsho.android.utils.accountmanager.AccountInstance
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket

object SocketManager {
    private const val TAG = "SocketManager"
    private var socket: Socket? = null

    @Synchronized
    fun initialize(): SocketManager {
        if (socket == null) {
            synchronized(SocketManager::class.java) {
                if (socket == null) {

                    val options = IO.Options().apply {
                        // Set transports to WebSocket only

                    }
                    // Create a new socket instance
                    socket = IO.socket(RetrofitClient.getSocketBaseUrl())

                    socket!!.on(Socket.EVENT_CONNECT) { args ->
                        Log.e(TAG, "EVENT_CONNECT:")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_CONNECTING) { args ->
                        Log.e(TAG, "EVENT_CONNECTING: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_CONNECT_ERROR) { args ->
                        Log.e(TAG, "EVENT_CONNECT_ERROR: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_CONNECT_TIMEOUT) { args ->
                        Log.e(TAG, "EVENT_CONNECT_TIMEOUT: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_DISCONNECT) { args ->
                        Log.e(TAG, "EVENT_DISCONNECT: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_ERROR) { args ->
                        Log.e(TAG, "EVENT_ERROR: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                    socket!!.on(Socket.EVENT_DISCONNECT) { args ->
                        Log.e(TAG, "EVENT_DISCONNECT: $args")
                        for (element in args) {
                            println(element)
                        }
                    }
                }
            }
        }
        return this;
    }

    @Synchronized
    fun establish() {
        socket?.connect()
    }

    @Synchronized
    fun close() {
        socket?.disconnect()
        socket = null
    }
}