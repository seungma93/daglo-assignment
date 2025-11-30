package com.seungma.daglo.data.mapper

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
        } as List<CharacterEntity>
    )
}


fun CharacterResponse.toEntity(): CharacterEntity {
    val locationEntity = location?.toEntity() ?: LocationEntity(name = "", url = "")
    return CharacterEntity(
        id = id ?: throw Exception("id is null"),
        image = image.orEmpty(),
        name = name.orEmpty(),
        status = status.orEmpty(),
        gender = gender.orEmpty(),
        species = species.orEmpty(),
        orgin = locationEntity,
        location = locationEntity
    )
}

fun LocationResponse.toEntity(): LocationEntity{
    return LocationEntity(
        name = name.orEmpty(),
        url = url.orEmpty()
    )
}
