package com.playsho.android.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.security.KeyPair


class RoomActivity : BaseActivity<ActivityRoomBinding>() {


    private lateinit var keyPairMap: KeyPair
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

    override fun onDestroy() {
        super.onDestroy()
        SocketManager.leaveRoom(roomObject.tag)
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.containerAdd.setOnClickListener{

        }
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
            binding.containerAddLink.visibility = View.GONE
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