package com.example.cashcowsaver.adaptors

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.databinding.TransactionItemBinding
import com.example.cashcowsaver.models.TransactionEntity
import java.text.NumberFormat
import java.util.*

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var transactions: List<TransactionEntity> = emptyList()

    fun setData(data: List<TransactionEntity>) {
        transactions = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TransactionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]

        // Set category name and note
        holder.binding.tvCategory.text = item.category
        holder.binding.tvNote.text = item.description
        holder.binding.tvDate.text = item.date

        // Format amount with R and decimals
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        val formattedAmount = currencyFormat.format(item.amount)

        val isExpense = item.type.equals("Expense", ignoreCase = true)
        val displayText = if (isExpense) "-$formattedAmount" else "+$formattedAmount"

        holder.binding.tvAmount.text = displayText
        holder.binding.tvAmount.setTextColor(if (isExpense) Color.RED else Color.parseColor("#4CAF50"))
        holder.binding.imgIcon.setImageResource(item.iconResId)
    }
}

