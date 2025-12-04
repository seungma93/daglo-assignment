package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import javax.inject.Inject

class LoadCharactersUseCase @Inject constructor(
    private val characterDataRepository: CharacterDataRepository
) {
    suspend operator fun invoke(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity {
        return characterDataRepository.loadCharacters(charactersLoadForm = charactersLoadForm)
    }
}
