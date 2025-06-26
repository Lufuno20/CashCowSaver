package com.example.cashcowsaver

import GoalAdapter
import android.content.Intent
import android.os.Bundle
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
        adapter = GoalAdapter { goal ->
            val intent = Intent(this, ActiveSavingActivity::class.java)
            intent.putExtra("goal_id", goal.id)
            startActivity(intent)
        }

        binding.recyclerGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerGoals.adapter = adapter

        // Observe Room DB
        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@GoalActivity)
                .goalDao()
                .getAllGoals()
                .collect { goals ->
                    adapter.setData(goals)
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


    }
}
