package com.example.firestoredemoapp.services

import android.util.Log
import android.widget.Toast
import com.example.firestoredemoapp.FirestoreDemoApplication
import com.example.firestoredemoapp.model.Expense
import com.example.firestoredemoapp.util.FirestoreStrings
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object FirestoreService {

    // READ all expenses from database
    fun getExpenses(): MutableList<Expense> {
        val collection: MutableList<Expense> = mutableListOf()

        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot)
                    collection.add(Expense.fromFirestoreMap(document.data as Map<String, Any>))
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Error loading expenses from database!",
                    Toast.LENGTH_LONG
                ).show()

                Log.w("Firestore READ", "Error reading documents: $exception")
            }

        return collection
    }

    // READ only specified expense from database
    fun getExpense(id: String): Expense? {
        var document: Expense? = null

        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id)
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
    fun createExpense(id: String, expense: Expense): Expense {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id)
            .set(expense)
            .addOnSuccessListener { snapshot ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Expense with id: $id successfully created!",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Error creating new expense!",
                    Toast.LENGTH_LONG
                ).show()

                Log.w("Firestore CREATE", "Error creating new document: $exception")
            }

        return expense
    }

    // UPDATE specified expense in database
    fun updateExpense(id: String, expense: Expense): Expense {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id)
            .update(
                "storeName", expense.storeName,
                "timestamp", expense.timestamp,
                "price", expense.price,
            )
            .addOnSuccessListener { snapshot ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Expense with id: $id successfully updated!",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Error updating expense!",
                    Toast.LENGTH_LONG
                ).show()

                Log.w("Firestore UPDATE", "Error updating document: $exception")
            }

        return expense
    }

    // DELETE specified expense in database
    fun deleteExpense(id: String) {
        Firebase.firestore
            .collection(FirestoreStrings.COLLECTION_EXPENSES.string)
            .document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Expense with id: $id successfully updated!",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    FirestoreDemoApplication().applicationContext,
                    "Error updating expense!",
                    Toast.LENGTH_LONG
                ).show()

                Log.w("Firestore UPDATE", "Error updating document: $exception")
            }
    }
}