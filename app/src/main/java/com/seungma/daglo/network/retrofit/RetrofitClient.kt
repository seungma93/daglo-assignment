package com.seungma.daglo.network.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    private const val RETROFIT_TIMEOUT_NEW = 15.toLong()

    private val okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS)
        .readTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS)
        .writeTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}
