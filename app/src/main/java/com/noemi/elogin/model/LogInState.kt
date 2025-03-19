package com.noemi.elogin.model

data class LogInState(
    val isLoading: Boolean = false,
    val data: ApiResponse<Int?> = ApiResponse(null, null)
)