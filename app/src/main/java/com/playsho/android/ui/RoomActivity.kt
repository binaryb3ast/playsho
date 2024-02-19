package com.playsho.android.ui

import android.os.Bundle
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivityRoomBinding

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

    }
}