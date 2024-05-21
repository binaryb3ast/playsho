package com.playsho.android.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetAddLinkBinding
import com.playsho.android.databinding.BottomSheetJoinRoomBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.ui.RoomActivity
import com.playsho.android.utils.ClipboardHandler
import com.playsho.android.utils.SystemUtilities
import com.playsho.android.utils.ThemeHelper
import retrofit2.Call
import retrofit2.Callback

class AddStreamLinkBottomSheet(private val roomTag: String) : BaseBottomSheet<BottomSheetAddLinkBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.bottom_sheet_add_link
    }
    lateinit var callback: BottomSheetResultCallback

    var titleArray = arrayOf(
        "Movie Time is Calling!",
        "Lights, Camera, Link!",
        "Ready to Roll?",
        "Lights, Link, Action!",
        "Stream It Like You Mean It!",
        "Let's Dive into Movie Magic!",
        "Ready to Hit Play?",
        "Stream Link Zone",
        "Enter the Stream Zone!",
        "Time to Tune In!",
        "Link Up for Movie Night!",
        "Streaming Party Starts Here!",
        "Let's Get Streaming!",
        "Ready, Set, Stream!",
        "Hit Play and Share the Fun!",
        "Lights, Link, Flicks!",
        "Stream Team, Assemble!",
        "It's Showtime!",
        "Enter the Movie Zone!",
        "Ready for Movie Madness?",
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTitle.text = titleArray.random()

        binding.input.background = ThemeHelper.createRect(
            R.color.neutral_100,
            45,
        )
        if (ClipboardHandler.hasTextPlainData()) {
            val copiedText = ClipboardHandler.getIndex(0)
            if (isUrl(copiedText)) {
                binding.input.setText(copiedText)
            }
        }
        binding.btn.setOnClickListener {
            if (binding.input.text.toString().trim().isEmpty()) {
                Snackbar.make(
                    dialog?.window?.decorView
                        ?: requireActivity().findViewById(android.R.id.content),
                    getString(R.string.hey_there_it_looks_like_you_haven_t_entered_a_valid_stream_link),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                requestAddLink( binding.input.text.toString().trim())
            }
        }
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                SystemUtilities.hideKeyboard(binding.input)
                if (binding.input.text.toString().trim().isEmpty()) {
                    Snackbar.make(
                        dialog?.window?.decorView
                            ?: requireActivity().findViewById(android.R.id.content),
                        getString(R.string.hey_there_it_looks_like_you_haven_t_entered_a_valid_stream_link),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    requestAddLink( binding.input.text.toString().trim())
                }
                return@setOnEditorActionListener true  // Return true to indicate that the action was handled
            }
            return@setOnEditorActionListener false // Return false if the action was not handled
        }
    }

    private fun isUrl(text: String): Boolean {
        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

    fun setOnResult(callback: BottomSheetResultCallback){
        this.callback = callback
    }

    private fun requestAddLink(url: String) {
        Agent.Room.addLink(roomTag , url).enqueue(object :
            Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Snackbar.make(
                    dialog?.window?.decorView
                        ?: requireActivity().findViewById(android.R.id.content),
                    t.message ?: "Error",
                    Snackbar.LENGTH_LONG
                ).show()
                binding.btn.stopProgress()
                callback.onBottomSheetProcessFail(t.message ?: "Error")

            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                binding.btn.stopProgress()
                if (response.isSuccessful) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        response.body()?.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.btn.stopProgress()
                    dismiss()
                    callback.onBottomSheetProcessSuccess(binding.input.text.toString())
                } else {
                    val errorResponse = response.errorBody()?.string()?.let {
                        Gson().fromJson(it, Response::class.java)
                    }
                    Snackbar.make(
                        dialog?.window?.decorView
                            ?: requireActivity().findViewById(android.R.id.content),
                        errorResponse?.errors?.getOrNull(0)?.message ?: "Error",
                        Snackbar.LENGTH_LONG
                    ).show()
                    callback.onBottomSheetProcessFail(errorResponse?.errors?.getOrNull(0)?.message ?: "Error")

                }

            }
        })
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}