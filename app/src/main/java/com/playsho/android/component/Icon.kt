package com.playsho.android.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.net.Uri
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.playsho.android.R
import com.playsho.android.utils.LocalController
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class Icon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView (context, attrs, defStyleAttr) {


    init {
        if (!isInEditMode) {
            attrs?.let {
                val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.Icon, defStyleAttr, 0)
                val color = a.getColor(R.styleable.Icon_color, 0)
                if (color != 0) {
                    setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                invalidate()
                a.recycle()
            }

        }
    }

    fun setColor(color: Int) {
        if (color != 0) {
            setColorFilter(LocalController.getColor(color), PorterDuff.Mode.SRC_ATOP)
        }
        invalidate()
    }

    fun load(uri: Uri) {
        Picasso.get().load(uri).into(this)
    }

    fun load(file: File) {
        Picasso.get().load(file).into(this)
    }

    fun load(path: String) {
        Picasso.get().load(path).into(this)
    }

    fun load(resourceId: Int) {
        Picasso.get().load(resourceId).into(this)
    }

}