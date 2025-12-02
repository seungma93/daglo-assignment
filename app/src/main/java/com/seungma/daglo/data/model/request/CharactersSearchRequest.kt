package com.seungma.daglo.data.model.request

data class CharactersSearchRequest (
    val keyword: String,
    val reload: Boolean
)