package io.jumpinggoose.unworthy.android.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import io.jumpinggoose.unworthy.PlayerDataRepository
import io.jumpinggoose.unworthy.models.PlayerData

class AndroidPlayerDataRepository : PlayerDataRepository {

    private var auth = Firebase.auth
    private val db = Firebase.firestore
    private val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    private fun getUser(action: ((FirebaseUser?) -> Unit)) {
        if (user != null) {
            action(user)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    action(result.user)
                }
                .addOnFailureListener { exception ->
                    action(null)
                }
        }
    }

    override fun getData(
        onSuccess: (PlayerData) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getUser { user ->
            if (user == null) {
                onFailure(Exception("User not authenticated."))
                return@getUser
            }
            db.collection("playerData")
                .document(user.uid)
                .addSnapshotListener { document, exception ->
                    if (exception != null) {
                        onFailure(exception)
                    } else if (document == null) {
                        onSuccess(PlayerData())
                    } else {
                        val playerData = document.toObject<PlayerData>()
                        if (playerData == null) {
                            onFailure(Exception("Failed to parse player data."))
                        } else {
                            onSuccess(playerData)
                        }
                    }
                }
        }
    }

    override fun setData(
        data: PlayerData,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getUser { user ->
            if (user == null) {
                onFailure(Exception("User not authenticated."))
                return@getUser
            }
            db.collection("playerData")
                .document(user.uid)
                .set(data)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
}
