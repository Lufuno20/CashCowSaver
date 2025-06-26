package com.example.cashcowsaver.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.R
import com.example.cashcowsaver.databinding.ItemLatestActivtyBinding
import com.example.cashcowsaver.models.GoalTransaction

class GoalTransactionAdapter : RecyclerView.Adapter<GoalTransactionAdapter.TransactionViewHolder>() {

    private var transactions = listOf<GoalTransaction>()

    inner class TransactionViewHolder(val binding: ItemLatestActivtyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemLatestActivtyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val tx = transactions[position]
        holder.binding.label.text = if (tx.isAdd) "Saved" else "Cash Out"
        holder.binding.date.text = tx.date
        holder.binding.amount.text = "R%.2f".format(tx.amount)

        holder.binding.icon.setImageResource(
            if (tx.isAdd) R.drawable.baseline_arrow_upward_24 else R.drawable.baseline_arrow_downward_24
        )
        holder.binding.icon.setColorFilter(
            ContextCompat.getColor(holder.itemView.context,
                if (tx.isAdd) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )
    }

    override fun getItemCount() = transactions.size

    fun setData(newList: List<GoalTransaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
