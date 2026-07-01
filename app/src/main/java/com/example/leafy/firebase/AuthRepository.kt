package com.example.leafy.firebase

import com.example.leafy.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser get() = auth.currentUser

    suspend fun login(email: String, password: String): User? {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            auth.currentUser?.let { u ->
                User(
                    id = u.uid,
                    name = u.displayName ?: "",
                    email = u.email ?: ""
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(name: String, email: String, password: String): User? {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val u = auth.currentUser ?: return null

            // Opcional: actualizar displayName
            val profileUpdates =
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            u.updateProfile(profileUpdates).await()

            User(
                id = u.uid,
                name = name,
                email = email
            )
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }
}