package com.playsho.android.ui.bottomsheet

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetSendMessageBinding
import com.playsho.android.utils.SystemUtilities
import com.playsho.android.utils.ThemeHelper

class SendMessageBottomSheet() : BaseBottomSheet<BottomSheetSendMessageBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.bottom_sheet_send_message
    }
    lateinit var callback: BottomSheetResultCallback

    var titleArray = arrayOf(
        "Spill the Popcorn",
        "Meme the Moment",
        "Live Commentary (Shhh!)",
        "Battle Royale of Thoughts",
        "Squad Chat: Activate!",
        "Mind Meld with Your Crew",
        "The Aftershow Starts Now",
        "Is This Scene Even Real? Discuss!",
        "Who Needs Subtitles? Talk it Out!",
        "Spoiler Alert (Just Kidding)",
        "The Chat Awakens",
        "Let's Get This Party Started (In the Chat)",
        "Don't Panic (But Talk in the Chat)",
        "Let's Dish About the Movie",
        "Spill the Tea (and Snack Commentary)",
        "Shhh... We're Chatting About the Movie"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.input.background = ThemeHelper.createRect(
            R.color.neutral_100,
            45,
        )

        binding.icSend.setOnClickListener {
            if (binding.input.text.toString().trim().isNotEmpty()) {
                this.callback.onBottomSheetProcessSuccess(binding.input.text.toString().trim())
            }
            dismiss()
        }
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                SystemUtilities.hideKeyboard(binding.input)
                if (binding.input.text.toString().trim().isNotEmpty()) {
                    this.callback.onBottomSheetProcessSuccess(binding.input.text.toString().trim())
                }
                dismiss()
                return@setOnEditorActionListener true  // Return true to indicate that the action was handled
            }
            return@setOnEditorActionListener false // Return false if the action was not handled
        }
    }

    fun setOnResult(callback: BottomSheetResultCallback){
        this.callback = callback
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}