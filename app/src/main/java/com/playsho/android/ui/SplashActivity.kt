package com.playsho.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.playsho.android.R
import com.playsho.android.base.ApplicationLoader
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivitySplashBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.utils.Crypto
import com.playsho.android.utils.DimensionUtils
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.accountmanager.AccountInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val SIDE_MARGIN: Int = 140
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_splash;
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background, true)
        setupLogoWidth()
        binding.txtDescription.typeWrite(
            this,
            LocalController.getString(R.string.splash_description),
            33
        )
        binding.btn.setOnClickListener {
            openActivity<RoomActivity>(
                "code" to "value"
            )
        }
        if (AccountInstance.hasAnyAccount()) {
            AccountInstance.use(AccountInstance.getAccounts()[0])
            binding.txtName.text = "Login as ${AccountInstance.getUserData("user_name")}"
            SocketManager.initialize().establish()
        }
        else {
            val keyPair = Crypto.generateRSAKeyPair()
            // Convert public key to string
            val publicKeyString = Crypto.publicKeyToString(keyPair.public)
            // Convert private key to string
            val privateKeyString = Crypto.privateKeyToString(keyPair.private)
            Agent.Device.generate(publicKeyString).enqueue(object : Callback<Response> {

                override fun onFailure(call: Call<Response>, t: Throwable) {}

                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    val account = AccountInstance.createAccount(
                        response.body()?.result?.device?.userName ?: "", "", Bundle().apply {
                            putString(
                                "user_name",
                                response.body()?.result?.device?.userName ?: "NO NAME"
                            )
                            putString("tag", response.body()?.result?.device?.tag)
                        });
                    binding.txtName.text = "Login as ${response.body()?.result?.device?.userName ?: "ERROR"}"

                    AccountInstance.use(account);
                    response.body()?.result?.device?.token?.let {
                        AccountInstance.setAuthToken(account,"Bearer",
                            it
                        )
                    }
                    AccountInstance.setAuthToken(account,"private_key", privateKeyString)
                    AccountInstance.setAuthToken(account,"public_key", publicKeyString)
                }
            })
        }
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