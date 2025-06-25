package com.example.cashcowsaver

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashcowsaver.TransactionActivity.Companion.CATEGORY_REQUEST_CODE
import com.example.cashcowsaver.databinding.CreateGoalActivityBinding
import com.example.cashcowsaver.models.GoalEntity
import com.example.cashcowsaver.database.GoalAppDatabase
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateGoalActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ICON_RES_ID = "extra_icon_res_id"
        const val EXTRA_ICON_COLOR = "extra_icon_color"
        const val EXTRA_CATEGORY_NAME = "extra_category_name"
        const val CATEGORY_REQUEST_CODE = 1001
    }

    private lateinit var binding: CreateGoalActivityBinding
    private var selectedIcon: Int = 0
    private var selectedDate: String = ""

    //date//
    private lateinit var setDateText: TextView
    private val calendar = Calendar.getInstance()
    private lateinit var dateTextView: TextView

    //category//
    private lateinit var selectedCategoryIcon: ImageView
    private lateinit var selectedCategoryText: TextView
    private lateinit var selectedCategoryLayout: LinearLayout
    private var selectedCategory: String = ""

    //amount//
    private lateinit var enterAmountEditText: EditText
    private var isFormatting = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateGoalActivityBinding.inflate(layoutInflater)
        setContentView(R.layout.create_goal_activity)
        setContentView(binding.root)

        //date picker//
        setDateText = findViewById(R.id.txtdateInput)
        dateTextView = findViewById(R.id.txtdateInput)

        setDateText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Enter Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                calendar.timeInMillis = selection
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                setDateText.text = formattedDate
            }

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }
        //set date picker//
        dateTextView.setOnClickListener {
            showDatePicker()
        }
        //amount//
        enterAmountEditText = findViewById(R.id.goalamount)

        enterAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true

                val cleanString = s.toString()
                    .replace("R", "")
                    .replace(" ", "")
                    .replace(",", "")
                    .replace(".", "")

                if (cleanString.isNotEmpty()) {
                    try {
                        val parsed = cleanString.toDouble() / 100
                        val formatted = "R %.2f".format(parsed)
                        enterAmountEditText.setText(formatted)
                        enterAmountEditText.setSelection(enterAmountEditText.text.length)
                    } catch (e: Exception) {
                        enterAmountEditText.setText("")
                    }
                }

                isFormatting = false
            }
        })

        //create button//
        binding.btncreate.setOnClickListener {
            val title = binding.txtgoals.text.toString()
            val amountText = binding.goalamount.text.toString()
                .replace("R", "")
                .replace(",", "")
                .trim()
            val amount = amountText.toDoubleOrNull()

            if (title.isEmpty() || amount == null || selectedDate.isEmpty() || selectedCategory.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = GoalEntity(
                title = title,
                category = selectedCategory,
                amount = amount,
                iconResId = selectedIcon,
                targetDate = selectedDate
            )

            lifecycleScope.launch {
                GoalAppDatabase.getDatabase(this@CreateGoalActivity).goalDao().insert(goal)
                finish()
            }

            //category//
            selectedCategoryIcon = findViewById(R.id.imgiconInput)
            selectedCategoryText = findViewById(R.id.txticonInput)
            selectedCategoryLayout = findViewById(R.id.categoryselect)

            selectedCategoryLayout.setOnClickListener {
                val intent = Intent(this, IconActivity::class.java)
                startActivityForResult(intent, CATEGORY_REQUEST_CODE)
                //set category selection from linear layout//
                setupCategorySelection()
            }
            //category//
        }


    }

    private fun setupCategorySelection() {
        val categoryMap = mapOf(
            R.id.house_tab to Pair("House", R.drawable.baseline_house_24),
            R.id.motor_tab to Pair("car", R.drawable.outline_directions_car_24),
            R.id.traveling_tab to Pair("traveling", R.drawable.outline_travel_24),
            R.id.shopping_tab to Pair("shopping", R.drawable.outline_apparel_24),
            R.id.renovate_tab to Pair("renovate", R.drawable.baseline_chair_24),
            R.id.payment_tab to Pair("payment", R.drawable.outline_payment_arrow_down_24),
            R.id.savings_tab to Pair("savings", R.drawable.outline_savings_24),
            R.id.business_tab to Pair("business", R.drawable.outline_business_center_24),


            )
        for ((viewId, value) in categoryMap) {
            val categoryView = findViewById<LinearLayout>(R.id.categoryselect)
            categoryView.setOnClickListener {
                selectedCategory = value.first
                selectedIcon = value.second
                clearAllHighlights(categoryMap.keys)
                categoryView.setBackgroundResource(R.drawable.bg_selected)
            }
        }

    }

    private fun clearAllHighlights(viewIds: Set<Int>) {
        for (id in viewIds) {
            findViewById<LinearLayout>(id).setBackgroundResource(android.R.color.transparent)
        }
    }

    //date picker display//
    private fun showDatePicker() {
        val cal = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

            // Format: 08 June 2025
            val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            selectedDate = formatter.format(calendar.time)

            binding.txtdateInput.text = selectedDate
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }


}
