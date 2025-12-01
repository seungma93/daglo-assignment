package com.seungma.daglo.data.datasource.character.remote

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.RetrofitClient
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import androidx.core.net.toUri

class CharacterRemoteDataSourceImpl(private val retrofitClient: RetrofitClient) :
    CharacterDataSource {
    private val characterService = retrofitClient.retrofit.create(RickAndMortyService::class.java)
    private var loadIndex: Int? = null

    override suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse {

        val response = characterService.getCharacters(
            page = when (charactersLoadRequest.reload) {
                true -> null
                false -> loadIndex
            },
            name = null,
            status = null,
            species = null,
            type = null,
            gender = null
        )

        loadIndex = parseNextPage(nextUrl = response.info?.next)

        return response
    }

    private fun parseNextPage(nextUrl: String?): Int? {
        return nextUrl?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        }
    }
}