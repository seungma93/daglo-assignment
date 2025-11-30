package com.seungma.daglo.presenter.list.viewmodel

import androidx.lifecycle.ViewModel
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.usecase.LoadCharactersUseCase
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CharacterViewModel (
    private val loadCharactersUseCase: LoadCharactersUseCase
) : ViewModel() {

    private val _viewState =
        MutableStateFlow(CharacterViewState(characters = emptyList()))
    val viewState: StateFlow<CharacterViewState> = _viewState.asStateFlow()

    data class CharacterViewState(
        val characters: List<CharacterEntity>
    )


    suspend fun loadCharacters(charactersLoadForm: CharactersLoadForm) {
        runCatching {
            val existingCharacters = viewState.value.characters
            val newCharacters = loadCharactersUseCase(charactersLoadForm = charactersLoadForm)

            val result = when (charactersLoadForm.reload) {
                true -> newCharacters.characters
                false -> existingCharacters + newCharacters.characters
            }

            _viewState.update { current ->
                current.copy(
                    characters = result
                )
            }

        }.onFailure {

        }.getOrNull()
    }

}