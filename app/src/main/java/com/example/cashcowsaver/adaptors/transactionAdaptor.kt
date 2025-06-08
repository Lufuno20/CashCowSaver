package com.example.cashcowsaver.adaptors

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.R
import com.example.cashcowsaver.models.Transaction
import com.example.cashcowsaver.databinding.TransactionItemBinding


class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionView>() {


    private var transactionList: List<Transaction> = listOf()

    fun submitList(newList: List<Transaction>) {
        transactionList = newList
        notifyDataSetChanged()
    }
    inner class TransactionView(val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionView {
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionView(binding)
    }

    override fun onBindViewHolder(holder: TransactionView, position: Int) {
        val transaction = transactionList[position]

        holder.binding.imgIcon.setImageResource(transaction.iconResId)
        holder.binding.tvCategory.text = transaction.category
        holder.binding.tvNote.text = transaction.description  // if note exists
        holder.binding.tvDate.text = transaction.date
        holder.binding.tvAmount.text = formatAmount(transaction.amount, transaction.type)
    }
    override fun getItemCount(): Int = transactionList.size

    private fun formatAmount(amount: Double, type: String): SpannableString {
        val sign = if (type == "income") "+" else "-"
        val color = if (type == "income") Color.parseColor("#2e7d32") else Color.parseColor("#c62828")
        val formatted = "$sign R${String.format("%.2f", amount)}"
        val spannable = SpannableString(formatted)
        spannable.setSpan(ForegroundColorSpan(color), 0, formatted.length, 0)
        return spannable
    }


    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.imgIcon)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val noteText: TextView = itemView.findViewById(R.id.tvNote)
        val amountText: TextView = itemView.findViewById(R.id.tvAmount)
        val date: TextView = itemView.findViewById(R.id.tvDate)
    }

}

