package io.jumpinggoose.unworthy.android.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import io.jumpinggoose.unworthy.PlayerDataRepository
import io.jumpinggoose.unworthy.android.repository.models.AndroidPlayerData
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
                .whereEqualTo("owner", user.uid)
                .addSnapshotListener { documents, exception ->
                    if (exception != null) {
                        onFailure(exception)
                    } else if (documents == null || documents.isEmpty) {
                        onSuccess(PlayerData())
                    } else {
                        val androidPlayerData = documents.first().toObject<AndroidPlayerData>()
                        onSuccess(androidPlayerData.data)
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
            val androidPlayerData = AndroidPlayerData(user.uid, data)
            db.collection("playerData")
                .whereEqualTo("owner", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        db.collection("playerData")
                            .add(androidPlayerData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    } else {
                        db.collection("playerData")
                            .document(documents.first().id)
                            .set(androidPlayerData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
}
