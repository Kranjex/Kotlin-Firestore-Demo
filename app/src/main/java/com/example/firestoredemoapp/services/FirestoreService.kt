package com.example.firestoredemoapp.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.firestoredemoapp.FirestoreDemoApplication
import com.example.firestoredemoapp.model.Expense
import com.example.firestoredemoapp.util.FirestoreStrings
import com.example.firestoredemoapp.util.SimpleDateFormatter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.UUID

object FirestoreService {
    var app: FirestoreDemoApplication? = null

    interface FirestoreCallback {
        fun onDataLoaded(expenses: MutableList<Expense>) {}
        fun onDataCreated(successMessage: String) {
            Toast.makeText(
                app,
                successMessage,
                Toast.LENGTH_LONG
            ).show()
        }

        fun onDataError(errorMessage: String) {
            Toast.makeText(
                app,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }

        fun onDataDelete(expense: Expense) {
            app?.expenses?.remove(expense)
            app?.notifyAdapter()
        }

    }

    fun setApplication(application: FirestoreDemoApplication) {
        app = application
    }

    // READ all expenses from database
    fun getExpenses(callback: FirestoreCallback): MutableList<Expense> {
        val collection: MutableList<Expense> = mutableListOf()

        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .get()
            .addOnSuccessListener { snapshot ->

                for (document in snapshot) {
                    Log.d("Firestore READ", document.data.toString())
                    collection.add(Expense.fromFirestoreMap(document.data as Map<String, Any>))
                }

                callback.onDataLoaded(collection)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore READ", "Error reading documents: $exception")
                callback.onDataError("Error loading expenses from database!")
            }

        return collection
    }

    // READ only specified expense from database
    fun getExpense(id: UUID, callback: FirestoreCallback): Expense? {
        var document: Expense? = null

        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id.toString())
            .get()
            .addOnSuccessListener { snapshot ->
                document = Expense.fromFirestoreMap(snapshot.data as Map<String, Any>)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Error loading expense from database!",
                    Toast.LENGTH_LONG
                ).show()

                Log.w("Firestore READ", "Error reading document: $exception")
            }

        return document
    }

    // CREATE new expense in database
    fun createExpense(id: UUID, expense: Expense, callback: FirestoreCallback): Expense {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id.toString())
            .set(expense)
            .addOnSuccessListener {
                callback.onDataLoaded(mutableListOf(expense))
                callback.onDataCreated("Expense from ${expense.storeName} successfully created!")
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore CREATE", "Error creating new document: $exception")
                callback.onDataError("Error creating new expense!")
            }

        return expense
    }

    // UPDATE specified expense in database
    fun updateExpense(id: UUID, expense: Expense, callback: FirestoreCallback): Expense {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id.toString())
            .update(
                "storeName", expense.storeName,
                "timestamp", expense.timestamp,
                "price", expense.price,
                "modifiedAt", SimpleDateFormatter.createNewDate()
            )
            .addOnSuccessListener {
                callback.onDataCreated("Expense from ${expense.storeName} successfully updated!")
                callback.onDataLoaded(mutableListOf(expense))
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore UPDATE", "Error updating document: $exception")
                callback.onDataError("Error updating expense!")
            }

        return expense
    }

    // DELETE specified expense in database
    fun deleteExpense(id: UUID, callback: FirestoreCallback) {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id.toString())
            .delete()
            .addOnSuccessListener {
                callback.onDataCreated("Expense with $id successfully deleted!")
                app?.expenses
                    ?.find { it.id.equals(id) }
                    ?.let { foundExpense ->
                        callback.onDataDelete(foundExpense)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore UPDATE", "Error updating document: $exception")
                callback.onDataError("Error deleting expense!")
            }
    }
}