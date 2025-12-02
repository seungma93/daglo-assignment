package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.data.SearchCharactersException
import com.seungma.daglo.domain.list.entity.CharactersSearchEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharactersSearchForm

class SearchCharactersUseCase(private val characterDataRepository: CharacterDataRepository) {
    suspend operator fun invoke(charactersSearchForm: CharactersSearchForm): CharactersSearchEntity {
        return runCatching {
            characterDataRepository.searchCharacters(charactersSearchForm = charactersSearchForm)
        }.onFailure {
            throw SearchCharactersException(_message = "캐릭터 검색 실패")
        }.getOrThrow()
    }
}
