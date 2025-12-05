package com.seungma.daglo.presenter.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seungma.daglo.data.CharacterResultEmptyException
import com.seungma.daglo.data.CharacterResultServerException
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
import javax.inject.Inject

sealed class CharacterInfoViewEvent {
    data class Error(val message: String) : CharacterInfoViewEvent()
}

data class CharacterInfoViewState(
    val character: CharacterEntity = CharacterEntity(
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

class CharacterInfoViewModel @Inject constructor(
    private val loadCharacterUseCase: LoadCharacterUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(CharacterInfoViewState())
    val viewState: StateFlow<CharacterInfoViewState> = _viewState.asStateFlow()

    private val _viewEvent = MutableSharedFlow<CharacterInfoViewEvent>()
    val viewEvent: SharedFlow<CharacterInfoViewEvent> = _viewEvent.asSharedFlow()


    fun loadCharacter(characterLoadForm: CharacterLoadForm) {
        viewModelScope.launch {
            runCatching {
                loadCharacterUseCase(characterLoadForm = characterLoadForm)
            }.onSuccess { character ->
                _viewState.update { current ->
                    current.copy(
                        character = character
                    )
                }
            }.onFailure {
                when (it) {
                    is CharacterResultEmptyException -> {
                        _viewEvent.emit(CharacterInfoViewEvent.Error(message = "검색 결과가 없습니다"))
                    }
                    is CharacterResultServerException -> {
                        _viewEvent.emit(CharacterInfoViewEvent.Error(message = "서버 에러가 발생했습니다"))
                    }
                    else -> {
                        _viewEvent.emit(CharacterInfoViewEvent.Error(message = "알 수 없는 에러가 발생했습니다"))
                    }
                }
            }
        }

    }

}
