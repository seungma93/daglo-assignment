package com.seungma.daglo.domain.list.repository

import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.presenter.list.form.CharactersLoadForm

interface CharacterDataRepository {

    suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity
}