package com.playsho.android.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.playsho.android.data.Device

data class Result(

    @SerializedName("device")
    @Expose
    val device: Device,

)
