package com.seungma.daglo.presenter.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seungma.daglo.data.CharacterResultEmptyException
import com.seungma.daglo.data.CharacterResultServerException
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.usecase.LoadCharactersUseCase
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CharacterViewEvent {
    data class Error(val message: String) : CharacterViewEvent()
}

data class CharacterViewState(
    val characters: List<CharacterEntity> = emptyList(),
    val nextPage: Int? = null,
    val prevPage: Int? = null,
    val isLoading: Boolean = false,
    val keyword: String = ""
) {
    val isFirstPage = prevPage == null
}

class CharacterViewModel @Inject constructor(
    private val loadCharactersUseCase: LoadCharactersUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(CharacterViewState())
    val viewState: StateFlow<CharacterViewState> = _viewState.asStateFlow()

    private val _viewEvent = MutableSharedFlow<CharacterViewEvent>()
    val viewEvent: SharedFlow<CharacterViewEvent> = _viewEvent.asSharedFlow()

    private val producer = Channel<String>()
    private val consumer = producer
        .consumeAsFlow()
        .distinctUntilChanged()
        .debounce(400)

    init {
        viewModelScope.launch {
            consumer.collect {
                fetch(query = it)
            }
        }
    }

    fun sendQuery(query: String) {
        viewModelScope.launch {
            producer.send(query)
        }
    }

    fun fetch(query: String) {
        if (viewState.value.isLoading) return
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                loadCharactersUseCase(
                    charactersLoadForm = CharactersLoadForm(
                        page = null,
                        keyword = query
                    )
                )
            }.onSuccess {
                val characters = it.characters
                val prevPage = it.prevPage
                val nextPage = it.nextPage
                Log.d("seungma", "패치")
                _viewState.update { current ->
                    current.copy(
                        characters = characters,
                        prevPage = prevPage,
                        nextPage = nextPage,
                        keyword = query
                    )
                }
            }.onFailure {
                when (it) {
                    is CharacterResultEmptyException -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "검색 결과가 없습니다"))
                    }
                    is CharacterResultServerException -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "서버 에러가 발생했습니다"))
                    }
                    else -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "알 수 없는 에러가 발생했습니다"))
                    }
                }
            }

        }.invokeOnCompletion {
            _viewState.update { it.copy(isLoading = false) }
        }
    }

    fun loadMore(charactersLoadForm: CharactersLoadForm) {
        if (viewState.value.isLoading) return
        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                loadCharactersUseCase(charactersLoadForm = charactersLoadForm)
            }.onSuccess {
                val characters = it.characters
                val prevPage = it.prevPage
                val nextPage = it.nextPage
                Log.d("seungma", "로드모어")
                _viewState.update { current ->
                    current.copy(
                        characters = (current.characters + characters)
                            .toSet()
                            .toList(),
                        prevPage = prevPage,
                        nextPage = nextPage,
                        keyword = charactersLoadForm.keyword
                    )
                }
            }.onFailure {
                when (it) {
                    is CharacterResultEmptyException -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "검색 결과가 없습니다"))
                    }
                    is CharacterResultServerException -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "서버 에러가 발생했습니다"))
                    }
                    else -> {
                        _viewEvent.emit(CharacterViewEvent.Error(message = "알 수 없는 에러가 발생했습니다"))
                    }
                }
            }
        }.invokeOnCompletion {
            _viewState.update { it.copy(isLoading = false) }
        }
    }
}
