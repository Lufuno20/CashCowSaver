package com.example.cashcowsaver.adaptors


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.databinding.ItemGoalBinding
import com.example.cashcowsaver.models.GoalEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GoalAdapter : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var goals = listOf<GoalEntity>()
    inner class GoalViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }


    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.binding.goalTitle.text = goal.title
        holder.binding.goalTargetAmount.text = "R %.2f".format(goal.amount)
        holder.binding.goalIcon.setImageResource(goal.iconResId)

        // Placeholder for saved amount (you’ll update later)
        val savedAmount = 0.0
        holder.binding.goalSavedAmount.text = "R %.2f".format(savedAmount)

        // Progress
        val progress = ((savedAmount / goal.amount) * 100).toInt().coerceIn(0, 100)
        holder.binding.goalProgressBar.progress = progress

        // Months left until target date
        holder.binding.goalStatus.text = getMonthsLeft(goal.date)
    }

    override fun getItemCount(): Int = goals.size

    fun updateData(newGoals: List<GoalEntity>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    private fun getMonthsLeft(targetDateString: String): String {
        return try {
            val format = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
            val targetDate = format.parse(targetDateString)
            val now = Calendar.getInstance().time

            val diffMillis = targetDate.time - now.time
            val months = (diffMillis / (1000L * 60 * 60 * 24 * 30)).toInt()

            if (months <= 0) "Completed"
            else "$months months left"
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}
