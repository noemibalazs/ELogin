package com.noemi.elogin.repository

import com.noemi.elogin.model.ApiResponse

interface LogInRepository {

    fun loginUser(email: String, password: String): ApiResponse<Int?>
}