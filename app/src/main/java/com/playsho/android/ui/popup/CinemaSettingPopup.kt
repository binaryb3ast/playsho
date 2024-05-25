package com.playsho.android.ui.popup

import android.content.Context
import com.playsho.android.R
import com.playsho.android.base.BasePopup
import com.playsho.android.databinding.PopupCinemaSettingBinding

class CinemaSettingPopup(context: Context) : BasePopup<PopupCinemaSettingBinding>(context) {
    override fun getLayoutResourceId(): Int {
        return R.layout.popup_cinema_setting
    }

    override fun onViewInitialized() {

    }
}