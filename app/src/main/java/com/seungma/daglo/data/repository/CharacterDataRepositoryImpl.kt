package com.seungma.daglo.data.repository

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.mapper.toLoadEntity
import com.seungma.daglo.data.mapper.toSearchEntity
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.request.CharactersSearchRequest
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.entity.CharactersSearchEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.form.CharactersSearchForm

class CharacterDataRepositoryImpl(private val characterDatasource: CharacterDataSource) :
    CharacterDataRepository {

    override suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity {
        return characterDatasource.loadCharacters(
            charactersLoadRequest = CharactersLoadRequest(
                reload = charactersLoadForm.reload
            )
        ).toLoadEntity()
    }

    override suspend fun searchCharacters(charactersSearchForm: CharactersSearchForm): CharactersSearchEntity {
        return characterDatasource.searchCharacters(
            charactersSearchRequest = CharactersSearchRequest(
                keyword = charactersSearchForm.keyword,
                reload = charactersSearchForm.reload
            )
        ).toSearchEntity()
    }

}