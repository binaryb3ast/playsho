package com.playsho.android.ui.bottomsheet

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.View
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetJoinRoomBinding
import com.playsho.android.ui.RoomActivity
import com.playsho.android.utils.ThemeHelper

class JoinRoomBottomSheet : BaseBottomSheet<BottomSheetJoinRoomBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.bottom_sheet_join_room
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.input.background = ThemeHelper.createRect(
            R.color.neutral_100,
            45,
        )
        binding.btn.setOnClickListener{
            val intent =Intent(activity, RoomActivity::class.java).apply {
                 putExtra("tag" , binding.input.text.toString().trim())
            }
            activity?.startActivity(intent)
            dismiss()
        }
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}