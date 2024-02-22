package com.playsho.android.ui

import android.annotation.SuppressLint
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.base.ApplicationLoader
import com.playsho.android.base.BaseActivity
import com.playsho.android.data.Message
import com.playsho.android.databinding.ActivityRoomBinding
import com.playsho.android.network.SocketManager
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.ThemeHelper
import com.playsho.android.utils.accountmanager.AccountInstance
import kotlin.math.log

class RoomActivity : BaseActivity<ActivityRoomBinding>() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRoomBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null

    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    val gson = Gson()

    private var ROOM_TAG = "";
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_room
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    public override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        SocketManager.joinRoom(getIntentStringExtra("tag") ?: "crash_room")
        if (player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()

    }

    private fun initializePlayer() {
        // ExoPlayer implements the Player interface
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->

                binding.playerView.player = exoPlayer

                // Update the track selection parameters to only pick standard definition tracks
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri("https://dl3.freeserver.top/st01/film/1402/12/The.Zone.of.Interest.2023.480p.WEB-DL.HardSub.DigiMoviez.mp4?md5=OhTtoOF_n_FV1xg_eIyCoA&expires=1708853204")
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build()
                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.addListener(playbackStateListener)
//                exoPlayer.prepare()
            }
            .apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                        Log.e(TAG, "onIsPlayingChanged: $isPlayingValue")
                    }

                    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                        super.onTimelineChanged(timeline, reason)
                        Log.e(TAG, "onTimelineChanged: $reason")
                    }

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        Log.e(TAG, "onPositionDiscontinuity: ${newPosition.positionMs}")

                        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        Log.e(TAG, "onPlaybackStateChanged: $playbackState")
                    }


                    override fun onIsLoadingChanged(isLoading: Boolean) {
                        super.onIsLoadingChanged(isLoading)
                        Log.e(TAG, "onIsLoadingChanged: $isLoading")
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e(TAG, "onPlayerError: ", error)
                    }


                    override fun onTrackSelectionParametersChanged(parameters: TrackSelectionParameters) {
                        super.onTrackSelectionParametersChanged(parameters)

                        val currentProgressTime = player?.currentPosition
                        Log.e(TAG, "Current progress time: $currentProgressTime milliseconds")
                        // You can now use the current progress time as needed


                    }

                })
            }

    }

    private val progressListener = object : Player.Listener {

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            // Called when there is a change in playback position
            val currentPosition = player?.currentPosition ?: 0
            val duration = player?.duration ?: 0
            val progressPercentage = if (duration > 0) {
                (currentPosition * 100 / duration).toInt()
            } else {
                0
            }
            // Now you have the current position and duration, and you can use them as needed
            Log.d(TAG, "Playback Progress: $progressPercentage%")
        }
    }

    private fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
            player.removeListener(playbackStateListener)
            player.release()
        }
        player = null
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ROOM_TAG = getIntentStringExtra("tag") ?: "crash_room"
        SocketManager.joinRoom(ROOM_TAG)
        setStatusBarColor(R.color.black_background, true)
        initUi()
        binding.icSend.setOnClickListener {
            var stringPK: String = AccountInstance.getAuthToken("public_key") ?: "NOT_SET";
            if (stringPK != "NOT_SET") {
                val encryptedMessage = Crypto.encryptMessage(
                    binding.input.text.toString(),
                    Crypto.stringToPublicKey(stringPK)
                )
                sendMsgThroughSocket(encryptedMessage)
            }
        }

        SocketManager.on(SocketManager.EVENTS.NEW_MESSAGE) { data ->
            Log.e(TAG, "NEW_MESSAGE: ")
            val message = gson.fromJson(data[0].toString(), Message::class.java)
            var stringPK: String = AccountInstance.getAuthToken("private_key") ?: "NOT_SET";
            if (stringPK != "NOT_SET") {
                val decryptedMessage = Crypto.decryptMessage(
                    message.message,
                    Crypto.stringToPrivateKey(stringPK)
                )
            }
        }
    }

    private fun sendMsgThroughSocket(msg: String) {
        SocketManager.sendMessage(ROOM_TAG, msg)
    }

    private fun initUi() {
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