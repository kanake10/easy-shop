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
package com.example.network.helper

import com.google.gson.GsonBuilder
import java.io.IOException
import java.net.UnknownHostException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): NetworkResult<T> = withContext(dispatcher) {
    try {
        NetworkResult.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is UnknownHostException -> {
                Timber.e(throwable, "Invalid URL or No Internet Connection")
                NetworkResult.NetworkError
            }
            is IOException -> {
                Timber.e(throwable, "Network failure. Check your internet connection.")
                NetworkResult.NetworkError
            }
            is HttpException -> {
                val errorResponse = convertErrorBody(throwable)
                Timber.e(throwable, "Server error: ${errorResponse?.message ?: "Unknown error"}")
                NetworkResult.ServerError(
                    code = throwable.code(),
                    errorBody = errorResponse
                )
            }
            else -> {
                Timber.e(throwable, "Unexpected error occurred.")
                NetworkResult.UnexpectedError(
                    errorMessage = throwable.localizedMessage ?: "Unexpected error",
                    throwable = throwable
                )
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? = try {
    throwable.response()?.errorBody()?.charStream()?.let {
        GsonBuilder().create().fromJson(it, ErrorResponse::class.java)
    }
} catch (exception: Exception) {
    Timber.e(exception, "Failed to parse error response")
    null
}
