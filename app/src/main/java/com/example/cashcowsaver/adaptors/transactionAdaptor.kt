package com.example.cashcowsaver.adaptors

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.R
import com.example.cashcowsaver.models.Transaction

class TransactionAdaptor(private val transactions: List<Transaction>) :
RecyclerView.Adapter<TransactionAdaptor.TransactionViewHolder>(){

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val icon: ImageView = itemView.findViewById(R.id.categoryimg)
        val category: TextView = itemView.findViewById(R.id.txtcategory)
        val description: TextView = itemView.findViewById(R.id.note_input)
        val date: TextView = itemView.findViewById(R.id.txtsetdate)
        val amount: TextView = itemView.findViewById(R.id.amount_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        TODO("Not yet implemented")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_page, parent,false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.icon.setImageResource(transaction.iconResId)
        holder.category.text = transaction.category
        holder.description.text = transaction.description
        holder.date.text = transaction.date

        val isExpense = transaction.type == "expense"
        val prefix = if (isExpense) "-" else "+"
        val color = if (isExpense) Color.RED else Color.GREEN

        holder.amount.text = "$prefix${"%.2f".format(transaction.amount)}"
        holder.amount.setTextColor(color)
    }

    override fun getItemCount(): Int = transactions.size

}