package com.playsho.android.ui.bottomsheet

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.View
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetChangeNameBinding
import com.playsho.android.databinding.BottomSheetJoinRoomBinding
import com.playsho.android.ui.RoomActivity
import com.playsho.android.utils.ThemeHelper
import com.playsho.android.utils.accountmanager.AccountInstance

class ChangeNameBottomSheet : BaseBottomSheet<BottomSheetChangeNameBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.bottom_sheet_change_name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.input.setText(AccountInstance.getUserData("user_name"))
        binding.input.requestFocus() // Set focus to EditText
        binding.input.selectAll() // Select all text in EditText
        binding.input.background = ThemeHelper.createRect(
            R.color.neutral_100,
            45,
        )
        binding.btn.setOnClickListener{

        }
    }

    private fun requestChangeName(){

    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}