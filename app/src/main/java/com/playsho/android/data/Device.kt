package com.playsho.android.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("tag")
    @Expose
    val tag: String? = "",

    @SerializedName("name")
    @Expose
    val name: String? = "",

    @SerializedName("brand")
    @Expose
    val brand: String,

    @SerializedName("model")
    @Expose
    val model: String,

    @SerializedName("os")
    @Expose
    val os: String,

    @SerializedName("secret")
    @Expose
    val secret: String? = "",

    @SerializedName("manufacturer")
    @Expose
    val manufacturer: String,

    @SerializedName("os_version")
    @Expose
    val osVersion: String,

    @SerializedName("token")
    @Expose
    val token: String? = "",

    @SerializedName("user_name")
    @Expose
    val userName: String? = "",

    @SerializedName("app_version_name")
    @Expose
    val appVersionName: String,

    @SerializedName("app_version")
    @Expose
    val appVersion: Int,

    @SerializedName("last_update_at")
    @Expose
    val lastUpdateAt: Long,

    @SerializedName("first_install_at")
    @Expose
    val firstInstallAt: Long,

    @SerializedName("public_key")
    @Expose
    val publicKey: String? = ""
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        return tag == other.tag
        // Check other properties similarly
        // return true if all properties are equal

    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }

    override fun toString(): String {
        return "Device(tag='$tag', name='$name', brand='$brand', os='$os', secret='$secret', manufacturer='$manufacturer', osVersion='$osVersion', token='$token', userName='$userName', appVersionName='$appVersionName', appVersion=$appVersion, lastUpdateAt=$lastUpdateAt, firstInstallAt=$firstInstallAt)"
    }
}
