package com.playsho.android.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.databinding.ActivitySettingBinding
import com.playsho.android.databinding.BottomSheetChangeNameBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.network.SocketManager
import com.playsho.android.ui.bottomsheet.ChangeNameBottomSheet
import com.playsho.android.utils.KeyStoreHelper
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.accountmanager.AccountInstance
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayInputStream
import java.security.KeyPair
import java.security.PublicKey
import java.security.cert.CertificateFactory

class SettingActivity : BaseActivity<ActivitySettingBinding>() {


    override fun getLayoutResourceId(): Int {
        return R.layout.activity_setting
    }

    override fun onBackPress() {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.black_background, true)
        binding.icBack.setOnClickListener {
            finish()
        }
        binding.containerGenerateKeys.setOnClickListener {
            KeyStoreHelper.getInstance().deleteEntry(KeyStoreHelper.KeyAllies.RSA_KEYS)

            val keys = RSAHelper.generateKeyPair();
            // Create a certificate chain containing the public key

            Agent.Device.regenerateKeypair(RSAHelper.printPublicKey(keys))
                .enqueue(object : Callback<Response> {

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        Snackbar.make(
                            binding.root,
                            LocalController.getString(R.string.rsa_key_pair_successfully_regenerated),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<Response>,
                        response: retrofit2.Response<Response>
                    ) {
                        Snackbar.make(
                            binding.root,
                            response.body()?.message ?: LocalController.getString(R.string.rsa_key_pair_successfully_regenerated),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                })

        }

        binding.containerChangeName.setOnClickListener{
            var bottomsheet = ChangeNameBottomSheet()
            bottomsheet.show(supportFragmentManager , "name")
        }
        loadData()
    }

    private fun loadData() {
        binding.txtTag.text = "Tag:${AccountInstance.getUserData("tag")}"
        binding.txtUserName.text = AccountInstance.getUserData("user_name")
    }
}