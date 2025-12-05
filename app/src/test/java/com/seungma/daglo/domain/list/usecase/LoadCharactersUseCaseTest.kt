package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.data.datasource.character.remote.CharacterRemoteDataSourceImpl
import com.seungma.daglo.data.repository.CharacterDataRepositoryImpl
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.entity.LocationEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import com.seungma.daglo.presenter.list.form.CharactersLoadForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

class LoadCharactersUseCaseTest {

    private lateinit var repository: CharacterDataRepository
    private lateinit var useCase: LoadCharactersUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = LoadCharactersUseCase(repository)
    }

    @Test
    fun `loadCharactersUseCase 성공 케이스`() = runTest {
        //Given
        val form = CharactersLoadForm(
            page = 1, keyword = ""
        )
        val expectedEntity = CharactersLoadEntity(
            characters = listOf(CharacterEntity(
                id = 1,
                image = "",
                name = "test",
                status = "",
                gender = "",
                species = "",
                orgin = LocationEntity(name = "", url = ""),
                location = LocationEntity(name = "", url = "")
            )), nextPage = 3, prevPage = 1

        )

        coEvery {
            repository.loadCharacters(charactersLoadForm = any())
        } returns expectedEntity

        // When
        val result = repository.loadCharacters(charactersLoadForm = form)

        // Then
        assertEquals(expectedEntity, result)
        assertEquals(expectedEntity.characters, result.characters)
        assertEquals(expectedEntity.characters.size, result.characters.size)

        coVerify(exactly = 1) { repository.loadCharacters(charactersLoadForm = form) }
    }

}
