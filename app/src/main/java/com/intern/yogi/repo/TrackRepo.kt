package com.intern.yogi.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.intern.yogi.OnError
import com.intern.yogi.OnSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class TrackRepo {

    private val firestore = FirebaseFirestore.getInstance()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBookDetails() = callbackFlow {

        val collection = firestore.collection("tracks")
        val snapshotListener = collection.addSnapshotListener { value, error ->
            val response = if (error == null) {
                OnSuccess(value)
            } else {
                OnError(error)
            }

            offer(response)
        }

        awaitClose {
            snapshotListener.remove()
        }
    }
}