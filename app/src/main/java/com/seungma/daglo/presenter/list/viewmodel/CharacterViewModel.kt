package com.seungma.daglo.presenter.list.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import retrofit2.HttpException
import javax.inject.Inject

sealed class CharacterViewEvent {
    data class Error(val message: String) : CharacterViewEvent()
    data class Scroll(val scrollToTop: Boolean) : CharacterViewEvent()
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
                loadCharactersLoadFetch(
                    charactersLoadForm = CharactersLoadForm(
                        page = viewState.value.nextPage ?: 1, keyword = it
                    )
                )
            }
        }
    }

    data class CharacterViewState(
        val characters: List<CharacterEntity> = emptyList(),
        val nextPage: Int? = null,
        val isLoading: Boolean = false,
        val keyword: String = "",
    )

    fun sendQuery(query: String) {
        viewModelScope.launch {
            producer.send(query)
        }
    }

    fun loadCharactersLoadFetch(charactersLoadForm: CharactersLoadForm) {
        if (viewState.value.isLoading) return

        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                val charactersLoadEntity =
                    loadCharactersUseCase(charactersLoadForm = charactersLoadForm.copy(page = 1))

                val characters = charactersLoadEntity.characters
                val nextPage = parseNextPage(nextUrl = charactersLoadEntity.nextPage)

                _viewState.update { current ->
                    current.copy(
                        characters = characters,
                        nextPage = nextPage,
                        isLoading = false,
                        keyword = charactersLoadForm.keyword
                    )
                }
                _viewEvent.emit(CharacterViewEvent.Scroll(scrollToTop = true))
            }.onFailure {
                when (it) {
                    is HttpException -> {
                        when (it.code()) {
                            404 -> _viewEvent.emit(CharacterViewEvent.Error(message = "검색 결과가 없습니다"))
                            else -> _viewEvent.emit(CharacterViewEvent.Error(message = "서버 에러가 발생 했습니다"))
                        }
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

    fun loadCharactersLoadMore(charactersLoadForm: CharactersLoadForm) {

        if (viewState.value.isLoading) return

        _viewState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            runCatching {
                val charactersLoadEntity =
                    loadCharactersUseCase(charactersLoadForm = charactersLoadForm)

                val characters = charactersLoadEntity.characters
                val nextPage = parseNextPage(nextUrl = charactersLoadEntity.nextPage)

                _viewState.update { current ->
                    current.copy(
                        characters = current.characters + characters,
                        nextPage = nextPage,
                        isLoading = false,
                        keyword = charactersLoadForm.keyword
                    )
                }

                if (charactersLoadForm.page == 1) {
                    _viewEvent.emit(CharacterViewEvent.Scroll(scrollToTop = true))
                }

            }.onFailure {
                when (it) {
                    is HttpException -> {
                        when (it.code()) {
                            404 -> _viewEvent.emit(CharacterViewEvent.Error(message = "검색 결과가 없습니다"))
                            else -> _viewEvent.emit(CharacterViewEvent.Error(message = "서버 에러가 발생 했습니다"))
                        }
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


    private fun parseNextPage(nextUrl: String?): Int? {
        return nextUrl?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        }
    }
}
