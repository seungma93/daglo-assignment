package com.seungma.daglo.data.datasource.character.remote

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import androidx.core.net.toUri
import com.seungma.daglo.data.model.request.CharactersSearchRequest
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRemoteDataSourceImpl @Inject constructor(
    retrofit: Retrofit
) : CharacterDataSource {
    private val characterService = retrofit.create(RickAndMortyService::class.java)
    private var loadIndex: Int? = null
    private var searchIndex: Int? = null

    override suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse {

        searchIndex?.let {
            searchIndex = null
        }

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

    override suspend fun searchCharacters(charactersSearchRequest: CharactersSearchRequest): PagedResponse {

        loadIndex?.let {
            loadIndex = null
        }

        val response = characterService.getCharacters(
            page = when (charactersSearchRequest.reload) {
                true -> null
                false -> searchIndex
            },
            name = charactersSearchRequest.keyword,
            status = null,
            species = null,
            type = null,
            gender = null
        )

        searchIndex = parseNextPage(nextUrl = response.info?.next)

        return  response
    }

    private fun parseNextPage(nextUrl: String?): Int? {
        return nextUrl?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        }
    }
}