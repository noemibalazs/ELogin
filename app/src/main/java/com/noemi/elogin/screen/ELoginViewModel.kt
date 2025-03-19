package com.noemi.elogin.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noemi.elogin.model.LogInState
import com.noemi.elogin.model.LoginUIEvent
import com.noemi.elogin.model.RegistrationFormState
import com.noemi.elogin.repository.LogInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ELoginViewModel @Inject constructor(private val logInRepository: LogInRepository) : ViewModel() {

    private val _registrationFormState = MutableStateFlow(RegistrationFormState())
    val registrationFormState = _registrationFormState.asStateFlow()

    private val _logInState = MutableStateFlow(LogInState())
    val logIntState = _logInState.asStateFlow()

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> onEmailChanged(event.email.trimEnd())
            is LoginUIEvent.PasswordChanged -> onPasswordChanged(event.password.trimEnd())
            is LoginUIEvent.CredentialError -> clearErrorMessage()
            is LoginUIEvent.LogIn -> onLogInClicked()
            is LoginUIEvent.LogInError -> clearLogInError()
            is LoginUIEvent.ClearLogInMessage -> clearLogInMessage()
        }
    }

    private fun onEmailChanged(email: String) {
        viewModelScope.launch {
            _registrationFormState.update {
                it.copy(email = email)
            }
        }
    }

    private fun onPasswordChanged(password: String) {
        viewModelScope.launch {
            _registrationFormState.update {
                it.copy(password = password)
            }
        }
    }

    private fun clearErrorMessage() {
        viewModelScope.launch {
            delay(900)

            _registrationFormState.update {
                it.copy(errorMessage = "")
            }
        }
    }

    private fun onLogInClicked() {
        viewModelScope.launch {
            val state = _registrationFormState.value

            when {
                state.email.isBlank() -> _registrationFormState.update {
                    it.copy(errorMessage = "Error, the email cannot be empty!")
                }

                state.password.isBlank() -> _registrationFormState.update {
                    it.copy(errorMessage = "Error, the password cannot be empty!")
                }

                state.email.isNotBlank() && state.password.isNotBlank() -> logIn()
            }
        }
    }

    private fun logIn() {
        viewModelScope.launch {

            _logInState.update {
                it.copy(
                    isLoading = true
                )
            }

            delay(900)

            val state = _registrationFormState.value

            viewModelScope.launch(Dispatchers.IO) {
                val response = logInRepository.loginUser(
                    email = state.email,
                    password = state.password
                )

                _logInState.update {
                    it.copy(
                        isLoading = false,
                        data = response
                    )
                }
            }
        }
    }

    private fun clearLogInError() {
        viewModelScope.launch {
            delay(900)

            _logInState.update {
                it.copy(
                    data = it.data.copy(error = null)
                )
            }
        }
    }

    private fun clearLogInMessage() {
        viewModelScope.launch {
            delay(900)

            _logInState.update {
                it.copy(
                    data = it.data.copy(result = null)
                )
            }
        }
    }
}