package com.noemi.elogin.repository

import com.noemi.elogin.model.ApiResponse
import com.noemi.elogin.network.ApiService
import javax.inject.Inject

class LogInRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : LogInRepository {

    override fun loginUser(email: String, password: String): ApiResponse<Int?> =
        apiService.login(userName = email, password = password)
}