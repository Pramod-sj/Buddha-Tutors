package com.buddhatutors.domain.model

sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()

    data class Error(val throwable: Throwable) : Resource<Nothing>()

}