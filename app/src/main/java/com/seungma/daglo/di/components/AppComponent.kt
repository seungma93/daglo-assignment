package com.seungma.daglo.di.components

import com.seungma.daglo.di.modules.DataSourceModule
import com.seungma.daglo.di.modules.NetworkModule
import com.seungma.daglo.di.modules.RepositoryModule
import com.seungma.daglo.di.modules.ViewModelModule
import com.seungma.daglo.presenter.list.fragment.CharacterListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DataSourceModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    fun inject(fragment: CharacterListFragment)
}