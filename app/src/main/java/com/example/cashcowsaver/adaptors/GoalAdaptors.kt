import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.databinding.ItemGoalBinding
import com.example.cashcowsaver.models.GoalEntity

class GoalAdapter(
    private val onGoalClick: (GoalEntity) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var goals = listOf<GoalEntity>()

    inner class GoalViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.binding.goalTitle.text = goal.title
        holder.binding.goalTargetAmount.text = "Goal: R %.2f".format(goal.amount)
        holder.binding.goalSavedAmount.text = "R %.2f".format(goal.savedAmount)
        holder.binding.goalIcon.setImageResource(goal.iconResId)
        val progress = ((goal.savedAmount / goal.amount) * 100).toInt().coerceIn(0, 100)
        holder.binding.goalProgressBar.progress = progress

        // Handle click
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
