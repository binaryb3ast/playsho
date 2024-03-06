package com.playsho.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.playsho.android.R
import com.playsho.android.base.ApplicationLoader
import com.playsho.android.base.BaseActivity
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.ActivitySplashBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.bottomsheet.JoinRoomBottomSheet
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.DimensionUtils
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.accountmanager.AccountInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import java.security.KeyPair


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val SIDE_MARGIN: Int = 140
    private lateinit var keyPairMap: KeyPair
    private var encryptedText = ""

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_splash;
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.txtName.text = "Logged in as  ${AccountInstance.getUserData("user_name")}."
    }
    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background, true)
        setupLogoWidth()

        binding.txtDescription.typeWrite(
            this,
            LocalController.getString(R.string.splash_description),
            33
        )

        if (AccountInstance.hasAnyAccount()) {
            AccountInstance.use(AccountInstance.getAccounts()[0])
            binding.txtName.text = "Logged in as  ${AccountInstance.getUserData("user_name")}."
            SocketManager.initialize().establish()
        } else {
           requestGenerateDevice()
        }
        binding.icSetting.setOnClickListener {
            openActivity<SettingActivity>()
        }

        binding.btn.setOnClickListener {
            binding.btn.startProgress()
            if (AccountInstance.hasAnyAccount()){
                requestCreateRoom()
            }else{
//                requestGenerateDevice()
            }
        }
        binding.btnJoin.setOnClickListener{
            val bottomSheet = JoinRoomBottomSheet()
            bottomSheet.show(supportFragmentManager , "join")
        }
    }

    private fun requestGenerateDevice(){
        binding.txtName.text = LocalController.getString(R.string.loading_with_dot)
        keyPairMap = RSAHelper.generateKeyPair()
        Agent.Device.generate(RSAHelper.printPublicKey(keyPairMap)).enqueue(object : Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                binding.txtName.text = "Error !"

            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                AccountInstance.removeAccount(response.body()?.result?.device?.userName ?: "unknown")
                val account = AccountInstance.createAccount(
                    response.body()?.result?.device?.userName ?: "unknown", "", Bundle().apply {
                        putString(
                            "user_name",
                            response.body()?.result?.device?.userName ?: "NO NAME"
                        )
                        putString("tag", response.body()?.result?.device?.tag)
                    });

                binding.txtName.text =
                    "Login as ${response.body()?.result?.device?.userName ?: "ERROR"}"

                AccountInstance.use(account);
                response.body()?.result?.device?.token?.let {
                    AccountInstance.setAuthToken(
                        account, "Bearer",
                        it
                    )
                }
                SocketManager.initialize().establish()
            }
        })
    }

    private fun requestCreateRoom() {

        Agent.Room.create().enqueue(object : Callback<Response> {

            override fun onFailure(call: Call<Response>, t: Throwable) {
                binding.btn.stopProgress()
            }

            override fun onResponse(
                call: Call<Response>,
                response: retrofit2.Response<Response>
            ) {
                if(response.isSuccessful){
                    binding.btn.stopProgress()
                    openActivity<CinemaActivity>(
                        "tag" to response.body()?.result?.room?.tag
                    )
                }

            }
        })
    }

    private fun TextView.typeWrite(lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
        this@typeWrite.text = ""
        lifecycleOwner.lifecycleScope.launch {
            repeat(text.length) {
                delay(intervalMs)
                this@typeWrite.text = text.take(it + 1)
            }
        }
    }

    private fun setupLogoWidth() {
        val layoutParams = binding.imgLogo.layoutParams
        val screenWidth = DimensionUtils.getDisplayWidthInPixel()
        val totalSideMargin = DimensionUtils.dpToPx((SIDE_MARGIN * 2).toFloat())
        val calculatedWidth = screenWidth - totalSideMargin
        layoutParams.width = calculatedWidth
    }


}