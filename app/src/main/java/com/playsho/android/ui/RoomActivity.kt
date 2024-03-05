package com.playsho.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.adapter.MessageAdapter
import com.playsho.android.base.BaseActivity
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.data.Device
import com.playsho.android.data.Message
import com.playsho.android.data.Room
import com.playsho.android.databinding.ActivityRoomBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.bottomsheet.AddStreamLinkBottomSheet
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.ThemeHelper
import retrofit2.Call
import retrofit2.Callback
import java.net.URLConnection
import java.security.KeyPair

class RoomActivity : BaseActivity<ActivityRoomBinding>() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRoomBinding.inflate(layoutInflater)
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: Player? = null
    private lateinit var keyPairMap: KeyPair
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    val gson = Gson()
    private lateinit var roomObject: Room
    private var members = mutableMapOf<String, Device>()
    private lateinit var messageAdapter: MessageAdapter
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_room
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    public override fun onStart() {
        super.onStart()
        if (!roomObject.streamLink.isNullOrEmpty())
            initializePlayer()
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null && !roomObject.streamLink.isNullOrEmpty()) {
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

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.leaveRoom(roomObject.tag)
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
        keyPairMap = RSAHelper.getKeyPairs()
        roomObject = Room(
            tag = getIntentStringExtra("tag") ?: "crash_room"
        )
        setStatusBarColor(R.color.black_background, true)
        requestGetRoom(roomObject.tag)
        initUi()
        binding.icSend.setOnClickListener {
            roomObject.roomKey?.let {
                val encryptedMsg = Crypto.encryptAES(binding.input.text.toString(), it)
                sendMsgThroughSocket(encryptedMsg)
                binding.input.setText("")
            }
        }
        binding.containerAddLink.setOnClickListener {
            val bottomSheet = AddStreamLinkBottomSheet(roomObject.tag)
            bottomSheet.setOnResult(callback = object : BaseBottomSheet.BottomSheetResultCallback {
                override fun onBottomSheetProcessSuccess(data: String) {
                    roomObject.streamLink = data
                }

                override fun onBottomSheetProcessFail(data: String) {

                }
            })
            bottomSheet.show(supportFragmentManager, "LINK")
        }
        configRecycler()
    }

    private fun configRecycler() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        binding.recyclerMessage.layoutManager = layoutManager
        messageAdapter = MessageAdapter(mutableListOf())
        binding.recyclerMessage.adapter = messageAdapter
    }

    private fun requestGetRoom(roomTag: String) {
        Agent.Room.get(roomTag).enqueue(object : Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {}

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                if (response.isSuccessful) {
                    response.body()?.result?.room?.tag?.let {
                        SocketManager.joinRoom(it)
                        SocketManager.on(SocketManager.EVENTS.NEW_MESSAGE, ::handleNewMessage)
                        SocketManager.on(SocketManager.EVENTS.JOINED, ::handleJoinMember)
                        SocketManager.on(SocketManager.EVENTS.LEFT, ::handleMemberLeft)
                        SocketManager.on(SocketManager.EVENTS.NEW_LINK, ::handleNewLink)
                    }
                    response.body()?.result?.room?.roomKey?.let {
                        runOnUiThread {
                            roomObject.roomKey = RSAHelper.decrypt(it, keyPairMap.private)
                        }

                    }
                }

            }
        })
    }

    private fun handleNewMessage(data: Array<Any>) {
        val message = gson.fromJson(data[0].toString(), Message::class.java)
        addMemberLocal(message.sender)
        if (message.type != "system") {
            roomObject.roomKey?.let {
                message.message = Crypto.decryptAES(message.message, it)
            }
        } else {
            message.sender.color = members[message.sender.tag]?.color
        }
        runOnUiThread {
            messageAdapter.addMessage(message) // Assuming `adapter` is your MessageAdapter instance
            binding.recyclerMessage.smoothScrollToPosition(0)
        }
    }

    private fun handleNewLink(data: Array<Any>) {
        val message = gson.fromJson(data[0].toString(), Message::class.java)
        roomObject.streamLink = message.payload
        runOnUiThread {
            messageAdapter.addMessage(message) // Assuming `adapter` is your MessageAdapter instance
            binding.recyclerMessage.smoothScrollToPosition(0)
        }
    }

    private fun addMemberLocal(device: Device) {
        val colorArray = listOf("#f46c7d", "#be6cf4", "#6c77f4", "#6cf46e", "#6c77f4", "#6c77f4")
        val randomIndex = (Math.random() * colorArray.size).toInt()
        device.color = colorArray[randomIndex]
        device.tag?.let { tag ->
            if (!members.containsKey(tag)) {
                members[tag] = device
            }
        }
    }

    private fun removeMemberLocal(device: Device) {
        device.tag?.let { tag ->
            if (members.containsKey(tag)) {
                members.remove(tag)
            }
        }
    }

    private fun handleJoinMember(data: Array<Any>) {
        val message = gson.fromJson(data[0].toString(), Message::class.java)
        addMemberLocal(message.sender)
        runOnUiThread {
            messageAdapter.addMessage(message) // Assuming `adapter` is your MessageAdapter instance
            binding.recyclerMessage.smoothScrollToPosition(0)
        }
        Log.e(TAG, "handleJoinMember: " + members.size)
    }

    private fun handleMemberLeft(data: Array<Any>) {
        val message = gson.fromJson(data[0].toString(), Message::class.java)
        removeMemberLocal(message.sender)
        runOnUiThread {
            messageAdapter.addMessage(message) // Assuming `adapter` is your MessageAdapter instance
            binding.recyclerMessage.smoothScrollToPosition(0)
        }
        Log.e(TAG, "handleMemberLeft: " + members.size)
    }

    private fun sendMsgThroughSocket(msg: String) {
        SocketManager.sendMessage(roomObject.tag, msg)
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