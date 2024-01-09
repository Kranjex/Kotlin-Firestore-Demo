package com.example.firestoredemoapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firestoredemoapp.FirestoreDemoApplication
import com.example.firestoredemoapp.databinding.ActivityExpenseFormBinding
import com.example.firestoredemoapp.model.Expense
import com.example.firestoredemoapp.services.FirestoreService
import com.example.firestoredemoapp.services.FirestoreService.FirestoreCallback
import java.util.UUID

class ExpenseFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseFormBinding;
    private lateinit var app: FirestoreDemoApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as FirestoreDemoApplication
        binding = ActivityExpenseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back (cancel) button
        binding.cancelButton.setOnClickListener {
            finish()
        }

        // Create button
        binding.updateExpenseButton.text = "Create"
        binding.updateExpenseButton.setOnClickListener {
            val expenseStoreName: String = binding.expenseStoreNameInput.text.toString()
            val expenseDate: String = binding.expenseDateInput.text.toString()
            val expensePriceString: String = binding.expensePriceInput.text.toString()

            if (expenseStoreName.isEmpty()) {
                Toast.makeText(this, "Store name should not be empty!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (expenseDate.isEmpty()) {
                Toast.makeText(this, "Date should not be empty!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (expensePriceString.isEmpty()) {
                Toast.makeText(this, "Price should not be null!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var expensePrice: Double = expensePriceString.toDouble()
            val newExpense = Expense(expenseStoreName, expenseDate, expensePrice)

            // Create new expense in Firestore
            FirestoreService.createExpense(newExpense.id, newExpense,
                object : FirestoreCallback {
                    override fun onDataLoaded(expenses: MutableList<Expense>) {
                        app.expenses.add(expenses[0])
                        // notify adapter
                        app.notifyAdapter()
                        finish()
                    }
                })
        }

        // Check if edit mode is enabled
        val selectedId = intent.getStringExtra("SELECTED_ID") ?: return

        val expenseId = UUID.fromString(selectedId)
        val expenseStoreName = intent.getStringExtra("SELECTED_STORE_NAME")
        val expenseTimestamp = intent.getStringExtra("SELECTED_TIMESTAMP")
        val expensePrice = intent.getDoubleExtra("SELECTED_PRICE", 0.0)

        if (expenseStoreName == null || expenseTimestamp == null)
            return

        binding.updateExpenseButton.text = "Update"
        binding.expenseStoreNameInput.setText(expenseStoreName)
        binding.expenseDateInput.setText(expenseTimestamp)
        binding.expensePriceInput.setText(expensePrice.toString())

        binding.updateExpenseButton.setOnClickListener {
            val newStoreName = binding.expenseStoreNameInput.text.toString()
            val newTimestamp = binding.expenseDateInput.text.toString()
            val newPrice = binding.expensePriceInput.text.toString().toDouble()

            FirestoreService.updateExpense(expenseId,
                Expense(newStoreName, newTimestamp, newPrice),
                object : FirestoreCallback {
                    override fun onDataLoaded(expenses: MutableList<Expense>) {
                        val id = app.expenses.indexOfFirst { it.id == expenseId }
                        app.expenses[id].storeName = newStoreName
                        app.expenses[id].timestamp = newTimestamp
                        app.expenses[id].price = newPrice

                        app.notifyAdapter()
                        finish()
                    }
                }
            )
        }
    }
}