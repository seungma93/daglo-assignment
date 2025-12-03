package com.seungma.daglo.presenter.list.viewmodel

import androidx.lifecycle.ViewModel
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.usecase.LoadCharactersUseCase
import com.seungma.daglo.domain.list.usecase.SearchCharactersUseCase
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import com.seungma.daglo.presenter.list.form.CharactersSearchForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CharacterViewModel (
    private val loadCharactersUseCase: LoadCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase
) : ViewModel() {

    private val _viewState =
        MutableStateFlow(CharacterViewState(characters = emptyList(), isLast = false, isLoading = false, keyword = null, errorMessage = null))
    val viewState: StateFlow<CharacterViewState> = _viewState.asStateFlow()

    data class CharacterViewState(
        val characters: List<CharacterEntity>,
        val isLast: Boolean,
        val isLoading: Boolean,
        val keyword: String?,
        val errorMessage: String?
    )


    suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm) {
        // 이미 로딩 중이면 중복 호출 방지
        if (_viewState.value.isLoading) return
        
        _viewState.update { current ->
            current.copy(isLoading = true, keyword = null, errorMessage = null)
        }
        
        runCatching {
            val existingCharacters = viewState.value.characters
            val newCharacters = loadCharactersUseCase(charactersLoadForm = charactersLoadForm)

            val result = when (charactersLoadForm.reload) {
                true -> newCharacters.characters
                false -> existingCharacters + newCharacters.characters
            }

            _viewState.update { current ->
                current.copy(
                    characters = result,
                    isLast = newCharacters.isLast,
                    isLoading = false,
                    keyword = null,
                    errorMessage = null
                )
            }

        }.onFailure {
            _viewState.update { current ->
                current.copy(isLoading = false, keyword = null, errorMessage = it.message)
            }
        }.getOrNull()
    }

    suspend fun searchCharacters(charactersSearchForm: CharactersSearchForm) {

        // 이미 로딩 중이면 중복 호출 방지
        if (_viewState.value.isLoading) return

        _viewState.update { current ->
            current.copy(isLoading = true, keyword = charactersSearchForm.keyword, errorMessage = null)
        }

        runCatching {
            val existingCharacters = viewState.value.characters
            val newCharacters = searchCharactersUseCase(charactersSearchForm = charactersSearchForm)

            val result = when (charactersSearchForm.reload) {
                true -> newCharacters.characters
                false -> existingCharacters + newCharacters.characters
            }

            _viewState.update { current ->
                current.copy(
                    characters = result,
                    isLast = newCharacters.isLast,
                    isLoading = false,
                    keyword = charactersSearchForm.keyword,
                    errorMessage = null
                )
            }

        }.onFailure {
            _viewState.update { current ->
                current.copy(isLoading = false, keyword = charactersSearchForm.keyword, errorMessage = it.message)
            }
        }.getOrNull()
    }

    fun clearViewState() {
        _viewState.update {
            CharacterViewState(
                characters = emptyList(), isLast = false, isLoading = false, keyword = null, errorMessage = null)
        }
    }


}