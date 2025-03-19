package com.noemi.elogin.model

sealed class LoginUIEvent {
    data class EmailChanged(val email: String) : LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()
    data object CredentialError : LoginUIEvent()
    data object LogIn : LoginUIEvent()
    data object LogInError : LoginUIEvent()
    data object ClearLogInMessage : LoginUIEvent()
}
