package com.playsho.android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.playsho.android.R
import com.playsho.android.base.BaseActivity
import com.playsho.android.base.BaseBottomSheet
import com.playsho.android.databinding.ActivitySettingBinding
import com.playsho.android.network.Agent
import com.playsho.android.network.Response
import com.playsho.android.ui.bottomsheet.ChangeNameBottomSheet
import com.playsho.android.utils.KeyStoreHelper
import com.playsho.android.utils.LocalController
import com.playsho.android.utils.RSAHelper
import com.playsho.android.utils.accountmanager.AccountInstance
import retrofit2.Call
import retrofit2.Callback

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
            binding.progress.visibility = View.VISIBLE
            KeyStoreHelper.getInstance().deleteEntry(KeyStoreHelper.KeyAllies.RSA_KEYS)

            val keys = RSAHelper.generateKeyPair()
            // Create a certificate chain containing the public key

            Agent.Device.regenerateKeypair(RSAHelper.printPublicKey(keys))
                .enqueue(object : Callback<Response> {

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        binding.progress.visibility = View.GONE
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
                        binding.progress.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            response.body()?.message
                                ?: LocalController.getString(R.string.rsa_key_pair_successfully_regenerated),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                })

        }

        binding.containerChangeName.setOnClickListener {
            val bottomSheet = ChangeNameBottomSheet()
            bottomSheet.setOnResult(callback = object : BaseBottomSheet.BottomSheetResultCallback {
                override fun onBottomSheetProcessSuccess(data: String) {
                    loadData()
                    Snackbar.make(
                        binding.root,
                        LocalController.getString(R.string.name_successfully_updated),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onBottomSheetProcessFail(data: String) {
                    Snackbar.make(
                        binding.root,
                        data,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
            bottomSheet.show(supportFragmentManager, "name")
        }
        loadData()
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        binding.txtTag.text = "Tag:${AccountInstance.getUserData("tag")}"
        binding.txtUserName.text = AccountInstance.getUserData("user_name")
    }
}