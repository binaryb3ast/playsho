package com.playsho.android.data

data class Message(var sender: Device, var room: String, var type: String, var tag: String, var message: String, var created_at: Long) {
    override fun toString(): String {
        return "Message(sender=$sender, room='$room', tag='$tag', message='$message', created_at=$created_at)"
    }
}

