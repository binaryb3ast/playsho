package com.playsho.android.config

object Conf {

    object HttpHeader {
        const val AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE = "Content-Type"
    }

    object Path {
        const val V1_DEVICE_GENERATE = "/v1/api/device/generate"
        const val V1_DEVICE_NAME = "/v1/api/device/name"
        const val V1_DEVICE_KEYPAIR_REGENERATE = "/v1/api/device/keypair/regenerate"
        const val V1_ROOM_CREATE = "/v1/api/room"
        const val V1_ROOM_GET = "/v1/api/room/{tag}"
        const val V1_ROOM_ENTRANCE = "/v1/api/room/{tag}/entrance"
        const val V1_ROOM_LINK = "/v1/api/room/{tag}/link"
    }

    object Query {
        const val STREAM_LINK = "stream_link"
        const val OS = "os"
        const val APP_VER = "app_version"
        const val APP_VER_NAME = "app_version_name"
        const val OS_VER = "os_version"
        const val LAST_UPDATE_TIME = "last_update_at"
        const val FIRST_INSTALL_TIME = "first_install_at"
        const val MANUFACTURER = "manufacturer"
        const val MODEL = "device_model"
        const val BRAND = "brand"
        const val FCM = "fcm"
        const val SECRET = "secret"
        const val PUBLIC_KEY = "public_key"
        const val USER_NAME = "user_name"
    }

}