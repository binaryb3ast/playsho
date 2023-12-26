package com.playsho.android.base;


import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * Base model class for common attributes shared among other model classes.
 */
public class BaseModel implements Serializable {

    @NotNull
    private String hash;

    @SerializedName("code")
    private String code;

    @SerializedName("_id")
    private String _id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    /**
     * Default constructor. Generates a random UUID as the hash.
     */
    public BaseModel() {
        this.hash = UUID.randomUUID().toString();
    }

    /**
     * Gets the hash value.
     *
     * @return The hash value.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the hash value.
     *
     * @param hash The hash value to set.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Gets the code.
     *
     * @return The code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the ID.
     *
     * @return The ID.
     */
    public String get_id() {
        return _id;
    }

    /**
     * Sets the ID.
     *
     * @param _id The ID to set.
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return The creation timestamp.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt The creation timestamp to set.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the update timestamp.
     *
     * @return The update timestamp.
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param updatedAt The update timestamp to set.
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}