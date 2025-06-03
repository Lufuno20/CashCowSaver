package com.example.cashcowsaver

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.cashcowsaver.models.Transaction

class TransactionActivity : AppCompatActivity() {

    private lateinit var selectedCategoryIcon: ImageView
    private lateinit var selectedCategoryText: TextView
    private lateinit var selectedCategoryLayout: LinearLayout

    companion object {
        const val EXTRA_ICON_RES_ID = "extra_icon_res_id"
        const val EXTRA_ICON_COLOR = "extra_icon_color"
        const val EXTRA_CATEGORY_NAME = "extra_category_name"
        const val CATEGORY_REQUEST_CODE = 1001
    }


    //time and date//
    private lateinit var setDateText: TextView
    private lateinit var setReminderText: TextView
    private val calendar = Calendar.getInstance()

    //calculator//
    private lateinit var enterAmountEditText: EditText
    private var isFormatting = false

    //transaction//
    private var isIncomeSelected = false
    private var isExpenseSelected = true // Default to expense
    private lateinit var dateTextView: TextView
    private var selectedDate: String = ""
    private var selectedCategory: String = ""
    private var selectedIconResId: Int =
        R.drawable.outline_shopping_cart_24 //fallback / default icon//


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_page)
        val income = findViewById<Button>(R.id.btnincome)
        val expense = findViewById<Button>(R.id.btnexpense)
        val transactionType = intent.getStringExtra("transaction_type")
        enterAmountEditText = findViewById(R.id.amount_input)

        //DATE AND TIME//
        setDateText = findViewById(R.id.txtsetdate)
        setReminderText = findViewById(R.id.txtreminder)

        //date picker//
        setDateText.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                calendar.timeInMillis = selection
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
                setDateText.text = formattedDate
            }

            datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

        //TIME PICKER//
        setReminderText.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .setTitleText("Select Time")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                //set selected time to calendar and format it//
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)
                val formattedTime = SimpleDateFormat("hh:mm a", Locale.US).format(calendar.time)
                setReminderText.text = formattedTime
            }

            timePicker.show(supportFragmentManager, "MATERIAL_TIME_PICKER")
        }

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
        //category//
        selectedCategoryIcon = findViewById(R.id.categoryimg)
        selectedCategoryText = findViewById(R.id.txtcategory)
        selectedCategoryLayout = findViewById(R.id.select_category)

        selectedCategoryLayout.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivityForResult(intent, CATEGORY_REQUEST_CODE)
        }
        //category//

//income & expense//
        // Highlight selected button
        if (transactionType == "income") {
            highlightButton(income)
            unhighlightButton(expense)
        } else if (transactionType == "expense") {
            highlightButton(expense)
            unhighlightButton(income)
        }
//transaction//
        val incomebtn = findViewById<Button>(R.id.btnincome)
        val expensebtn = findViewById<Button>(R.id.btnexpense)
        val savebtn = findViewById<TextView>(R.id.save_button)
        val amountedit = findViewById<EditText>(R.id.amount_input)
        val noteditText = findViewById<EditText>(R.id.note_input)
        dateTextView = findViewById(R.id.txtsetdate)

        //set category selection from linearlayouyt//
        setupCategorySelection()

        //set date picker//
        dateTextView.setOnClickListener {
            showDatePicker()
        }

        incomebtn.setOnClickListener {
            isIncomeSelected = true
            isExpenseSelected = false
        }
        expensebtn.setOnClickListener {
            isIncomeSelected = false
            isExpenseSelected = true
        }

        savebtn.setOnClickListener {
            val amount = amountedit.text.toString().toDoubleOrNull() ?: 0.0
            val note = noteditText.text.toString()
            val type = if (isIncomeSelected) "income" else "expense"

            if (selectedDate.isBlank() || selectedCategory.isBlank()) {
                Toast.makeText(this, "please enter select category and date", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                category = selectedCategory,
                description = note,
                amount = amount,
                date = selectedDate,
                iconResId = selectedIconResId,
                type = type
            )

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("transaction", transaction)
            startActivity(intent)
            finish()
        }
    }

    //setup for the category transaction//
    private fun setupCategorySelection() {
        val categoryMap = mapOf(
            R.id.grocery_tab to Pair("Groceries", R.drawable.outline_shopping_cart_24),
            R.id.house_tab to Pair("House", R.drawable.baseline_house_24),
            R.id.school_tab to Pair("school", R.drawable.outline_apartment_24),
            R.id.car_tab to Pair("car", R.drawable.outline_directions_car_24),
            R.id.retail_tab to Pair("retail", R.drawable.outline_shopping_bag_24),
            R.id.food_tab to Pair("food", R.drawable.outline_fastfood_24),
            R.id.savings_tab to Pair("savings", R.drawable.outline_savings_24),
            R.id.business_tab to Pair("business", R.drawable.outline_business_center_24),
            R.id.travel_tab to Pair("travel", R.drawable.outline_travel_luggage_and_bags_24),
            R.id.transport_tab to Pair("transport", R.drawable.outline_directions_bus_24),
            R.id.health_tab to Pair("health", R.drawable.outline_health_metrics_24),
            R.id.furniture_tab to Pair("furniture", R.drawable.outline_chair_24),
            R.id.drinks_tab to Pair("drinks", R.drawable.outline_glass_cup_24),
            R.id.technology_tab to Pair("electronic", R.drawable.outline_connected_tv_24),
            R.id.subscription_tab to Pair("subscription", R.drawable.outline_subscriptions_24),
            R.id.salary_tab to Pair("salary", R.drawable.outline_account_balance_wallet_24),
            R.id.entertain_tab to Pair("entertain", R.drawable.outline_comedy_mask_24)

        )
        for((viewId, value) in categoryMap){
            val categoryView = findViewById<LinearLayout>(viewId)
            categoryView.setOnClickListener {
                selectedCategory = value.first
                selectedIconResId = value.second
                clearAllHighlights(categoryMap.keys)
                categoryView.setBackgroundResource(R.drawable.bg_selected)
            }
        }

    }

    private fun clearAllHighlights(viewIds: Set<Int>){
        for(id in viewIds){
            findViewById<LinearLayout>(id).setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate ="${getMonthName(selectedMonth)} $selectedDay, $selectedYear"
                dateTextView.text = selectedDate

            }, year, month, day)
        datePickerDialog.show()
    }
    //this will display the months//
    private fun getMonthName(month: Int): String{
        return arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )[month]
    }


    //function to highlight the selected button//
    private fun highlightButton(button: Button) {
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.green_dark))
        button.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    //function to reset other button to default color//
    private fun unhighlightButton(button: Button) {
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.green_light))
        button.setTextColor(ContextCompat.getColor(this, R.color.green_dark))
    }

    //category//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK) {
            val iconResId = data?.getIntExtra(EXTRA_ICON_RES_ID, 0) ?: 0
            val color = data?.getIntExtra(EXTRA_ICON_COLOR, Color.BLACK)
            val categoryName = data?.getStringExtra(EXTRA_CATEGORY_NAME) ?: "Category"

            // Display on the Select Category section (icon + text + color)
            selectedCategoryIcon.setImageResource(iconResId)
            selectedCategoryIcon.setColorFilter(color?.toInt() ?: 0)
            selectedCategoryText.text = categoryName
        }
    }


}









