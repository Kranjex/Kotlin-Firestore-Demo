package com.example.firestoredemoapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestoredemoapp.FirestoreDemoApplication
import com.example.firestoredemoapp.databinding.ActivityMainBinding
import com.example.firestoredemoapp.model.Expense
import com.example.firestoredemoapp.services.FirestoreService
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var app: FirestoreDemoApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as FirestoreDemoApplication
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val expensesAdapter = ExpenseTrackerAdapter(app,
            // Click listener
            object : ExpenseTrackerAdapter.itemOnClick {
                override fun onClick(p0: View?, position: Int) {
                    val intent = Intent(p0?.context, ExpenseFormActivity::class.java)
                    val id = UUID(
                        app.expenses[position].id.mostSignificantBits,
                        app.expenses[position].id.leastSignificantBits
                    )
                    val storeName = app.expenses[position].storeName
                    val timestamp = app.expenses[position].timestamp
                    val price = app.expenses[position].price

                    intent.putExtra("SELECTED_ID", id.toString())
                    intent.putExtra("SELECTED_STORE_NAME", storeName)
                    intent.putExtra("SELECTED_TIMESTAMP", timestamp)
                    intent.putExtra("SELECTED_PRICE", price)
                    startActivity(intent)
                }
            },

            // Long click listener
            object : ExpenseTrackerAdapter.itemOnLongClick {
                override fun onLongClick(p0: View?, position: Int) {
                    FirestoreService.deleteExpense(
                        app.expenses[position].id,
                        object : FirestoreService.FirestoreCallback {}
                    )
                }
            }
        )

        binding.recyclerView.adapter = expensesAdapter
        app.setAdapter(expensesAdapter)

        // Add expense button
        binding.addExpenseButton.setOnClickListener {
            val intent = Intent(this, ExpenseFormActivity::class.java);
            startActivity(intent)
        }


        // Load expenses on app start (restart)
        FirestoreService.getExpenses(object : FirestoreService.FirestoreCallback {
            override fun onDataLoaded(expenses: MutableList<Expense>) {
                app.expenses = expenses
                expensesAdapter.notifyDataSetChanged()
            }
        })
    }
}