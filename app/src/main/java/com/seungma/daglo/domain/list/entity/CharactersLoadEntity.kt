package com.seungma.daglo.domain.list.entity

data class CharactersLoadEntity (
    val reload: Boolean,
    val characters: List<CharacterEntity>
)