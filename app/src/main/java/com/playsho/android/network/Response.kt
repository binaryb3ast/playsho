package com.playsho.android.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Response(

    @SerializedName("message")
    @Expose
    val message: String,


    @SerializedName("code")
    @Expose
    val code: Int,

    @SerializedName("result")
    @Expose
    val result: Result,

    @SerializedName("errors")
    @Expose
    val errors: List<Errors>,

    @SerializedName("meta")
    @Expose
    val meta: Meta
) {

    override fun toString(): String {
        return "Response{" +
                "message='$message', " +
                "code=$code, " +
                "result=$result, " +
                "errors=$errors, " +
                "meta=$meta" +
                '}'
    }
}
