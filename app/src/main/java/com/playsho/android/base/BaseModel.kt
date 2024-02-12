package com.playsho.android.base

import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull
import java.io.Serializable
import java.util.UUID

/**
 * Base model class for common attributes shared among other model classes.
 */
class BaseModel : Serializable {
    var hash: String = UUID.randomUUID().toString()

    @SerializedName("code")
    var code: String? = null

    @SerializedName("_id")
    var id: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}