package com.playsho.android.network
import com.google.gson.GsonBuilder
import com.playsho.android.config.Conf
import com.playsho.android.utils.DebugUtils
import com.playsho.android.utils.Validator
import com.playsho.android.utils.accountmanager.AccountInstance
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val REQUEST_READ_TIME_OUT = 15L
    private const val REQUEST_CONNECT_TIME_OUT = 15L
    private const val BASE_URL_TEST = "https://93be-5-122-195-155.ngrok-free.app/"
    private const val BASE_URL = "https://d.digilog.pro/"

    private val CONNECTION_SPECS = listOf(
        ConnectionSpec.COMPATIBLE_TLS,
        ConnectionSpec.RESTRICTED_TLS,
        ConnectionSpec.CLEARTEXT,
        ConnectionSpec.MODERN_TLS
    )

    private var instance: Retrofit? = null

    private fun getInstance(): Retrofit {
        return instance ?: synchronized(this) {
            instance ?: buildRetrofit().also { instance = it }
        }
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_TEST)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(getHttpClient())
            .build()
    }

    fun getBaseUrl(): String {
        return BASE_URL
    }

    private fun getHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.addHeader(Conf.HttpHeader.CONTENT_TYPE, "application/json")
                if (!Validator.isNull(AccountInstance.getCurrentAccount())) {
                    requestBuilder.addHeader(
                        Conf.HttpHeader.AUTHORIZATION,
                        "Bearer ".plus(AccountInstance.getCurrentAccount()
                            ?.let { AccountInstance.getAuthToken(it, "Bearer") })
                    )
                }
                chain.proceed(requestBuilder.build())
            }
            .connectionSpecs(CONNECTION_SPECS)
            .readTimeout(REQUEST_READ_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_CONNECT_TIME_OUT, TimeUnit.SECONDS)
        if (DebugUtils.isDebuggable()) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            client.addInterceptor(logging)
        }
        return client.build()
    }

    fun getNetworkConfiguration(): APIService {
        return getInstance().create(APIService::class.java)
    }
}