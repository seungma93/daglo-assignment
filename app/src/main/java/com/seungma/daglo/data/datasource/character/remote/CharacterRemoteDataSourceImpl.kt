package com.seungma.daglo.data.datasource.character.remote

import com.seungma.daglo.data.CharacterResultEmptyException
import com.seungma.daglo.data.CharacterResultServerException
import com.seungma.daglo.data.datasource.character.CharacterDataSource
import com.seungma.daglo.data.model.request.CharacterLoadRequest
import com.seungma.daglo.data.model.request.CharactersLoadRequest
import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.network.retrofit.service.RickAndMortyService
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

class CharacterRemoteDataSourceImpl @Inject constructor(
    retrofit: Retrofit
) : CharacterDataSource {
    private val characterService = retrofit.create(RickAndMortyService::class.java)

    override suspend fun loadCharacters(charactersLoadRequest: CharactersLoadRequest): PagedResponse {

        return runCatching {
            characterService.getCharacters(
                page = charactersLoadRequest.page,
                name = charactersLoadRequest.keyword,
                status = null,
                species = null,
                type = null,
                gender = null
            )
        }.getOrElse { e ->
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) throw CharacterResultEmptyException("캐릭터 검색 결과가 없습니다")
                    else throw CharacterResultServerException("캐릭터 검색 서버 에러가 발생했습니다")
                }

                else -> throw e
            }
        }
    }

    override suspend fun loadCharacter(loadCharacterRequest: CharacterLoadRequest): CharacterResponse {
        return runCatching {

            characterService.getCharacter(
                id = loadCharacterRequest.id
            )
        }.getOrElse { e ->
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) throw CharacterResultEmptyException("캐릭터 검색 결과가 없습니다")
                    else throw CharacterResultServerException("캐릭터 검색 서버 에러가 발생했습니다")
                }

                else -> throw e
            }
        }

    }

}