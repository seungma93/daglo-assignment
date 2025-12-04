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

sealed class CharacterViewEvent{
    data class Error(val message: String): CharacterViewEvent()
    data class Scroll(val scrollToTop: Boolean): CharacterViewEvent()
}

class CharacterViewModel @Inject constructor(
    private val loadCharactersUseCase: LoadCharactersUseCase
) : ViewModel() {

    private val _viewState =
        MutableStateFlow(
            CharacterViewState(
                characters = emptyList(),
                nextPage = null,
                isLoading = false,
                keyword = ""
            )
        )
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
                loadCharacters(
                    charactersLoadForm = CharactersLoadForm(
                        page = viewState.value.nextPage ?: 1, keyword = it
                    )
                )
            }
        }
    }

    data class CharacterViewState(
        val characters: List<CharacterEntity>,
        val nextPage: Int?,
        val isLoading: Boolean,
        val keyword: String,
    )

    fun sendQuery(query: String) {
        viewModelScope.launch {
            producer.send(query)
        }
    }

    fun loadCharacters(charactersLoadForm: CharactersLoadForm) {
        if(viewState.value.isLoading) return

        viewModelScope.launch {

            _viewState.update { it.copy(isLoading = true) }

            when (viewState.value.keyword == charactersLoadForm.keyword) {

                true -> {
                    runCatching {
                        val charactersLoadEntity =
                            loadCharactersUseCase(charactersLoadForm = charactersLoadForm)

                        val characters = charactersLoadEntity.characters
                        val nextPage = parseNextPage(nextUrl = charactersLoadEntity.nextPage)

                        _viewState.update { current ->
                            current.copy(
                                characters = if (charactersLoadForm.page == 1) characters else current.characters + characters,
                                nextPage = nextPage,
                                isLoading = false,
                                keyword = charactersLoadForm.keyword
                            )
                        }

                        if(charactersLoadForm.page == 1) {
                            _viewEvent.emit(CharacterViewEvent.Scroll(scrollToTop = true))
                        }

                    }.onFailure {
                        _viewState.update { it.copy(isLoading = false) }
                    }
                }

                false -> {
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
                        _viewState.update { it.copy(isLoading = false) }
                        when(it) {
                            is HttpException -> {
                                when(it.code()) {
                                    404 -> _viewEvent.emit(CharacterViewEvent.Error(message = "검색 결과가 없습니다"))
                                }
                            }
                        }
                    }

                }

            }

        }
    }

    private fun parseNextPage(nextUrl: String?): Int? {
        return nextUrl?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        }
    }
}
