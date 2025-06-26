package com.example.cashcowsaver

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashcowsaver.adaptors.GoalAdapter
import com.example.cashcowsaver.database.GoalAppDatabase
import com.example.cashcowsaver.databinding.GoalsActivityBinding
import kotlinx.coroutines.launch

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: GoalsActivityBinding
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goals_activity)
        binding = GoalsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.creategoals.setOnClickListener {
            val intent = Intent(this, CreateGoalActivity::class.java)
            startActivity(intent)
        }
        adapter = GoalAdapter()
        binding.recyclerGoals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerGoals.adapter = adapter

        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@GoalActivity).goalDao().getAllGoals().collect {
                adapter.updateData(it)
            }
        }
    }
}