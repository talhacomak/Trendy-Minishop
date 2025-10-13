package com.trendy.core.common

sealed interface AppError {
    data object Network : AppError
    data object Server : AppError
    data object Empty : AppError
    data class Unknown(val throwable: Throwable) : AppError
}
