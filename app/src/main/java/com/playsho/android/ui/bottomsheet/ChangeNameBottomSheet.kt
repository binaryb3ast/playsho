package com.playsho.android.ui.bottomsheet

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetChangeNameBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.utils.SystemUtilities
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
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                SystemUtilities.hideKeyboard(binding.input)
                requestChangeName()
                // Handle the "Go" action here
                // For example, perform some action or submit the form
                return@setOnEditorActionListener true  // Return true to indicate that the action was handled
            }
            return@setOnEditorActionListener false // Return false if the action was not handled
        }
    }

    fun setOnResult(callback: BottomSheetResultCallback){
        this.callback = callback
    }

    private fun requestChangeName(){
        SystemUtilities.hideKeyboard(binding.input)
        binding.btn.startProgress()
        Agent.Device.updateName(binding.input.text.toString()).enqueue(object :
            Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                binding.input.requestFocus()
                binding.input.selectAll()
                SystemUtilities.showKeyboard(binding.input)
                binding.btn.stopProgress()
                callback.onBottomSheetProcessFail("")
            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                binding.btn.stopProgress()
                if (response.isSuccessful){
                    AccountInstance.updateAccountUserData("user_name" , binding.input.text.toString())
                    binding.btn.stopProgress()
                    callback.onBottomSheetProcessSuccess(binding.input.text.toString())
                    dismiss()
                }else{
                    val errorResponse = response.errorBody()?.string()?.let {
                        Gson().fromJson(it, Response::class.java)
                    }
                    Snackbar.make(
                        dialog?.window?.decorView ?:requireActivity().findViewById(android.R.id.content),
                        errorResponse?.errors?.getOrNull(0)?.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            }
        })
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}