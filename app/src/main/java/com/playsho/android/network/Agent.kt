package com.playsho.android.network

import com.playsho.android.config.Conf
import com.playsho.android.utils.DeviceUtils
import okhttp3.FormBody
import okhttp3.RequestBody
import retrofit2.Call
import java.security.PublicKey


object Agent {

    object Device {

        fun generate(privateKey: String): Call<Response> {
            val device = DeviceUtils.createDevice();
            val builder = FormBody.Builder().apply {
                device.secret?.let { add(Conf.Query.SECRET, it) }
                add(Conf.Query.PUBLIC_KEY, privateKey)
                add(Conf.Query.FCM, "not_set")
                add(Conf.Query.BRAND, device.brand)
                add(Conf.Query.MODEL, device.model)
                add(Conf.Query.MANUFACTURER, device.manufacturer)
                add(Conf.Query.FIRST_INSTALL_TIME, device.firstInstallAt.toString())
                add(Conf.Query.LAST_UPDATE_TIME, device.lastUpdateAt.toString())
                add(Conf.Query.OS_VER, device.osVersion)
                add(Conf.Query.APP_VER_NAME, device.appVersionName)
                add(Conf.Query.APP_VER, device.appVersion.toString())
                add(Conf.Query.OS, DeviceUtils.OS)
            }
            val body: RequestBody = builder.build()
            return RetrofitClient.getNetworkConfiguration().generateDevice(body)
        }

        fun regenerateKeypair(publicKey: String): Call<Response> {
            val builder = FormBody.Builder().apply {
                add(Conf.Query.PUBLIC_KEY, publicKey)
            }
            val body: RequestBody = builder.build()
            return RetrofitClient.getNetworkConfiguration().regenerateDeviceKeypair(body)
        }

        fun updateName(name: String): Call<Response> {
            val builder = FormBody.Builder().apply {
                add(Conf.Query.USER_NAME, name)
            }
            val body: RequestBody = builder.build()
            return RetrofitClient.getNetworkConfiguration().updateName(body)
        }
    }

    object Room {
        fun create(): Call<Response> {
            return RetrofitClient.getNetworkConfiguration().createRoom()
        }

        fun get(roomTag: String): Call<Response> {
            return RetrofitClient.getNetworkConfiguration().getRoom(roomTag);
        }

        fun checkEntrance(roomTag: String): Call<Response> {
            return RetrofitClient.getNetworkConfiguration().checkEntrance(roomTag);
        }

        fun addLink(roomTag: String , link:String): Call<Response> {
            val builder = FormBody.Builder().apply {
                add(Conf.Query.STREAM_LINK, link)
            }
            val body: RequestBody = builder.build()
            return RetrofitClient.getNetworkConfiguration().addLink(roomTag,body);
        }
    }


}