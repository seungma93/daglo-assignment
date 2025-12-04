package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import javax.inject.Inject

class LoadCharacterUseCase @Inject constructor(
    private val characterDataRepository: CharacterDataRepository
) {
    suspend operator fun invoke(characterLoadForm: CharacterLoadForm): CharacterEntity {
        return characterDataRepository.loadCharacter(characterLoadForm = characterLoadForm)
    }
}
