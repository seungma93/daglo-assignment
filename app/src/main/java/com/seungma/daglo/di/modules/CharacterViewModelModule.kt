package com.seungma.daglo.di.modules

import androidx.lifecycle.ViewModel
import com.seungma.daglo.domain.list.usecase.LoadCharactersUseCase
import com.seungma.daglo.presenter.list.viewmodel.CharacterViewModel
import com.seungma.daglo.presenter.list.viewmodel.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class CharacterViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(CharacterViewModel::class)
    fun provideCharacterViewModel(
        loadCharactersUseCase: LoadCharactersUseCase
    ): ViewModel {
        return CharacterViewModel(
            loadCharactersUseCase = loadCharactersUseCase
        )
    }
}
