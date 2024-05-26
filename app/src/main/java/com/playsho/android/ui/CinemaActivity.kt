package com.playsho.android.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.playsho.android.R
import com.playsho.android.adapter.MessageAdapter
import com.playsho.android.base.BaseActivity
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.data.Device
import com.playsho.android.data.Message
import com.playsho.android.data.Room
import com.playsho.android.databinding.ActivityCinemaBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.bottomsheet.AddStreamLinkBottomSheet
import com.playsho.android.ui.bottomsheet.SendMessageBottomSheet
import com.playsho.android.ui.popup.CinemaSettingPopup
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.DimensionUtils
import com.playsho.android.utils.RSAHelper
import retrofit2.Call
import retrofit2.Callback
import java.security.KeyPair


class CinemaActivity : BaseActivity<ActivityCinemaBinding>() {

    private var playWhenReady = false
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private var player: Player? = null
    private lateinit var roomObject: Room
    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityCinemaBinding.inflate(layoutInflater)
    }
    private lateinit var keyPairMap: KeyPair
    val gson = Gson()
    private var members = mutableMapOf<String, Device>()
    private lateinit var messageAdapter: MessageAdapter
    private var syncWith: String = "d7c1e46b-aea5-4649-bc4c-cddbb58806ab"

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_cinema
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
        if (player == null && !roomObject.streamLink.isNullOrEmpty()) {
            initializePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        roomObject.tag.let { SocketManager.leaveRoom(it) }
        super.onDestroy()
    }

    private fun releasePlayer() {
        player?.let { player ->
            playbackPosition = player.currentPosition
            mediaItemIndex = player.currentMediaItemIndex
            playWhenReady = player.playWhenReady
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.handler = ClickHandler(this)
        roomObject = Room(
            tag = getIntentStringExtra("tag") ?: "crash_room"
        )
        binding.txtRoomTitle.text = roomObject.tag
        requestGetRoom(roomObject.tag)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        keyPairMap = RSAHelper.getKeyPairs()
        binding.playerView.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                adjust(80f)
                binding.toolbar.visibility = View.VISIBLE
            } else {
                adjust(10f)
                binding.toolbar.visibility = View.GONE
            }
        })
        if (player == null && !roomObject.streamLink.isNullOrEmpty()) {
            initializePlayer()
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
            if (syncWith.isNotEmpty() && syncWith == message.sender.tag){
                runOnUiThread {
                    when (message.action) {
                        "pause" -> {
                            player?.stop()
                        }

                        "buffering" -> {
                            player?.stop()
                        }

                        "resume" -> {
                            player?.release()
                        }

                        "scrub" -> {
                            player?.seekTo(message.data.toLong())
                        }
                    }
                }
            }
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
            binding.playerView.visibility = View.VISIBLE
            initializePlayer()
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

    class ClickHandler(private val activity: CinemaActivity) {

        fun onSettingPressed(view: View) {
            var popup = CinemaSettingPopup(activity)
            popup.showAtLocation(view)
        }

        fun onAddLinkPress(view: View) {
            val bottomSheet = AddStreamLinkBottomSheet(activity.roomObject.tag)
            bottomSheet.setOnResult(callback = object : BaseBottomSheet.BottomSheetResultCallback {
                override fun onBottomSheetProcessSuccess(data: String) {
                    activity.roomObject.streamLink = data
                }

                override fun onBottomSheetProcessFail(data: String) {

                }
            })
            bottomSheet.show(activity.supportFragmentManager, "link")
        }

        fun onMessageIconPressed(view: View) {
            val bottomSheet = SendMessageBottomSheet()
            bottomSheet.setOnResult(callback = object : BaseBottomSheet.BottomSheetResultCallback {
                override fun onBottomSheetProcessSuccess(data: String) {
                    activity.roomObject.roomKey?.let {
                        val encryptedMsg = Crypto.encryptAES(data, it)
                        activity.sendMsgThroughSocket(encryptedMsg)
                    }
                }

                override fun onBottomSheetProcessFail(data: String) {

                }
            })
            bottomSheet.show(activity.supportFragmentManager, "messsage")
        }
    }

    private fun sendMsgThroughSocket(msg: String) {
        SocketManager.sendMessage(roomObject.tag, msg)
    }

    private fun adjust(value: Float) {
        val layoutParams = binding.recyclerMessage.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = DimensionUtils.dpToPx(value)
        binding.recyclerMessage.layoutParams = layoutParams
    }

    @SuppressLint("DefaultLocale")
    private fun getFormattedCurrentPosition(currentPosition: Long): String {
        val seconds = (currentPosition / 1000).toInt()
        val minutes = (seconds / 60)
        val secondsRemainder = (seconds % 60)
        return String.format("%02d:%02d", minutes, secondsRemainder)
    }

    @OptIn(UnstableApi::class)
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
                    .setUri(roomObject.streamLink)
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build()

                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.prepare()
            }
            .apply {
                val seekBar: DefaultTimeBar =
                    binding.playerView.findViewById(androidx.media3.ui.R.id.exo_progress) // Replace with your SeekBar ID
                seekBar.setPlayedColor(Color.RED);

                seekBar.setUnplayedColor(Color.GRAY);

                seekBar.setBufferedColor(Color.BLUE);

                seekBar.setScrubberColor(Color.GREEN);

                seekBar.addListener(object : TimeBar.OnScrubListener {
                    override fun onScrubStart(timeBar: TimeBar, position: Long) {
                        // Called when the user starts scrubbing
                        Log.e("TimeBar", "Scrub started at position: $position")
                    }

                    override fun onScrubMove(timeBar: TimeBar, position: Long) {
                        // Called when the user moves the scrubber
                        Log.e("TimeBar", "Scrubbing to position: $position")
                    }

                    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                        // Called when the user stops scrubbing
                        Log.e("TimeBar", "Scrub stopped at position: $position")
                        if (!canceled) {
                            SocketManager.sendPlayerState(
                                roomObject.tag, "scrub",
                                position.toString()
                            )
                        }
                    }
                })

                addListener(object : Player.Listener {

                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        super.onPlayWhenReadyChanged(playWhenReady, reason)
                        Log.e("test_player", "onPlayWhenReadyChanged: $playWhenReady $reason")
                        Log.d(
                            "test_player",
                            "Current playback position: $${player!!.getCurrentPosition()} ms (formatted: $${
                                getFormattedCurrentPosition(player!!.getCurrentPosition())
                            })"
                        )
                        SocketManager.sendPlayerState(
                            roomObject.tag,
                            if (playWhenReady) "resume" else "pause",
                            player?.currentPosition.toString()
                        )
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        val stateString = when (playbackState) {
                            ExoPlayer.STATE_IDLE -> "idle"
                            ExoPlayer.STATE_BUFFERING -> "buffering"
                            ExoPlayer.STATE_READY -> "ready"
                            ExoPlayer.STATE_ENDED -> "ended"
                            else -> "unknown ($playbackState)"
                        }
                        Log.d(
                            "test_player",
                            "Current playback position: $${player!!.getCurrentPosition()} ms (formatted: $${
                                getFormattedCurrentPosition(player!!.getCurrentPosition())
                            })"
                        )
                        SocketManager.sendPlayerState(
                            roomObject.tag,
                            stateString,
                            player?.currentPosition.toString()
                        )
                        Log.d("test_player", "onPlaybackStateChanged: $stateString")
                        Log.e("test_player", "onPlaybackStateChanged: $playbackState")
                    }

                })
            }

    }

}