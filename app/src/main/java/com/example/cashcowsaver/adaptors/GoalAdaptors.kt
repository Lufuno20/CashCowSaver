package com.example.cashcowsaver.adaptors


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.databinding.ItemGoalBinding
import com.example.cashcowsaver.models.GoalEntity

class GoalAdapter(private var goals: List<GoalEntity>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.binding.goalTitle.text = goal.title
        holder.binding.goalTargetAmount.text = "R%.2f".format(goal.amount)
        holder.binding.goalIcon.setImageResource(goal.iconResId)
       // holder.binding.goalStatus.text = goal.status
    }

    override fun getItemCount() = goals.size

    fun updateData(newGoals: List<GoalEntity>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}
