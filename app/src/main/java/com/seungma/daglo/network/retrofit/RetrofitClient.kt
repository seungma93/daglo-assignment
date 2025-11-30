package com.seungma.daglo.network.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private const val BASE_URL = "https://rickandmortyapi.com/api/"
    private const val RETROFIT_TIMEOUT_NEW = 15.toLong()

    private val interceptorClient = OkHttpClient().newBuilder()
        .addInterceptor(RequestInterceptor())
        .addInterceptor(ResponseInterceptor())
        .addInterceptor(CurlLoggingInterceptor())
        .connectTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS)
        .readTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS)
        .writeTimeout(RETROFIT_TIMEOUT_NEW, TimeUnit.SECONDS).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(interceptorClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}

class RequestInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        return chain.proceed(builder.build())
    }
}

class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            200 -> {

            }

            400 -> {

            }

            401 -> {

            }

            402 -> {

            }

            403 -> {

            }
            500 -> {

            }
            else -> {

            }
        }
        return response
    }
}

class CurlLoggingInterceptor : Interceptor {

    companion object {
        private val UTF8 = StandardCharsets.UTF_8
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        val curlCommand = StringBuilder()
        curlCommand.append("curl -X ${request.method} ")

        // Append headers
        for ((name, value) in request.headers) {
            curlCommand.append("-H \"$name: $value\" ")
        }

        // Append request body
        requestBody?.let {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val charset: Charset = requestBody.contentType()?.charset(UTF8) ?: UTF8
            curlCommand.append("--data '").append(buffer.readString(charset)).append("' ")
        }

        // Append URL
        curlCommand.append("\"${request.url}\"")

        return chain.proceed(request)
    }
}