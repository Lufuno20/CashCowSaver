package com.example.cashcowsaver

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_page)
        val income = findViewById<Button>(R.id.btnincome)
        val expense = findViewById<Button>(R.id.btnexpense)
        val transactionType = intent.getStringExtra("transaction_type")
        enterAmountEditText = findViewById(R.id.amount_input)

        //DATE AND TIME//
        setDateText = findViewById(R.id.set_date)
        setReminderText = findViewById(R.id.set_reminder)

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









