package com.example.cashcowsaver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashcowsaver.adaptors.GoalTransactionAdapter
import com.example.cashcowsaver.database.GoalAppDatabase
import com.example.cashcowsaver.databinding.ActivitySavingActiveBinding
import com.example.cashcowsaver.models.GoalEntity
import com.example.cashcowsaver.models.GoalTransaction
import com.example.cashcowsaver.database.GoalTransactionDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class ActiveSavingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavingActiveBinding
    private var goal: GoalEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavingActiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // or onBackPressedDispatcher.onBackPressed()
        }

        val goalId = intent.getIntExtra("goal_id", -1)

        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@ActiveSavingActivity).goalDao().getAllGoals()
                .collect { goals ->
                    goal = goals.find { it.id == goalId }
                    goal?.let { displayGoal(it) }
                }
        }
        binding.btnadd.setOnClickListener {
            showCalculatorDialog { enteredAmount ->
                updateSavedAmount(enteredAmount)
            }
        }

        binding.btncashout.setOnClickListener {
            showCalculatorDialog { enteredAmount ->
                updateSavedAmount(-enteredAmount)
            }
        }
    }

    private fun displayGoal(goal: GoalEntity) {
        binding.txtgoalname.text = goal.title
        binding.goalicon.setImageResource(goal.iconResId)
        binding.txtstatus1.text = getMonthsLeft(goal.date)
        binding.amountsaved1.text = "R%.2f".format(goal.savedAmount)
        binding.goalamount1.text = "Goal: R%.2f".format(goal.amount)
        binding.progression.progress = ((goal.savedAmount / goal.amount) * 100).toInt().coerceIn(0, 100)

        // Latest Activity: just show last saved for now
        val transactionAdapter = GoalTransactionAdapter()
        binding.activityRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.activityRecyclerView.adapter = transactionAdapter

        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@ActiveSavingActivity)
                .goalTransactionDao()
                .getTransactionsForGoal(goal.id)
                .collect {
                    transactionAdapter.setData(it)
                }
        }

    }


    private fun showCalculatorDialog(onAmountEntered: (Double) -> Unit) {
        val dialog = CalculatorDialog(this) { amount ->
            if (amount > 0) onAmountEntered(amount)
        }
        dialog.show()
    }

    private fun updateSavedAmount(delta: Double) {
        goal?.let { goalItem ->
            val newAmount = (goalItem.savedAmount + delta).coerceAtLeast(0.0)
            val updatedGoal = goalItem.copy(savedAmount = newAmount)

            lifecycleScope.launch {
                val db = GoalAppDatabase.getDatabase(this@ActiveSavingActivity)

                // 1. Update the goal's savedAmount
                db.goalDao().insert(updatedGoal)

                // 2. Log the transaction
                val now = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                val transaction = GoalTransaction(
                    goalId = goalItem.id,
                    amount = delta.absoluteValue,
                    date = now,
                    isAdd = delta >= 0
                )
                db.goalTransactionDao().insert(transaction)
            }
        }
    }

    private fun getMonthsLeft(date: String): String {
        return try {
            val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
            val target = sdf.parse(date)
            val now = Calendar.getInstance().time
            val diff = target.time - now.time
            val months = (diff / (1000L * 60 * 60 * 24 * 30)).toInt()
            if (months <= 0) "Goal Reached" else "$months Months Left"
        } catch (e: Exception) {
            ""
        }
    }
}