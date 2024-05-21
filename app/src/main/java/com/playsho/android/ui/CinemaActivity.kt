package com.playsho.android.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
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
import com.playsho.android.databinding.ActivityRoomBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.bottomsheet.AddStreamLinkBottomSheet
import com.playsho.android.ui.bottomsheet.JoinRoomBottomSheet
import com.playsho.android.ui.bottomsheet.SendMessageBottomSheet
import com.playsho.android.utils.ClipboardHandler
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.DimensionUtils
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.ThemeHelper
import com.playsho.android.utils.Validator
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

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_cinema
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    var titleAddLinkArray = arrayOf(
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
        requestGetRoom(roomObject.tag)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        keyPairMap = RSAHelper.getKeyPairs()
        binding.playerView.setControllerVisibilityListener(ControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE){
                adjust(80f)
                binding.toolbar.visibility = View.VISIBLE
            } else {
                adjust(0f)
                binding.toolbar.visibility = View.GONE
            }
        })
        initializePlayer()
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

    class ClickHandler(private val activity: CinemaActivity) {

        fun onAddLinkPress(view: View){
            val bottomSheet = AddStreamLinkBottomSheet(activity.roomObject.tag)
        }

        fun onMessageIconPressed(view: View){
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
            bottomSheet.show(activity.supportFragmentManager , "messsage")
        }
    }

    private fun sendMsgThroughSocket(msg: String) {
        SocketManager.sendMessage(roomObject.tag, msg)
    }

    private fun adjust(value: Float){
        val layoutParams = binding.recyclerMessage.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = DimensionUtils.dpToPx(value)
        binding.recyclerMessage.layoutParams = layoutParams
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
                    .setUri(roomObject.streamLink)
                    .setMimeType(MimeTypes.VIDEO_MP4)
                    .build()

                exoPlayer.setMediaItems(listOf(mediaItem), mediaItemIndex, playbackPosition)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.prepare()
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

}