package com.playsho.android.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.playsho.android.data.Device
import com.playsho.android.data.Room

data class Result(

    @SerializedName("device")
    @Expose
    val device: Device,

    @SerializedName("room")
    @Expose
    val room: Room,

    )
