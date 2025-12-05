package com.seungma.daglo.data

class CharacterResultEmptyException(
    val _message: String
) : Exception(_message)

class CharacterResultServerException(
    val _message: String
) : Exception(_message)