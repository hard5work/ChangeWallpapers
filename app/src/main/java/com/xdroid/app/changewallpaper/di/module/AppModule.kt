package com.xdroid.app.changewallpaper.di.module

import android.content.Context
import androidx.room.Room
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.xdroid.app.changewallpaper.App
import com.xdroid.app.changewallpaper.BuildConfig
import com.xdroid.app.changewallpaper.data.neworks.ApiHelper
import com.xdroid.app.changewallpaper.data.neworks.ApiHelperImpl
import com.xdroid.app.changewallpaper.data.neworks.ApiService
import com.xdroid.app.changewallpaper.data.room.AppDatabase
import com.xdroid.app.changewallpaper.utils.constants.NetworkError
import com.xdroid.app.changewallpaper.utils.constants.NetworkError.DATA_EXCEPTION
import com.xdroid.app.changewallpaper.utils.constants.NetworkError.IO_EXCEPTION
import com.xdroid.app.changewallpaper.utils.constants.NetworkError.SERVER_EXCEPTION
import com.xdroid.app.changewallpaper.utils.constants.PrefConstant
import com.xdroid.app.changewallpaper.utils.helpers.NetworkHelper
import com.xdroid.app.changewallpaper.utils.helpers.PreferenceHelper
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


val appModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), BuildConfig.BASE_URL) }
    single { provideApiService(get()) }
    single { provideNetworkHelper(androidContext()) }
    single { PreferenceHelper(androidContext()) }

    single<ApiHelper> {
        return@single ApiHelperImpl(get())
    }

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "note_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    single { get<AppDatabase>().favDao() }
    single { get<AppDatabase>().myItemsDao() }
}

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(loggingInterceptor)
        .build()
} else {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient
        .Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    BASE_URL: String
): Retrofit =
    Retrofit.Builder()
//        .addConverterFactory(MoshiConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

fun getErrorMessage(e: Throwable): String? {
    return when (e) {
        is JsonSyntaxException -> {
            val jObj = JsonObject()
            jObj.addProperty("title", "JsonSyntaxException")
            jObj.addProperty("status", false)
            jObj.addProperty("message", DATA_EXCEPTION)
            jObj.toString()

        }

        is HttpException -> {
            val responseBody = e.response()!!.errorBody()
            val code = e.response()!!.code().toString()
//            if (code != "401")
            getErrorMessage(responseBody!!)
            /* else {
                 getErrorMessages(responseBody!!)
             }*/
        }

        is SocketTimeoutException -> {
            val jObj = JsonObject()
            jObj.addProperty("title", "Socket Timeout")
            jObj.addProperty("status", false)
            jObj.addProperty("message", NetworkError.TIME_OUT)
            jObj.toString()
//            TIME_OUT
        }

        is IOException -> {
            val jObj = JsonObject()
            jObj.addProperty("title", "IO Error")
            jObj.addProperty("status", false)
            jObj.addProperty("message", IO_EXCEPTION)
            jObj.toString()
//            IO_EXCEPTION
        }

        else -> {
            val jObj = JsonObject()
            jObj.addProperty("title", "Server Error")
            jObj.addProperty("status", false)
            jObj.addProperty("message", SERVER_EXCEPTION)
            jObj.toString()
//            SERVER_EXCEPTION
        }
    }
}

private fun getErrorMessage(responseBody: ResponseBody): String? {
    return try {
        val jsonObject = JSONObject(responseBody.string())
//        jsonObject.getString("status")
//        jsonObject.getString("message")
        val jObj = JsonObject()
        jObj.addProperty("status", jsonObject.optString("status",""))
        jObj.addProperty("message", jsonObject.optString("message", jsonObject.toString()))
        jObj.toString()
    } catch (e: Exception) {
        e.message
        val jObj = JsonObject()
        jObj.addProperty("status", "")
        jObj.addProperty("message", e.message.toString())
        jObj.toString()
    }
}

private fun getErrorMessages(response: ResponseBody): String? {
    return try {
        // Extract server message from response body
        val serverMessage = response.string()
        // You can then handle or display the server message as needed
        // For example, logging it or showing an error message to the user
        println("Server message: $serverMessage")
        val jsonObject = JSONObject(response.string())
        jsonObject.getString("title")
        jsonObject.getString("message")
        val jObj = JsonObject()
        jObj.addProperty("title", jsonObject.getString("title"))
        jObj.addProperty("message", jsonObject.getString("message"))
        jObj.toString()
        return serverMessage

    } catch (e: Exception) {
        e.message
    }
}
