package com.seungma.daglo.data.repository

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.mapper.toEntity
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharactersLoadForm

class CharacterDataRepositoryImpl(private val characterDatasource: CharacterDataSource) :
    CharacterDataRepository {

    override suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity {
        return characterDatasource.loadCharacters(
            charactersLoadRequest = CharactersLoadRequest(
                reload = charactersLoadForm.reload
            )
        ).toEntity()
    }

}