package com.playsho.android.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import java.net.URLConnection

object PlayerUtils {

    @OptIn(DelicateCoroutinesApi::class)
    fun getUrlMimeType(url: String, callback: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                val mimeType = connection.contentType
                callback(mimeType)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }
}