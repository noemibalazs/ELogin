package com.noemi.elogin.network

import com.noemi.elogin.model.ApiResponse

interface ApiService {

    fun login(userName: String, password: String): ApiResponse<Int?>

}