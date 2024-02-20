package com.playsho.android.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Meta(

    @SerializedName("page")
    @Expose
    val page: Int,

    @SerializedName("limit")
    @Expose
    val limit: Int,

    @SerializedName("totalPage")
    @Expose
    val totalPage: Int,

    @SerializedName("count")
    @Expose
    val count: Int
)
