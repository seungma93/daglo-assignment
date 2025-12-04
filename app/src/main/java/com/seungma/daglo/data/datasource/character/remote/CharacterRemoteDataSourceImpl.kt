package com.seungma.daglo.data.datasource.character.remote

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.model.request.CharacterLoadRequest
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import retrofit2.Retrofit
import javax.inject.Inject

class CharacterRemoteDataSourceImpl @Inject constructor(
    retrofit: Retrofit
) : CharacterDataSource {
    private val characterService = retrofit.create(RickAndMortyService::class.java)

    override suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse {

        return characterService.getCharacters(
            page = charactersLoadRequest.page,
            name = charactersLoadRequest.keyword,
            status = null,
            species = null,
            type = null,
            gender = null
        )
    }

    override suspend fun loadCharacter(loadCharacterRequest: CharacterLoadRequest): CharacterResponse {
        return characterService.getCharacter(
            id = loadCharacterRequest.id
        )
    }

}