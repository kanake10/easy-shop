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
package com.example.data.impl

import com.example.data.SecureStorage
import com.example.data.interfaces.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val secureStorage: SecureStorage
) : AuthRepository {
    override val currentUser get() = auth.currentUser

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.sendEmailVerification()?.await()
        secureStorage.saveEncryptedData("email", email)
        secureStorage.saveEncryptedData("password", password)
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun deleteUser() {
        currentUser?.delete()?.await()
        secureStorage.saveEncryptedData("email", "")
        secureStorage.saveEncryptedData("password", "")
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getAuthState() = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun getSavedCredentials(): Pair<String?, String?> {
        val email = secureStorage.getDecryptedData("email")
        val password = secureStorage.getDecryptedData("password")
        return email to password
    }
}
