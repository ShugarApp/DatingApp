package com.dating.core.domain.util

class DataErrorException(
    val error: DataError
) : Exception()
