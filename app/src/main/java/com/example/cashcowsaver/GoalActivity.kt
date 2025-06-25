package com.example.cashcowsaver

import android.os.Bundle
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
        setContentView(binding.root)

        adapter = GoalAdapter(emptyList())
        binding.recyclerGoals.layoutManager = LinearLayoutManager(this)
        binding.recyclerGoals.adapter = adapter

        lifecycleScope.launch {
            GoalAppDatabase.getDatabase(this@GoalActivity).goalDao().getAll().collect {
                adapter.updateData(it)
            }
        }
    }
}