package com.seungma.daglo.network.retrofit

import com.seungma.daglo.data.HttpErrorException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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

        // 성공 응답이면 그대로 반환
        if (response.isSuccessful) {
            return response
        }

        // 에러 응답 body 읽기 (peekBody로 원본 body 유지)
        val errorBody = try {
            response.peekBody(Long.MAX_VALUE).string()
        } catch (e: IOException) {
            null
        }

        // HTTP 에러 코드에 따라 커스텀 예외 던지기
        when (response.code) {
            400 -> {
                // Bad Request - 잘못된 요청
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "잘못된 요청입니다.",
                    errorBody = errorBody
                )
            }
            401 -> {
                // Unauthorized - 인증 실패
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "인증에 실패했습니다.",
                    errorBody = errorBody
                )
            }
            402 -> {
                // Payment Required
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "결제가 필요합니다.",
                    errorBody = errorBody
                )
            }
            403 -> {
                // Forbidden - 권한 없음
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "권한이 없습니다.",
                    errorBody = errorBody
                )
            }
            404 -> {
                // Not Found - 검색 결과 없음 등
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "요청한 리소스를 찾을 수 없습니다.",
                    errorBody = errorBody
                )
            }
            500, 502, 503 -> {
                // Server Error - 서버 오류
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "서버 오류가 발생했습니다.",
                    errorBody = errorBody
                )
            }
            else -> {
                // 기타 에러
                throw HttpErrorException(
                    code = response.code,
                    errorMessage = "알 수 없는 오류가 발생했습니다.",
                    errorBody = errorBody
                )
            }
        }
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