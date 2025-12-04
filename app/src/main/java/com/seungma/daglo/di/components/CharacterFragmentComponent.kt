package com.seungma.daglo.di.components

import com.seungma.daglo.di.modules.CharacterInfoViewModelModule
import com.seungma.daglo.di.modules.DataSourceModule
import com.seungma.daglo.di.modules.NetworkModule
import com.seungma.daglo.di.modules.RepositoryModule
import com.seungma.daglo.presenter.list.fragment.CharacterFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DataSourceModule::class,
        RepositoryModule::class,
        CharacterInfoViewModelModule::class
    ]
)
interface CharacterFragmentComponent {
    fun inject(fragment: CharacterFragment)
}