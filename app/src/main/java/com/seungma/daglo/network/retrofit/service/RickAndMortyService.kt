package com.seungma.daglo.network.retrofit.service

import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): PagedResponse

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): CharacterResponse

    @GET("character/{ids}")
    suspend fun getCharactersByIds(
        @Path("ids") idsCsv: String
    ): List<CharacterResponse>
}