package com.seungma.daglo.data.repository

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.mapper.toEntity
import com.seungma.daglo.data.model.request.CharacterLoadRequest
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import javax.inject.Inject

class CharacterDataRepositoryImpl @Inject constructor(
    private val characterDatasource: CharacterDataSource
) : CharacterDataRepository {

    override suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity {
        return characterDatasource.loadCharacters(
            charactersLoadRequest = CharactersLoadRequest(
                page = charactersLoadForm.page,
                keyword = charactersLoadForm.keyword.ifEmpty { null }
            )
        ).toEntity()
    }

    override suspend fun loadCharacter(characterLoadForm: CharacterLoadForm): CharacterEntity {
        return characterDatasource.loadCharacter(
            loadCharacterRequest = CharacterLoadRequest(
                id = characterLoadForm.id
            )
        ).toEntity()
    }
}