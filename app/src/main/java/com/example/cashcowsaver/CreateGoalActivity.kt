package com.example.cashcowsaver

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashcowsaver.database.GoalAppDatabase
import com.example.cashcowsaver.databinding.CreateGoalActivityBinding
import com.example.cashcowsaver.models.GoalEntity
import com.example.cashcowsaver.models.Icons
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.NumberFormat
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
    private var selectedCategoryName: String = ""
    private var selectedIconResId: Int = 0
    private var selectedColorResId: Int = 0

    private var selectedDate: String = ""
    private var selectedAmount: Double = 0.0

    //calculator//
    private lateinit var enterAmountEditText: EditText
    private var isFormatting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateGoalActivityBinding.inflate(layoutInflater)
        setContentView(R.layout.create_goal_activity)
        setContentView(binding.root)
//val btnBack = findViewById<ImageView>(R.id.btnBack)
        // Handle custom back button
        binding.btnBack.setOnClickListener {
            handleBackWithConfirmation()
        }

        // === 1. Select Date ===
        binding.datepick.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate =
                        String.format("%02d %s %d", dayOfMonth, getMonthName(month), year)
                    binding.txtdateInput.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // === 2. Amount (Open calculator from transaction logic) ===
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
        // === 3. Category selection ===
        binding.categoryselect.setOnClickListener {
            showCategoryDialog()
        }


        // === 4. Save goal to RoomDB ===
        binding.btncreate.setOnClickListener {
            val goalTitle = binding.txtgoals.text.toString().trim()
            val amountText = binding.goalamount.text.toString()
            val amount = amountText.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0

            if (goalTitle.isEmpty() || amount <= 0.0 || selectedCategoryName.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = GoalEntity(
                title = goalTitle,
                amount = amount,
                categoryName = selectedCategoryName,
                iconResId = selectedIconResId,
                colorResId = selectedColorResId,
                date = selectedDate
            )

            lifecycleScope.launch {
                GoalAppDatabase.getDatabase(this@CreateGoalActivity).goalDao().insert(goal)
                startActivity(Intent(this@CreateGoalActivity, GoalActivity::class.java))
                finish()
            }
        }
    }

    private val categoryMap = listOf(
        Icons("House", R.drawable.baseline_house_24, android.R.color.holo_blue_light),
        Icons("car", R.drawable.outline_directions_car_24, android.R.color.holo_orange_light),
        Icons("savings", R.drawable.outline_savings_24, android.R.color.holo_purple),
        Icons("business", R.drawable.outline_business_center_24, android.R.color.holo_purple),
        Icons("renovate", R.drawable.baseline_chair_24, android.R.color.holo_red_light),
        Icons("shopping", R.drawable.outline_shoppingmode_24, android.R.color.holo_green_light),
        Icons(
            "payment",
            R.drawable.outline_payment_arrow_down_24,
            android.R.color.holo_blue_bright
        ),
        Icons("traveling", R.drawable.outline_travel_24, android.R.color.holo_orange_dark),
    )


    // Helper: Convert numeric month to string
    private fun getMonthName(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    private fun showCategoryDialog() {
        val categoryNames = categoryMap.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Category")
            .setItems(categoryNames) { _, index ->
                val selected = categoryMap[index]
                applySelectedCategory(selected)
            }
            .create()
            .show()
    }
    private fun applySelectedCategory(icons: Icons) {
        binding.txticonInput.text = icons.name
        binding.imgiconInput.setImageResource(icons.iconResId)

        // Store for saving
        selectedCategoryName = icons.name
        selectedIconResId = icons.iconResId
        selectedColorResId = icons.colorResId
    }
    // Detect if any fields were filled
    private fun hasUserInput(): Boolean {
        return binding.txtgoals.text.toString().isNotBlank() ||
                selectedAmount > 0.0 ||
                selectedCategoryName.isNotBlank() ||
                selectedDate.isNotBlank()
    }

    // Custom back button and system back button share this logic
    private fun handleBackWithConfirmation() {
        if (hasUserInput()) {
            AlertDialog.Builder(this)
                .setTitle("Discard Goal?")
                .setMessage("You have unsaved changes. Do you want to discard and go back?")
                .setPositiveButton("Discard") { _, _ -> finish() }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            finish()
        }
    }


}
