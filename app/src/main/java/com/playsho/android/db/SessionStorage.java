package com.playsho.android.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.playsho.android.base.ApplicationLoader;
import com.playsho.android.utils.Validator;

/**
 * A class for managing session storage using SharedPreferences.
 */
public class SessionStorage {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    // Name of the shared preference file
    private final String SHARED_PREFERENCE_NAME = "main_sp";

    /**
     * Constructs a SessionStorage instance and initializes SharedPreferences and its editor.
     */
    public SessionStorage() {
        this.pref = ApplicationLoader.getAppContext().getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        editor = pref.edit();
        editor.apply();
    }

    /**
     * Clears all entries in the SharedPreferences.
     */
    public void clearAll(){
        pref.edit().clear().apply();
    }

    /**
     * Gets a String value from the SharedPreferences with the specified key.
     *
     * @param key The key for the String value.
     * @return The String value associated with the key, or {@code null} if not found.
     */
    public String getString(String key) {
        return pref.getString(key, null);
    }


    /**
     * Gets a String value from the SharedPreferences with the specified key, providing a default value if not found.
     *
     * @param key      The key for the String value.
     * @param defValue The default value to return if the key is not found.
     * @return The String value associated with the key, or the default value if not found.
     */
    public String getString(String key , String defValue) {
        return pref.getString(key, defValue);
    }

    /**
     * Sets a String value in the SharedPreferences with the specified key.
     *
     * @param key   The key for the String value.
     * @param value The String value to set.
     */
    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    /**
     * Gets an integer value from the SharedPreferences with the specified key.
     *
     * @param key The key for the integer value.
     * @return The integer value associated with the key, or 0 if not found.
     */
    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    /**
     * Gets an integer value from the SharedPreferences with the specified key, providing a default value if not found.
     *
     * @param key      The key for the integer value.
     * @param defValue The default value to return if the key is not found.
     * @return The integer value associated with the key, or the default value if not found.
     */
    public int getInteger(String key , int defValue) {
        return pref.getInt(key, defValue);
    }

    /**
     * Sets an integer value in the SharedPreferences with the specified key.
     *
     * @param key   The key for the integer value.
     * @param value The integer value to set.
     */
    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }


    /**
     * Deserializes a JSON string stored in SharedPreferences into an object of the specified class.
     *
     * @param key   The key for the JSON string.
     * @param clazz The class type to deserialize the JSON into.
     * @param <T>   The type of the class.
     * @return An object of the specified class, or a default object if the JSON is not found.
     */
    public <T> T deserialize(String key, Class<T> clazz) {
        String json = this.getString(key);
        if (Validator.isNullOrEmpty(json)) {
            json = "{}";
        }
        return ApplicationLoader.getGson().fromJson(json, clazz);
    }

    /**
     * Inserts a JSON-serializable object into SharedPreferences with the specified key.
     *
     * @param key The key for storing the JSON string.
     * @param o   The object to be serialized and stored.
     */
    public void insertJson(String key, Object o) {
        this.editor.putString(key,ApplicationLoader.getGson().toJson(o));
        editor.apply();
        editor.commit();
    }


    /**
     * Gets a boolean value from the SharedPreferences with the specified key.
     *
     * @param key The key for the boolean value.
     * @return The boolean value associated with the key, or {@code false} if not found.
     */
    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    /**
     * Gets a boolean value from the SharedPreferences with the specified key, providing a default value if not found.
     *
     * @param key      The key for the boolean value.
     * @param defValue The default value to return if the key is not found.
     * @return The boolean value associated with the key, or the default value if not found.
     */
    public boolean getBoolean(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    /**
     * Sets a boolean value in the SharedPreferences with the specified key.
     *
     * @param key   The key for the boolean value.
     * @param value The boolean value to set.
     */
    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
        editor.commit();
    }

}