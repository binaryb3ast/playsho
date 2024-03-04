package com.playsho.android.ui.bottomsheet

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.service.carrier.CarrierMessagingService.ResultCallback
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetChangeNameBinding
import com.playsho.android.databinding.BottomSheetJoinRoomBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.RoomActivity
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.ThemeHelper
import com.playsho.android.utils.accountmanager.AccountInstance
import retrofit2.Call
import retrofit2.Callback

class ChangeNameBottomSheet : BaseBottomSheet<BottomSheetChangeNameBinding>() {

    lateinit var callback: BottomSheetResultCallback

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
            requestChangeName()
        }
    }

    fun setOnResult(callback: BottomSheetResultCallback){
        this.callback = callback
    }

    private fun requestChangeName(){
        binding.btn.startProgress()
        Agent.Device.updateName(binding.input.text.toString()).enqueue(object :
            Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                binding.btn.stopProgress()
                callback.onBottomSheetProcessFail("")
            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                AccountInstance.updateAccountUserData("user_name" , binding.input.text.toString())
                dismiss()
                binding.btn.stopProgress()
                callback.onBottomSheetProcessSuccess(binding.input.text.toString())

            }
        })
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}