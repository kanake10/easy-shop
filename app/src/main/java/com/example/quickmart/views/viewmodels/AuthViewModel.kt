/*
 * Copyright 2025 Ezra Kanake.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.quickmart.views.viewmodels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SecureStorage
import com.example.data.interfaces.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

typealias AuthResponse = Response<Unit>
const val EMPTY_STRING = ""

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val secureStorage: SecureStorage
) : ViewModel() {
    private val _email = MutableStateFlow(TextFieldValue(EMPTY_STRING))
    val email: StateFlow<TextFieldValue> = _email.asStateFlow()

    private val _password = MutableStateFlow(TextFieldValue(EMPTY_STRING))
    val password: StateFlow<TextFieldValue> = _password.asStateFlow()

    val _authState = MutableStateFlow<AuthResponse>(Response.Idle)
    val authState: StateFlow<AuthResponse> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onEmailChange(newEmail: TextFieldValue) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: TextFieldValue) {
        _password.value = newPassword
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        try {
            _authState.value = Response.Loading
            _authState.value = Response.Success(repo.signInWithEmailAndPassword(email, password))
            saveCredentials(email, password)
        } catch (e: Exception) {
            _authState.value = Response.Failure(e)
        }
    }

    fun signUp(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            _authState.value = Response.Loading
            _authState.value = Response.Success(repo.signUpWithEmailAndPassword(email, password))
            saveCredentials(email, password)
        } catch (e: Exception) {
            _authState.value = Response.Failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    fun loadSavedCredentials() = viewModelScope.launch {
        val (savedEmail, savedPassword) = repo.getSavedCredentials()
        _email.value = TextFieldValue(savedEmail ?: EMPTY_STRING)
        _password.value = TextFieldValue(savedPassword ?: EMPTY_STRING)
    }

    fun saveCredentials(email: String, password: String) = viewModelScope.launch {
        secureStorage.saveEncryptedData("email", email)
        secureStorage.saveEncryptedData("password", password)
    }

    val isEmailVerified get() = repo.currentUser?.isEmailVerified == true
}

sealed class Response<out T> {
    data object Idle : Response<Nothing>()
    data object Loading : Response<Nothing>()
    data class Success<out T>(
        val data: T?
    ) : Response<T>()
    data class Failure(
        val e: Exception?
    ) : Response<Nothing>()
}
