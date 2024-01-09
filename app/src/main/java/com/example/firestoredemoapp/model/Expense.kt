package com.example.firestoredemoapp.model

import android.util.Log
import com.example.firestoredemoapp.util.SimpleDateFormatter
import java.util.UUID

data class Expense(
    var storeName: String,
    var timestamp: String,  // Timestamp (date) when expense was created (user bought something)
    var price: Double,
    var modifiedAt: String = SimpleDateFormatter.createNewDate(),   // Date when expense was modified in database
    val createdAt: String = SimpleDateFormatter.createNewDate(),    // Date when expense was created in database
    val id: UUID = UUID.randomUUID()
) {

    companion object {
        fun fromFirestoreMap(firestoreMap: Map<String, Any>): Expense {
            val storeName = firestoreMap["storeName"] as String
            val date = firestoreMap["timestamp"] as String
            val price = firestoreMap["price"] as Double

            val idMap = firestoreMap["id"] as Map<String, Any>
            val mostSignificantBits = idMap["mostSignificantBits"] as Long
            val leastSignificantBits = idMap["leastSignificantBits"] as Long
            val id = UUID(mostSignificantBits, leastSignificantBits)

            val createdAt = firestoreMap["createdAt"] as String
            val modifiedAt = firestoreMap["modifiedAt"] as String

            return Expense(storeName, date, price, modifiedAt, createdAt, id)
        }
    }

}
