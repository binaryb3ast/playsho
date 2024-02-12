package com.playsho.android.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat

class PhotoBoard : AppCompatImageView {
    constructor(context: Context) : super(context) {
        // Initialize your custom view
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        // Initialize your custom view with attributes
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // Initialize your custom view with attributes and style
    }
}