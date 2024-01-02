package com.example.firestoredemoapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FirestoreDemoApplication : Application() {
    lateinit var firestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()

        // Initialize Firestore
        firestore = Firebase.firestore

    }
}