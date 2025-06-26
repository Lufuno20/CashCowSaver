import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.GoalActivity
import com.example.cashcowsaver.database.GoalAppDatabase
import com.example.cashcowsaver.databinding.ItemGoalBinding
import com.example.cashcowsaver.models.GoalEntity
import kotlinx.coroutines.launch

class GoalAdapter(
    private val context: Context,
    private val onGoalClick: (GoalEntity) -> Unit,
    private val onGoalCompleted: (GoalEntity) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var goals = listOf<GoalEntity>()

    inner class GoalViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        val isGoalComplete = goal.savedAmount >= goal.amount

        holder.binding.goalTitle.text = goal.title
        holder.binding.goalTargetAmount.text = "Goal: R %.2f".format(goal.amount)
        holder.binding.goalSavedAmount.text = "R %.2f".format(goal.savedAmount)
        holder.binding.goalIcon.setImageResource(goal.iconResId)
        val progress = ((goal.savedAmount / goal.amount) * 100).toInt().coerceIn(0, 100)
        holder.binding.goalProgressBar.progress = progress


        // Trophy
        holder.binding.goalMedal.visibility = if (isGoalComplete) View.VISIBLE else View.GONE

        // Notify only if not already marked as completed
        if (isGoalComplete && !goal.isCompleted) {
            onGoalCompleted(goal)
        }

        holder.binding.root.setOnClickListener {
            onGoalClick(goal)
        }
    }
    override fun getItemCount() = goals.size

    fun setData(newGoals: List<GoalEntity>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}
