package com.seungma.daglo.data


class TestException(
    private val _message: String,
    throwable: Throwable?
) : Exception(throwable?.message, throwable)

class LoadCharactersException(
    val _message: String
) : Exception(_message)

class SearchCharactersException(
    val _message: String
) : Exception(_message)

class HttpErrorException(
    val code: Int,
    val errorMessage: String,
    val errorBody: String?
) : java.io.IOException("HTTP $code: $errorMessage")

