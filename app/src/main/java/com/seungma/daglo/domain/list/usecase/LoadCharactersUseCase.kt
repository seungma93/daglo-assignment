package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.data.LoadCharactersException
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadCharactersUseCase @Inject constructor(
    private val characterDataRepository: CharacterDataRepository
) {
    suspend operator fun invoke(charactersLoadForm: CharactersLoadForm): CharactersLoadEntity {
        return runCatching {
            characterDataRepository.loadCharacters(charactersLoadForm = charactersLoadForm)
        }.onFailure {
            throw LoadCharactersException(_message = "캐릭터 로드 실패")
        }.getOrThrow()
    }
}
