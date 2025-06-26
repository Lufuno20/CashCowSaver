package com.example.cashcowsaver

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashcowsaver.database.AppDatabase
import com.example.cashcowsaver.databinding.TransactionPageBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.cashcowsaver.models.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch

class TransactionActivity : AppCompatActivity() {
    private lateinit var navDrawer: View

    private lateinit var binding: TransactionPageBinding

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
    private lateinit var db: AppDatabase
    private var selectedDate: String = ""
    private var selectedCategory: String = ""
    private var selectedIconResId: Int = 0
    private var selectedType: String = "Income" // Default to income


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_page)
        binding = TransactionPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val income = findViewById<Button>(R.id.btnincome)
        val expense = findViewById<Button>(R.id.btnexpense)
        val investment = findViewById<Button>(R.id.btninvestment)
        db = AppDatabase.getDatabase(this)

        // Make sure you are referencing the full included drawer view, not the ScrollView
        navDrawer = findViewById(R.id.navView)

// Click on profile icon to show nav
        val profilePic = binding.imgprofile // Use binding if in your layout
        profilePic.setOnClickListener {
            showNavigationDrawer()
        }
        // Optional: Close drawer if background is tapped
        navDrawer.setOnClickListener {
            hideNavigationDrawer()
        }

        // When background or Log Off is clicked
        val logOff = navDrawer.findViewById<LinearLayout>(R.id.logoff)
        logOff.setOnClickListener {
            hideNavigationDrawer()
        }


        // Access nav tabs
        val homeTab = navDrawer.findViewById<LinearLayout>(R.id.home_tab)
        val savingsTab = navDrawer.findViewById<LinearLayout>(R.id.savings_tab)
        val transactTab = navDrawer.findViewById<LinearLayout>(R.id.transact_tab)
        val reportTab = navDrawer.findViewById<LinearLayout>(R.id.report_tab)
        val logOffTab = navDrawer.findViewById<LinearLayout>(R.id.logoff)

        // Each tab click → navigate
        homeTab.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            hideNavigationDrawer()
        }

        savingsTab.setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
            hideNavigationDrawer()
        }

        transactTab.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
            hideNavigationDrawer()
        }

        reportTab.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
            hideNavigationDrawer()
        }

        logOffTab.setOnClickListener {
            // TODO: Clear user data/session
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //investment button//
        investment.setOnClickListener {
            val intent = Intent(this, GoalActivity::class.java)
            startActivity(intent)
        }
        //Back button (custom ImageView)
        binding.backArrow.setOnClickListener {
            handleBackPress()
        }
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
            //set category selection from linear layout//
            setupCategorySelection()
        }
        //category//

//income & expense//
        // Highlight selected button
        if (transactionType == "Income") {
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


        //set date picker//
        dateTextView.setOnClickListener {
            showDatePicker()
        }

        binding.btnincome.setOnClickListener {
            selectedType = "Income"
            highlightSelectedType()
        }
        binding.btnexpense.setOnClickListener {
            selectedType = "Expense"
            highlightSelectedType()
        }

        savebtn.setOnClickListener {
            val amountText = binding.amountInput.text.toString()
                .replace("R", "")
                .replace(",", "")
                .trim()
            val amount = amountText.toDoubleOrNull()
            val note = binding.noteInput.text.toString()
            selectedDate = binding.txtsetdate.text.toString()

            if (selectedDate.isEmpty() || amount == null || selectedCategory.isEmpty()) {
                Toast.makeText(this, "please enter select category and date", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val transaction = TransactionEntity(
                    category = selectedCategory,
                    description = note,
                    amount = amount,
                    date = selectedDate,
                    iconResId = selectedIconResId,
                    type = selectedType
                )

                lifecycleScope.launch {
                    db.transactionDao().insert(transaction)
                    val intent = Intent(this@TransactionActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }

            }
        }
        // Default selection highlight
        highlightSelectedType()
    }

    private fun highlightSelectedType() {
        val selectedColor = ContextCompat.getColor(this, R.color.green_dark)
        val unselectedColor = ContextCompat.getColor(this, R.color.green_light)
        val letterColor = ContextCompat.getColor(this, R.color.white)
        if (selectedType == "Income") {
            binding.btnincome.setBackgroundColor(selectedColor)
            binding.btnincome.setTextColor(letterColor)
            binding.btnexpense.setBackgroundColor(unselectedColor)

        } else {
            binding.btnexpense.setBackgroundColor(selectedColor)
            binding.btnexpense.setTextColor(letterColor)
            binding.btnincome.setBackgroundColor(unselectedColor)
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
        for ((viewId, value) in categoryMap) {
            val categoryView = findViewById<LinearLayout>(R.id.select_category)
            categoryView.setOnClickListener {
                selectedCategory = value.first
                selectedIconResId = value.second
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

            binding.txtsetdate.text = selectedDate
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
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

        if (requestCode == CATEGORY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedIconResId = data.getIntExtra(EXTRA_ICON_RES_ID, 0)
            val color = data.getIntExtra(EXTRA_ICON_COLOR, Color.BLACK)
            selectedCategory = data.getStringExtra(EXTRA_CATEGORY_NAME) ?: "Category"

            // Display on the Select Category section (icon + text + color)
            binding.categoryimg.setImageResource(selectedIconResId)
            binding.categoryimg.setColorFilter(color.toInt())
            binding.txtcategory.text = selectedCategory
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        handleBackPress()
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        val amountFilled = binding.amountInput.text.toString().isNotEmpty()
        val noteFilled = binding.noteInput.text.toString().isNotEmpty()
        val dateFilled = binding.txtsetdate.text.toString().isNotEmpty()
        val categorySelected = selectedCategory.isNotEmpty()

        val hasUserStartedFilling = amountFilled || noteFilled || dateFilled || categorySelected

        if (hasUserStartedFilling) {
            AlertDialog.Builder(this)
                .setTitle("Discard changes?")
                .setMessage("You have unsaved data. Are you sure you want to discard and go back?")
                .setPositiveButton("Discard") { _, _ ->
                    goBackToHome()
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            goBackToHome()
        }
    }

    private fun goBackToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showNavigationDrawer() {
        navDrawer.visibility = View.VISIBLE
        navDrawer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_in))
    }


    private fun hideNavigationDrawer() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_right_out)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                navDrawer.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        navDrawer.startAnimation(anim)
    }

}










