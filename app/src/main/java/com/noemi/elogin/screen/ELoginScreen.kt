package com.noemi.elogin.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.elogin.R
import com.noemi.elogin.model.LoginUIEvent
import com.noemi.elogin.util.showSnackBar


@Composable
fun ELoginScreen(modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<ELoginViewModel>()
    val registrationFormState by viewModel.registrationFormState.collectAsStateWithLifecycle()
    val logInState by viewModel.logIntState.collectAsStateWithLifecycle()

    val keyBoardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val emailInteractionSource = remember { MutableInteractionSource() }
    val passwordInteractionSource = remember { MutableInteractionSource() }
    var passwordVisualTransformation by remember {
        mutableStateOf<VisualTransformation>(
            PasswordVisualTransformation()
        )
    }

    Scaffold(
        topBar = {
            ELoginAppBar()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Spacer(modifier = modifier.padding(20.dp))

            EmailTextField(
                email = registrationFormState.email,
                onEmailChanged = { viewModel.onEvent(LoginUIEvent.EmailChanged(it)) },
                interactionSource = emailInteractionSource
            )

            PasswordTextField(
                password = registrationFormState.password,
                onPasswordChanged = { viewModel.onEvent(LoginUIEvent.PasswordChanged(it)) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = when (passwordVisualTransformation != VisualTransformation.None) {
                                true -> R.drawable.ic_eye_off
                                else -> R.drawable.ic_eye_on
                            }
                        ),
                        contentDescription = stringResource(id = R.string.label_placeholder_password),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            passwordVisualTransformation = when (passwordVisualTransformation != VisualTransformation.None) {
                                true -> VisualTransformation.None
                                else -> PasswordVisualTransformation()
                            }
                        }
                    )
                },
                passwordVisualTransformation = passwordVisualTransformation,
                interactionSource = passwordInteractionSource,
                keyBoardController = keyBoardController
            )

            Spacer(modifier = modifier.padding(20.dp))

            LogInButton(
                onClick = { viewModel.onEvent(LoginUIEvent.LogIn) },
                keyBoardController = keyBoardController,
                isLoading = logInState.isLoading
            )
        }

        if (registrationFormState.errorMessage.isNotEmpty()) showSnackBar(
            message = registrationFormState.errorMessage, snackBarHostState = snackBarHostState, scope = scope
        ).also { viewModel.onEvent(LoginUIEvent.CredentialError) }

        if (logInState.data.error != null) showSnackBar(
            message = logInState.data.error?.name ?: "", snackBarHostState = snackBarHostState, scope = scope
        ).also { viewModel.onEvent(LoginUIEvent.LogInError) }

        if (logInState.data.result != null) showSnackBar(
            message = "User ${logInState.data.result} logged in successful!", snackBarHostState = snackBarHostState, scope = scope
        ).also { viewModel.onEvent(LoginUIEvent.ClearLogInMessage) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ELoginAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_icon),
                contentDescription = stringResource(id = R.string.label_icon_content_description),
                modifier = modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(32.dp),
                tint = Color.White
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.testTag(stringResource(id = R.string.label_app_bar_tag))
    )
}


@Composable
private fun EmailTextField(
    email: String,
    onEmailChanged: (String) -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        value = email,
        onValueChange = { onEmailChanged.invoke(it) },
        label = {
            Text(
                text = stringResource(id = R.string.label_placeholder_email),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Email, contentDescription = stringResource(id = R.string.label_placeholder_email),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        ),
        interactionSource = interactionSource,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    )
}


@Composable
private fun PasswordTextField(
    password: String,
    onPasswordChanged: (String) -> Unit,
    trailingIcon: @Composable () -> Unit,
    passwordVisualTransformation: VisualTransformation,
    interactionSource: MutableInteractionSource,
    keyBoardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 20.dp, end = 20.dp),
        value = password,
        onValueChange = { onPasswordChanged.invoke(it) },
        label = {
            Text(
                text = stringResource(id = R.string.label_placeholder_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary
        ),
        maxLines = 1,
        trailingIcon = {
            trailingIcon.invoke()
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock, contentDescription = stringResource(id = R.string.label_placeholder_password),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyBoardController?.hide()
            }
        ),
        interactionSource = interactionSource,
        visualTransformation = passwordVisualTransformation
    )
}


@Composable
private fun LogInButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    keyBoardController: SoftwareKeyboardController?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Button(
            enabled = !isLoading,
            onClick = {
                onClick.invoke()
                keyBoardController?.hide()
            },
            modifier = modifier
                .height(50.dp)
                .wrapContentWidth()
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(id = R.string.label_login),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = modifier.size(40.dp),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}