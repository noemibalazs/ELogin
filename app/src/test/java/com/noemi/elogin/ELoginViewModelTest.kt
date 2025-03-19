package com.noemi.elogin

import app.cash.turbine.test
import com.noemi.elogin.model.ApiResponse
import com.noemi.elogin.model.Error
import com.noemi.elogin.model.LoginUIEvent
import com.noemi.elogin.repository.LogInRepository
import com.noemi.elogin.screen.ELoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ELoginViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val repository: LogInRepository = mockk()
    private lateinit var viewModel: ELoginViewModel

    private val email = "user"
    private val wrong = "wrong"
    private val password = "password1!"

    private val successApiResult = ApiResponse<Int?>(
        result = 1,
        error = null
    )
    private val failedApiResult = ApiResponse<Int?>(
        result = null,
        error = Error.WRONG_CREDENTIALS
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = ELoginViewModel(
            logInRepository = repository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test logIn and should be successful`() = runBlocking {

        viewModel.onEvent(LoginUIEvent.EmailChanged(email))
        viewModel.onEvent(LoginUIEvent.PasswordChanged(password))

        val job = launch {

            coEvery { repository.loginUser(eq(email), eq(password)) } returns successApiResult

            viewModel.logIntState.test {

                val result = awaitItem()
                assertTrue(result.data.result == successApiResult.result)
                assertTrue(result.data.error == successApiResult.error)
                assertTrue(!result.isLoading)

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.loginUser(email, password) }
        }

        viewModel.onEvent(LoginUIEvent.LogIn)

        job.cancelAndJoin()
    }

    @Test
    fun `test logIn and should fail with wrong credential error`() = runBlocking {

        viewModel.onEvent(LoginUIEvent.EmailChanged(wrong))
        viewModel.onEvent(LoginUIEvent.PasswordChanged(password))

        val job = launch {

            coEvery { repository.loginUser(eq(wrong), eq(password)) } returns failedApiResult

            viewModel.logIntState.test {

                val result = awaitItem()
                assertTrue(result.data.result == failedApiResult.result)
                assertTrue(result.data.error == failedApiResult.error)
                assertTrue(!result.isLoading)

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.loginUser(wrong, password) }
        }

        viewModel.onEvent(LoginUIEvent.LogIn)

        job.cancelAndJoin()
    }
}