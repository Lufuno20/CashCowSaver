package com.example.cashcowsaver


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.example.cashcowsaver.adaptors.TransactionAdapter
import com.example.cashcowsaver.databinding.HomePageBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.models.TransactionEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    //transaction view//
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase

    private lateinit var binding: HomePageBinding
    private lateinit var adapter: TransactionAdapter


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addIncome.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            intent.putExtra("transaction_type", "Income") // Pass "income" type
            startActivity(intent)
        }

        binding.addExpense.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            intent.putExtra("transaction_type", "Expense") // Pass "expense" type
            startActivity(intent)
        }

        binding.seegraph.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }
        //transaction//

        //filter//



        // Setup buttons

        // Observe filtered transactions
      
        // Create an instance of the adapter
        adapter = TransactionAdapter()

        // Set adapter and layout manager
        binding.transactionview.adapter = adapter
        binding.transactionview.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this)


        lifecycleScope.launch {
            db.transactionDao().getAll().collect { transactions ->
                adapter.setData(transactions)
                updateBalance(transactions) //THIS MUST BE CALLED
            }
        }
        // UI-only clear
        binding.btnclear.setOnClickListener {
            adapter.setData(emptyList())
            updateBalance(emptyList())
        }

    }
    override fun onResume() {
        super.onResume()

        // Observe Room database when activity resumes
        lifecycleScope.launch {
            db.transactionDao().getAll().collectLatest { transactions ->
                adapter.setData(transactions)
                updateBalance(transactions)
            }
        }
    }

    private fun updateBalance(transactions: List<TransactionEntity>) {
        transactions.forEach {
            Log.d("DEBUG_TXN", "Type: ${it.type}, Amount: ${it.amount}")
        }

        val income = transactions.filter { it.type.equals("Income", true) }.sumOf { it.amount }
        val expense = transactions.filter { it.type.equals("Expense", true) }.sumOf { it.amount }
        val balance = income - expense

        val formatted = "R %.2f".format(kotlin.math.abs(balance))

        if (balance > 0) {
            binding.totalbalance.text = "$formatted"
            binding.totalbalance.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_light
                )
            )

        } else {
            binding.totalbalance.text = "-$formatted"
            binding.totalbalance.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_red_dark
                )
            )

        }
        Log.d("BALANCE_FINAL", "Income=$income, Expense=$expense, Balance=$balance")

    }


}




