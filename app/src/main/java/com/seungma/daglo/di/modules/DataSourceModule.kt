package com.seungma.daglo.di.modules

import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.datasource.character.remote.CharacterRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindCharacterDataSource(
        characterRemoteDataSourceImpl: CharacterRemoteDataSourceImpl
    ): CharacterDataSource
}
