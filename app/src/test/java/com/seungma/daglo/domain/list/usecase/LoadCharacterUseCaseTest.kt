package com.seungma.daglo.domain.list.usecase

import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.entity.LocationEntity
import com.seungma.daglo.domain.list.repository.CharacterDataRepository
import com.seungma.daglo.presenter.list.form.CharacterLoadForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoadCharacterUseCaseTest {

    private lateinit var repository: CharacterDataRepository
    private lateinit var useCase: LoadCharacterUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = LoadCharacterUseCase(characterDataRepository = repository)
    }

    @Test
    fun `loadCharacterUseCase 성공 케이스`() = runTest {

        //given
        val form = CharacterLoadForm(id = 1)
        val characterEntity = CharacterEntity(
            id = 1,
            image = "",
            name = "1",
            status = "",
            gender = "",
            species = "",
            orgin = LocationEntity(name = "", url = ""),
            location = LocationEntity(name = "", url = "")
        )

        coEvery{
            repository.loadCharacter(characterLoadForm = any())
        } returns characterEntity


        //when
        val result = useCase(characterLoadForm = form)

        //then
        assertEquals(characterEntity, result)
        assertEquals(characterEntity.name, result.name)


        coVerify(exactly = 1) {
            repository.loadCharacter(characterLoadForm = form)
        }

    }


}