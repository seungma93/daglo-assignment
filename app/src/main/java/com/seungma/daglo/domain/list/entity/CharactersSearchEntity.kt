package com.seungma.daglo.domain.list.entity

data class CharactersSearchEntity (
    val characters: List<CharacterEntity>,
    val isLast: Boolean
)