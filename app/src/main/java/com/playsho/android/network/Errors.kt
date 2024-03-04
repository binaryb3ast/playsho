package com.playsho.android.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Errors(

    @SerializedName("property")
    @Expose
    val property: String,

    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("value")
    @Expose
    val value: Any,
)
