package com.seungma.daglo.di.modules

import androidx.lifecycle.ViewModel
import com.seungma.daglo.domain.list.usecase.LoadCharacterUseCase
import com.seungma.daglo.presenter.list.viewmodel.CharacterInfoViewModel
import com.seungma.daglo.presenter.list.viewmodel.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class CharacterInfoViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(CharacterInfoViewModel::class)
    fun provideCharacterInfoViewModel(
        loadCharacterUseCase: LoadCharacterUseCase
    ): ViewModel {
        return CharacterInfoViewModel(
            loadCharacterUseCase = loadCharacterUseCase
        )
    }
}
