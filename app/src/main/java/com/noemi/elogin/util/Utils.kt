package com.noemi.elogin.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showSnackBar(scope: CoroutineScope, snackBarHostState: SnackbarHostState, message: String) {
    scope.launch {
        snackBarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}