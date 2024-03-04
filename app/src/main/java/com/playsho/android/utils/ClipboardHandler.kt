package com.playsho.android.utils

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import com.playsho.android.base.ApplicationLoader

object ClipboardHandler {
    private var clipBoardInstance : ClipboardManager = ApplicationLoader.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun hasTextPlainData():Boolean {
        return clipBoardInstance.hasPrimaryClip() &&
                clipBoardInstance.primaryClipDescription?.hasMimeType(
                    ClipDescription.MIMETYPE_TEXT_PLAIN
                ) == true
    }

    fun getIndex(index:Int):String{
        return clipBoardInstance.primaryClip?.getItemAt(index)?.text.toString()
    }
}