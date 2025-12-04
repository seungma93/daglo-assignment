package com.seungma.daglo.data.datasource.character

import com.seungma.daglo.data.model.request.CharacterLoadRequest
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.PagedResponse

interface CharacterDataSource {
    suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse
    suspend fun loadCharacter(loadCharacterRequest: CharacterLoadRequest): CharacterResponse
}