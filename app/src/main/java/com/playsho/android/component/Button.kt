package com.playsho.android.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.playsho.android.R
import com.github.razir.progressbutton.DrawableButton
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.ThemeHelper

class Button @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var mode: ButtonMode = ButtonMode.PRIMARY;
    private var isClickDisable: Boolean = false;
    private var text: String = ""

    enum class ButtonMode {
        PRIMARY,
        SECONDARY
    }

    init {
        if (!isInEditMode) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr, 0)
            mode = when (a.getString(R.styleable.Button_mode) ?: "p") {
                "p" -> ButtonMode.PRIMARY
                "s" -> ButtonMode.SECONDARY
                else -> ButtonMode.PRIMARY // Default value
            }
            isClickDisable = a.getBoolean(R.styleable.Button_mode, false)
            a.recycle()
        }
        text = getText().toString()
        attachTextChangeAnimator {
            fadeInMills = 300
            fadeOutMills = 300
        }
        setBackgroundDrawable(
            if (isClickDisable)
                this.getDisableDrawable()
            else this.getPrimaryDrawable()
        )
        setTextColor(
            LocalController.getColor(
                if (isClickDisable) R.color.button_text_deactive
                else R.color.button_text_active
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            typeface = LocalController.getFont(R.font.roboto_bold)
        }else{
            typeface = LocalController.getFont("roboto_bold.ttf")
        }
        textSize = 14f
        gravity = Gravity.CENTER
    }

    private fun getPrimaryDrawable(): GradientDrawable {
        return ThemeHelper.createRect(R.color.button_primary, 28)
    }

    private fun getDisableDrawable(): GradientDrawable {
        return ThemeHelper.createRect(R.color.button_disable, 28)
    }

    fun startProgress() {
        this.showProgress {
            progressColor = Color.WHITE
            gravity = DrawableButton.GRAVITY_CENTER
        }
        isEnabled = false

    }

    fun stopProgress() {
        hideProgress(text)
        isEnabled = true
    }


}