package com.info.commons

import android.app.Activity
import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Single

/**
 * Class to handle communication with Firebase information such as getting the ID of the
 * logged in user.
 */
class FirebaseManager {
    fun getFirebaseUserID(activity: Activity): Single<String> {
        return Single.create { emitter ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener(activity) { instanceIdResult ->
                val newToken = instanceIdResult.getResult()
                emitter.onSuccess(newToken)
                val sp = activity.getSharedPreferences("debug_prefs", Context.MODE_PRIVATE)
                with(sp.edit()) {
                    putString("debug_firebase_id", newToken)
                    apply()
                }
            }.addOnFailureListener {
                emitter.onError(Throwable("Could not get Firebase Instance ID ${it.message}"))
            }
        }
    }
}