package com.noemi.elogin.model

data class RegistrationFormState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String = ""
)