package com.example.firestoredemoapp.activities

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.firestoredemoapp.FirestoreDemoApplication
import com.example.firestoredemoapp.R
import com.example.firestoredemoapp.services.FirestoreService

class ExpenseTrackerAdapter(
    private val app: FirestoreDemoApplication,
    private val onClickObject: itemOnClick,
    private val onLongClickObject: itemOnLongClick
) : RecyclerView.Adapter<ExpenseTrackerAdapter.ViewHolder>() {

    interface itemOnClick {
        fun onClick(p0: View?, position: Int)
    }

    interface itemOnLongClick {
        fun onLongClick(p0: View?, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val expenseTimestamp: TextView = itemView.findViewById(R.id.timestamp)
        val expensePrice: TextView = itemView.findViewById(R.id.expensePrice)
        val container: CardView = itemView.findViewById(R.id.expenseCardContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.expense_card_layout, parent, false)

        Log.d("Recycler View Debug", "onCreateViewHolder")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Recycler View Debug", "onBindViewHolder")

        val expense = app.expenses[position]

        holder.storeName.text = expense.storeName
        holder.expenseTimestamp.text = expense.timestamp
        holder.expensePrice.text = "${expense.price.toString()}€"

        // Click (touch down)
        holder.container.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                // Open expense in edit mode
                onClickObject.onClick(p0, holder.adapterPosition)
            }
        })

        // Long click (touch and hold)
        holder.container.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                // Remove expense from the list
                onLongClickObject.onLongClick(p0, holder.adapterPosition)
                return true
            }
        })
    }

    override fun getItemCount(): Int = app.expenses.size
}
