package com.noemi.elogin.network

import com.noemi.elogin.model.ApiResponse
import com.noemi.elogin.model.Error
import javax.inject.Inject

class ApiServiceImpl @Inject constructor() : ApiService {

    override fun login(userName: String, password: String): ApiResponse<Int?> {
        return when (userName) {
            "user" -> ApiResponse(
                result = 1,
                error = null
            )

            "wrong" -> ApiResponse(
                result = null,
                error = Error.WRONG_CREDENTIALS
            )

            "internal" -> ApiResponse(
                result = null,
                error = Error.INTERNAL_SERVER_ERROR
            )

            else -> ApiResponse(
                result = null,
                error = Error.UNKNOWN_ERROR
            )
        }
    }
}