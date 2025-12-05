package com.seungma.daglo.data.mapper

import androidx.core.net.toUri
import com.seungma.daglo.data.model.response.CharacterResponse
import com.seungma.daglo.data.model.response.LocationResponse
import com.seungma.daglo.data.model.response.PagedResponse
import com.seungma.daglo.domain.list.entity.CharacterEntity
import com.seungma.daglo.domain.list.entity.CharactersLoadEntity
import com.seungma.daglo.domain.list.entity.LocationEntity


fun PagedResponse.toEntity(): CharactersLoadEntity {
    return CharactersLoadEntity(
        characters = results?.map {
            it.toEntity()
        } ?: emptyList(),
        prevPage = info?.prev?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        },
        nextPage = info?.next?.let {
            it.toUri().getQueryParameter("page")?.toInt()
        }
    )
}

fun CharacterResponse.toEntity(): CharacterEntity {
    val origin = origin?.toEntity() ?: LocationEntity(name = "", url = "")
    val location = location?.toEntity() ?: LocationEntity(name = "", url = "")
    return CharacterEntity(
        id = id ?: throw Exception("id is null"),
        image = image.orEmpty(),
        name = name.orEmpty(),
        status = status.orEmpty(),
        gender = gender.orEmpty(),
        species = species.orEmpty(),
        orgin = origin,
        location = location
    )
}

fun LocationResponse.toEntity(): LocationEntity{
    return LocationEntity(
        name = name.orEmpty(),
        url = url.orEmpty()
    )
}
