package com.seungma.daglo.data.datasource.character

import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.request.CharactersSearchRequest
import com.seungma.daglo.data.model.response.PagedResponse

interface CharacterDataSource {
    suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse
}