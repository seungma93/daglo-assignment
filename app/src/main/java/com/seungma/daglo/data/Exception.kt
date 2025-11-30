package com.seungma.daglo.data


class TestException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class LoadCharactersException(
    val _message: String
) : Exception(_message)



