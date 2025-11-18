package com.fittracksa.app.notifications

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object MessagingHelper {
    private const val TAG = "MessagingHelper"

    /**
     * Fetch FirebaseMessaging token and save to users/{uid}.fcmToken
     * Call this after successful login / registration.
     */
    fun fetchAndSaveToken() {
        val user = FirebaseAuth.getInstance().currentUser ?: run {
            Log.d(TAG, "No signed-in user; skipping token save")
            return
        }
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                Log.d(TAG, "Got FCM token: $token")
                val uid = user.uid
                val map = mapOf("fcmToken" to token)
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .set(map, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener { Log.d(TAG, "Saved FCM token to Firestore") }
                    .addOnFailureListener { e -> Log.w(TAG, "Save token failed: ${e.localizedMessage}") }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Failed to get FCM token: ${e.localizedMessage}")
            }
    }
}
