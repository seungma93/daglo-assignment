package com.seungma.daglo.presenter.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.entity.LocationEntity
import com.seungma.daglo.domain.list.usecase.LoadCharacterUseCase
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class CharacterInfoViewEvent{
    data class Error(private val message: String): CharacterInfoViewEvent()
}

class CharacterInfoViewModel(
    private val loadCharacterUseCase: LoadCharacterUseCase
) : ViewModel() {

    data class CharacterInfoViewState(
        val character: CharacterEntity?
    )

    private val _viewState =
        MutableStateFlow(
            CharacterInfoViewState(
                character = CharacterEntity(
                    id = 0,
                    image = "",
                    name = "",
                    status = "",
                    gender = "",
                    species = "",
                    orgin = LocationEntity(name = "", url = ""),
                    location = LocationEntity(name = "", url = "")
                )
            )
        )
    val viewState: StateFlow<CharacterInfoViewState> = _viewState.asStateFlow()

    private val _viewEvent = MutableSharedFlow<CharacterInfoViewEvent>()
    private val viewEvent: SharedFlow<CharacterInfoViewEvent> = _viewEvent.asSharedFlow()



    fun loadCharacter(characterLoadForm: CharacterLoadForm) {
        viewModelScope.launch {
            runCatching {
                val character = loadCharacterUseCase(characterLoadForm = characterLoadForm)

                _viewState.update {
                    it.copy(
                        character = character
                    )
                }

            }.onFailure {
                when(it) {
                    is HttpException -> {
                        when(it.code()) {
                            404 -> _viewEvent.emit(CharacterInfoViewEvent.Error(message = "캐릭터 상세결과가 없습니다"))
                        }
                    }
                }
            }
        }

    }

}
