package com.playsho.android.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.BottomSheetJoinRoomBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.ui.RoomActivity
import com.playsho.android.utils.ClipboardHandler
import com.playsho.android.utils.SystemUtilities
import com.playsho.android.utils.ThemeHelper
import retrofit2.Call
import retrofit2.Callback

class JoinRoomBottomSheet : BaseBottomSheet<BottomSheetJoinRoomBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.bottom_sheet_join_room
    }

    var titleArray = arrayOf(
        "The Screening Roommate: Gather 'Round the Virtual Room!",
        "Roomie Reel Time: Dive into the Movie Nook!",
        "The Movie Mogul's Lair: Join the Exclusive Room!",
        "Movie Mania: Snag Your Spot in the Room!",
        "The Showtime Salon: Welcome to the Cozy Room!",
        "Film Fanatics' Fiesta: Shake Up the Screening Room!",
        "The Celluloid Chamber: Step into the Epic Room!",
        "Screening Squad Shenanigans: Occupy the Movie Magic Room!",
        "The Reel Roomies Retreat: Lock in Your Place in the Room!",
        "Cinema Sanctuary: Secure Your Spot in the Room!",
        "Tag 'n' Tune: Link Up with Your Roomies for Movie Magic!",
        "Room Tag Rendezvous: Connect and Catch Flicks with Friends!",
        "Movie Mate Mixer: Punch in the Room Tag and Let's Roll!",
        "Roomie Reunion: Enter the Tag to Unlock the Movie Haven!",
        "Tagged and Toasted: Join the Room Tag for a Movie Mashup!",
        "Flick 'n' Tag Fiesta: Punch in the Room Tag for Film Fun!",
        "Tagged Theater: Dial in the Room Tag for Movie Marvels!",
        "Roomie Romp: Swipe Right with the Room Tag for Movie Madness!",
        "Movie Night Maneuver: Crack the Room Tag and Gather 'Round!",
        "Tag Team Tune-In: Dial Up the Room Tag for Movie Mayhem!",
        "Tagged Talkies: Plug in the Room Tag for Movie Magic!",
        "Roomie Roll Call: Dial in the Tag and Dive into Movies!",
        "Flick Tag Frenzy: Punch in the Room Tag and Popcorn Up!",
        "Tagged Viewing Vault: Enter the Room Tag for Film Fun!",
        "Movie Mate Mashup: Dial in the Room Tag and Let's Watch!",
        "Tag 'n' Watch Bash: Connect with the Room Tag for Fun Flicks!",
        "Cinephile Code: Unlock the Room Tag and Join the Movie Marathon!",
        "Tagged Theater Thrills: Tune in with the Room Tag for Movie Madness!",
        "Roomie Reel Rendezvous: Gather 'Round with the Room Tag for Films!",
        "Movie Tag Mania: Dial Up the Room Tag for Epic Entertainment!"
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
            val prefix = "https://playsho.com/room/"
            if (copiedText.startsWith(prefix)) {
                binding.input.setText(copiedText.removePrefix(prefix))
            }
        }
        binding.btn.setOnClickListener {
            if (binding.input.text.toString().trim().isEmpty()) {
                Snackbar.make(
                    dialog?.window?.decorView
                        ?: requireActivity().findViewById(android.R.id.content),
                    getString(R.string.oops_looks_like_the_room_tag_is_missing),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                requestCheckRoom(extractTagFromInput())
            }
        }
        binding.input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                SystemUtilities.hideKeyboard(binding.input)
                if (binding.input.text.toString().trim().isEmpty()) {
                    Snackbar.make(
                        dialog?.window?.decorView
                            ?: requireActivity().findViewById(android.R.id.content),
                        getString(R.string.oops_looks_like_the_room_tag_is_missing),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    requestCheckRoom(extractTagFromInput())
                }
                return@setOnEditorActionListener true  // Return true to indicate that the action was handled
            }
            return@setOnEditorActionListener false // Return false if the action was not handled
        }
    }

    private fun extractTagFromInput(): String {
        var tag = binding.input.text.toString().trim()
        tag = tag.replace("https://playsho.com/room/", "")
        return tag;
    }


    private fun requestCheckRoom(tag: String) {
        Agent.Room.checkEntrance(tag).enqueue(object :
            Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Snackbar.make(
                    dialog?.window?.decorView
                        ?: requireActivity().findViewById(android.R.id.content),
                    t.message ?: "Error",
                    Snackbar.LENGTH_LONG
                ).show()
                binding.btn.stopProgress()
            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                binding.btn.stopProgress()
                if (response.isSuccessful) {
                    val intent = Intent(activity, RoomActivity::class.java).apply {
                        putExtra("tag", response.body()?.result?.room?.tag)
                    }
                    activity?.startActivity(intent)
                    binding.btn.stopProgress()
                    dismiss()
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
                }

            }
        })
    }

    override fun initView() {
        TODO("Not yet implemented")
    }
}