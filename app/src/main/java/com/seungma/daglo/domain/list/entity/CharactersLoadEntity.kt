package com.seungma.daglo.domain.list.entity

data class CharactersLoadEntity (
    val characters: List<CharacterEntity>,
    val isLast: Boolean
)