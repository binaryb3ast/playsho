package com.playsho.android.network

import android.util.Log
import com.playsho.android.utils.accountmanager.AccountInstance
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

object SocketManager {
    private const val TAG = "SocketManager"
    private var socket: Socket? = null

    object EVENTS {
        const val SEND_MESSAGE = "room_msg"
        const val NEW_MESSAGE = "new_message"
        const val JOINED = "joined"
        const val LEFT = "left"
        const val TRADE = "trade"
    }

    @Synchronized
    fun initialize(): SocketManager {
        if (socket == null) {
            synchronized(SocketManager::class.java) {
                if (socket == null) {

                    val options = IO.Options().apply {
                        // Set transports to WebSocket only
                        forceNew = true
                        query = "token=${AccountInstance.getAuthToken("Bearer")}"

                    }
                    // Create a new socket instance
                    socket = IO.socket("http://192.168.100.110:7777", options)

                    socket!!.on(Socket.EVENT_CONNECT) { args ->
                        Log.e(TAG, "EVENT_CONNECT:")
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

                    socket!!.on(Socket.EVENT_DISCONNECT) { args ->
                        Log.e(TAG, "EVENT_DISCONNECT: $args")
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

    fun joinRoom(room: String) {
        val json = JSONObject()
        try {
            json.put("room", room)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket?.emit("join", json)
    }

    fun leaveRoom(room: String) {
        val json = JSONObject()
        try {
            json.put("room", room)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket?.emit("leave", json)
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

    fun on(eventName: String, callback: (Array<Any>) -> Unit) {
        socket?.on(eventName, callback)
    }

    @Synchronized
    fun sendMessage(room: String, message: String) {
        val json = JSONObject()
        try {
            json.put("room", room)
            json.put("message", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket?.emit(EVENTS.SEND_MESSAGE, json)
    }

    @Synchronized
    fun trade(tag: String) {
        val json = JSONObject()
        try {
            val senderJson = JSONObject()
            senderJson.put("tag", AccountInstance.getUserData("tag"))
            senderJson.put("public_key", AccountInstance.getAuthToken("public_key"))
            val receiverJson = JSONObject()
            receiverJson.put("tag",tag)
            json.put("sender", senderJson)
            json.put("receiver" , receiverJson)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        socket?.emit(EVENTS.TRADE, json)
    }
}