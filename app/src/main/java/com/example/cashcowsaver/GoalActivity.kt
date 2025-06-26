package com.example.cashcowsaver

import GoalAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.database.GoalAppDatabase
import com.example.cashcowsaver.databinding.GoalsActivityBinding
import kotlinx.coroutines.launch

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: GoalsActivityBinding
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoalsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish() // or onBackPressedDispatcher.onBackPressed()
        }

        // Navigate to CreateGoalActivity
        binding.creategoals.setOnClickListener {
            startActivity(Intent(this, CreateGoalActivity::class.java))
        }

        // Adapter handles goal click -> go to ActiveSavingActivity
        adapter = GoalAdapter(
            context = this,
            onGoalClick = { goal ->
            val intent = Intent(this, ActiveSavingActivity::class.java)
            intent.putExtra("goal_id", goal.id)
            startActivity(intent)
        },
            onGoalCompleted = { goal ->
                showGoalCompleteDialog()

                lifecycleScope.launch {
                    val updatedGoal = goal.copy(isCompleted = true)
                    GoalAppDatabase.getDatabase(this@GoalActivity).goalDao().updateGoal(updatedGoal)
                }
            }
        )

        binding.recyclerGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerGoals.adapter = adapter

        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@GoalActivity).goalDao().getAllGoals().collect { goals ->
                adapter.setData(goals)

                // ✅ Disable the remove button if no goals exist
                binding.btnremove.isEnabled = goals.isNotEmpty()
                binding.btnremove.alpha = if (goals.isNotEmpty()) 1.0f else 0.4f
            }
        }


        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@GoalActivity)
                .goalDao()
                .getTotalSaved()
                .collect { total ->
                    val amount = total ?: 0.0
                    binding.savedbalance.text = "R %.2f".format(amount)
                }
        }
        //remove//
        binding.btnremove.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Remove All Goals?")
                .setMessage("Are you sure you want to delete all your saved goals and their transactions?")
                .setPositiveButton("Remove") { _, _ ->
                    lifecycleScope.launch {
                        val db = GoalAppDatabase.getDatabase(this@GoalActivity)
                        db.goalTransactionDao().deleteAllGoalTransactions()
                        db.goalDao().deleteAllGoals()
                        Toast.makeText(this@GoalActivity, "All goals removed", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


    }

    fun showGoalCompleteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_activty, null)
        val videoView = dialogView.findViewById<VideoView>(R.id.congratsgiff)
        val closeBtn = dialogView.findViewById<Button>(R.id.btnCloseDialog)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val uri = Uri.parse("android.resource://$packageName/${R.raw.congratsgiff}")
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { videoView.start() }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
