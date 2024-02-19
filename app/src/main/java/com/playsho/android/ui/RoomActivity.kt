package com.playsho.android.ui

import android.os.Bundle
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivityRoomBinding
import com.playsho.android.utils.ThemeHelper

class RoomActivity : BaseActivity<ActivityRoomBinding>() {


    override fun getLayoutResourceId(): Int {
        return R.layout.activity_room
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background, true)
        binding.containerAdd.background = ThemeHelper.createRect(
            R.color.black_background,
            25,
            R.color.neutral_500,
            1,
            4f,
            3f
        )
        binding.containerAddLink.background = ThemeHelper.createRect(
            R.color.black_background,
            25,
            R.color.neutral_500,
            1,
            4f,
            3f
        )
        binding.containerInput.background = ThemeHelper.createRect(
            R.color.neutral_100,
            45,
        )
    }
}