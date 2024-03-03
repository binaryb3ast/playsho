package com.playsho.android.ui

import android.os.Bundle
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivitySettingBinding
import com.playsho.android.utils.accountmanager.AccountInstance

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_setting
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background , true)
        binding.icBack.setOnClickListener{
            finish()
        }
        loadData()
    }

    private fun loadData(){
        binding.txtTag.text = "Tag:${AccountInstance.getUserData("tag")}"
        binding.txtUserName.text = AccountInstance.getUserData("user_name")
    }
}