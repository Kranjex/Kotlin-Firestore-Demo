package com.example.firestoredemoapp

import android.app.Application
import com.example.firestoredemoapp.activities.ExpenseTrackerAdapter
import com.example.firestoredemoapp.model.Expense
import com.example.firestoredemoapp.services.FirestoreService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FirestoreDemoApplication : Application() {
    private lateinit var recyclerViewAdapter: ExpenseTrackerAdapter
    lateinit var firestore: FirebaseFirestore
    var expenses: MutableList<Expense> = mutableListOf()

    override fun onCreate() {
        super.onCreate()

        // Initialize Firestore
        firestore = Firebase.firestore

        // Set application in FirestoreService object
        FirestoreService.setApplication(this)
    }

    fun setAdapter(adapter: ExpenseTrackerAdapter) {
        this.recyclerViewAdapter = adapter
    }

    fun notifyAdapter() {
        this.recyclerViewAdapter.notifyDataSetChanged()
    }
}