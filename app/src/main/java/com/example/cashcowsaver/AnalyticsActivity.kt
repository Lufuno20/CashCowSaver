package com.example.cashcowsaver

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.databinding.ActivityAnalyticsBinding
import com.example.cashcowsaver.models.TransactionEntity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        //Back button (custom ImageView)

            binding.backArrow2.setOnClickListener {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        lifecycleScope.launch {
            db.transactionDao().getAll().collect { transactions ->
                setupBarChart(transactions)
                setupPieChart(transactions)
            }
        }
    }

    private fun setupBarChart(transactions: List<TransactionEntity>) {
        val incomeTotal = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expenseTotal = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

        val entries = listOf(
            BarEntry(0f, incomeTotal.toFloat()),
            BarEntry(1f, expenseTotal.toFloat())
        )

        val dataSet = BarDataSet(entries, "")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        val barData = BarData(dataSet)
        barData.setValueTextSize(15f)

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Income", "Expense"))
            xAxis.granularity = 1f
            axisRight.isEnabled = false
            description.isEnabled = false
            animateY(2000)
            invalidate()
        }
    }

    private fun setupPieChart(transactions: List<TransactionEntity>) {
        val expenses = transactions.filter { it.type == "Expense" }

        val categoryTotals = mutableMapOf<String, Float>()
        expenses.forEach { txn ->
            categoryTotals[txn.category] = (categoryTotals[txn.category] ?: 0f) + txn.amount.toFloat()
        }


        val total = categoryTotals.values.sum()

        val entries = categoryTotals.map { (category, value) ->
            PieEntry((value / total) * 100, category)
        }

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        pieDataSet.valueFormatter = PercentFormatter()
        pieDataSet.valueTextSize = 18f

        binding.pieChart.apply {
            data = PieData(pieDataSet)
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            animateY(1000)
            invalidate()
        }
    }
}

