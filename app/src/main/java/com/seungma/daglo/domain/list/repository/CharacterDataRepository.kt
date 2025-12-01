package com.seungma.daglo.domain.list.repository

import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.entity.CharactersSearchEntity
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.form.CharactersSearchForm

interface CharacterDataRepository {

    suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity
    suspend fun searchCharacters(charactersSearchForm: CharactersSearchForm): CharactersSearchEntity
}