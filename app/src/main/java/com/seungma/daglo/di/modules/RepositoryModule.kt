package com.seungma.daglo.di.modules

import com.seungma.daglo.data.repository.CharacterDataRepositoryImpl
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterDataRepositoryImpl: CharacterDataRepositoryImpl
    ): CharacterDataRepository
}
