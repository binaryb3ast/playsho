package com.playsho.android.data

data class Message(val sender: Device, val room: String, val tag: String, val message: String, val created_at: Long) {
    override fun toString(): String {
        return "Message(sender=$sender, room='$room', tag='$tag', message='$message', created_at=$created_at)"
    }
}

