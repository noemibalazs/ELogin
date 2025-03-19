package com.noemi.elogin.model

data class ApiResponse<T>(
    val result: T?,
    val error: Error?
)
