package com.fittracksa.app.domain.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthManager(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            // Optionally ensure user doc exists
            val uid = auth.currentUser?.uid
            uid?.let {
                firestore.collection("users").document(it)
                    .set(mapOf("email" to email, "displayName" to (auth.currentUser?.displayName ?: "")), com.google.firebase.firestore.SetOptions.merge())
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val update = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
            result.user?.updateProfile(update)?.await()
            val uid = result.user?.uid
            uid?.let {
                firestore.collection("users").document(it)
                    .set(mapOf("email" to email, "displayName" to displayName))
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDisplayName(newName: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user signed in"))
            val update = UserProfileChangeRequest.Builder().setDisplayName(newName).build()
            user.updateProfile(update).await()

            val uid = user.uid
            firestore.collection("users").document(uid)
                .set(mapOf("displayName" to newName), com.google.firebase.firestore.SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUserName(): String? = auth.currentUser?.displayName
}
