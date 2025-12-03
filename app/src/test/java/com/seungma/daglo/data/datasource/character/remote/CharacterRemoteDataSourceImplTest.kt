package com.seungma.daglo.data.datasource.character.remote

import com.seungma.daglo.data.HttpErrorException
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.request.CharactersSearchRequest
import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.InfoResponse
import com.seungma.daglo.data.model.response.LocationResponse
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * CharacterRemoteDataSourceImpl 테스트
 * 
 * 테스트 케이스:
 * 1. loadCharacters 성공 케이스
 * 2. searchCharacters 성공 케이스
 * 3. HttpErrorException 처리
 * 4. 페이지 인덱스 관리
 * 5. parseNextPage 로직
 */
class CharacterRemoteDataSourceImplTest {

    private lateinit var retrofit: Retrofit
    private lateinit var service: RickAndMortyService
    private lateinit var dataSource: CharacterRemoteDataSourceImpl

    @Before
    fun setup() {
        retrofit = mockk()
        service = mockk()
        every { retrofit.create(RickAndMortyService::class.java) } returns service
        dataSource = CharacterRemoteDataSourceImpl(retrofit)
    }

    @Test
    fun `loadCharacters 성공 케이스 - reload true일 때`() = runTest {
        // Given
        val request = CharactersLoadRequest(reload = true)
        val expectedResponse = createMockPagedResponse(
            characters = listOf(
                createMockCharacterResponse(id = 1, name = "Rick"),
                createMockCharacterResponse(id = 2, name = "Morty")
            )
        )

        coEvery {
            service.getCharacters(
                page = null,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        } returns expectedResponse

        // When
        val result = dataSource.loadCharacters(request)

        // Then
        assertEquals(expectedResponse, result)
        assertEquals(2, result.results?.size)
        assertEquals("Rick", result.results?.get(0)?.name)

        // Verify
        coVerify(exactly = 1) {
            service.getCharacters(
                page = null,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        }
    }

    @Test
    fun `searchCharacters 성공 케이스`() = runTest {
        // Given
        val request = CharactersSearchRequest(keyword = "Rick", reload = true)
        val expectedResponse = createMockPagedResponse(
            characters = listOf(
                createMockCharacterResponse(id = 1, name = "Rick Sanchez")
            ),
            nextUrl = null
        )

        coEvery {
            service.getCharacters(
                page = null,
                name = "Rick",
                status = null,
                species = null,
                type = null,
                gender = null
            )
        } returns expectedResponse

        // When
        val result = dataSource.searchCharacters(request)

        // Then
        assertEquals(expectedResponse, result)
        assertEquals(1, result.results?.size)
        assertEquals("Rick Sanchez", result.results?.get(0)?.name)
        assertNull(result.info?.next)

        coVerify(exactly = 1) {
            service.getCharacters(
                page = null,
                name = "Rick",
                status = null,
                species = null,
                type = null,
                gender = null
            )
        }
    }

    @Test
    fun `loadCharacters 실패 케이스 - HttpErrorException 발생`() = runTest {
        // Given
        val request = CharactersLoadRequest(reload = true)
        val httpException = HttpErrorException(
            code = 404,
            errorMessage = "요청한 리소스를 찾을 수 없습니다.",
            errorBody = null
        )

        coEvery {
            service.getCharacters(
                page = null,
                name = null,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        } throws httpException

        // When & Then
        try {
            dataSource.loadCharacters(request)
            fail("예외가 발생해야 합니다")
        } catch (e: HttpErrorException) {
            assertEquals(404, e.code)
            assertEquals("요청한 리소스를 찾을 수 없습니다.", e.errorMessage)
        }
    }

    @Test
    fun `searchCharacters 실패 케이스 - HttpErrorException 발생`() = runTest {
        // Given
        val request = CharactersSearchRequest(keyword = "NonExistent", reload = true)
        val httpException = HttpErrorException(
            code = 404,
            errorMessage = "요청한 리소스를 찾을 수 없습니다.",
            errorBody = null
        )

        coEvery {
            service.getCharacters(
                page = null,
                name = "NonExistent",
                status = null,
                species = null,
                type = null,
                gender = null
            )
        } throws httpException

        // When & Then
        try {
            dataSource.searchCharacters(request)
            fail("예외가 발생해야 합니다")
        } catch (e: HttpErrorException) {
            assertEquals(404, e.code)
        }
    }



    private fun createMockPagedResponse(
        characters: List<CharacterResponse> = emptyList(),
        nextUrl: String? = null
    ): PagedResponse {
        val info = InfoResponse(
            count = characters.size,
            pages = 1,
            next = nextUrl,
            prev = null
        )
        return PagedResponse(
            info = info,
            results = characters
        )
    }

    private fun createMockCharacterResponse(
        id: Int = 1,
        name: String = "Test Character"
    ): CharacterResponse {
        val location = LocationResponse(name = "Earth", url = "https://rickandmortyapi.com/api/location/1")
        return CharacterResponse(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = null,
            gender = "Male",
            origin = location,
            location = location,
            image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            episode = emptyList(),
            url = "https://rickandmortyapi.com/api/character/$id",
            created = null
        )
    }
}

