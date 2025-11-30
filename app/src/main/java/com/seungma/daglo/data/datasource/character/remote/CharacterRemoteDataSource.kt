package com.seungma.daglo.data.datasource.character.remote

import android.net.Uri
import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.RetrofitClient
import com.seungma.daglo.network.retrofit.service.RickAndMortyService

class CharacterRemoteDataSource(private val retrofitClient: RetrofitClient) : CharacterDataSource {
    private val characterService = retrofitClient.retrofit.create(RickAndMortyService::class.java)
    private var loadIndex: Int? = null

    override suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse {

        return when (charactersLoadRequest.reload) {
            true -> {
                val response = characterService.getCharacters(
                    page = null,
                    name = null,
                    status = null,
                    species = null,
                    type = null,
                    gender = null
                )
                // 인덱스 처리
                loadIndex = parseNextPage(nextUrl = response.info?.next)

                // 리턴
                response
            }

            false -> {

                val response = characterService.getCharacters(
                    page = loadIndex,
                    name = null,
                    status = null,
                    species = null,
                    type = null,
                    gender = null
                )

                loadIndex?.let {
                    // 인덱스 처리
                    loadIndex = parseNextPage(nextUrl = response.info?.next)

                    // 리턴
                    response

                } ?: run {
                    response
                }
            }
        }
    }

    private fun parseNextPage(nextUrl: String?): Int? {
        return nextUrl?.let {
            Uri.parse(it).getQueryParameter("page")?.toInt()
        }
    }
}