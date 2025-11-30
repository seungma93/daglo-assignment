package com.seungma.daglo.data.datasource.character

import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.CharactersLoadResponse

interface CharacterDataSource {
    fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): CharactersLoadResponse
}