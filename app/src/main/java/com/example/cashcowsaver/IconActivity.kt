package com.example.cashcowsaver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt

class IconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.icon_list_activity)

        //house//
        val houseIcon = findViewById<LinearLayout>(R.id.house_tab)
        houseIcon.setOnClickListener {
            returnCategory(R.drawable.baseline_house_24, ("#2DA9F6".toColorInt()), "House")
        }
        //car//
        val carIcon = findViewById<LinearLayout>(R.id.motor_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.outline_directions_car_24, "#FFBB33".toColorInt(), "Car")
        }

        //traveling//
        val travelIcon = findViewById<LinearLayout>(R.id.traveling_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.outline_travel_24, "#CC4E00FF".toColorInt(), "Traveling")
        }

        //shopping//
        val shoppingIcon = findViewById<LinearLayout>(R.id.shopping_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.outline_apparel_24, "#6CF306".toColorInt(), "Shopping")
        }

        //payment//
        val paymentIcon = findViewById<LinearLayout>(R.id.payment_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.outline_payment_arrow_down_24, "#02FFB7".toColorInt(), "Payment")
        }

        //renovate//
        val renovateIcon = findViewById<LinearLayout>(R.id.renovate_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.baseline_chair_24, "#FF3E3E".toColorInt(), "Renovate")
        }

        //savings//
        val savingsIcon = findViewById<LinearLayout>(R.id.savings_tab)
        savingsIcon.setOnClickListener {
            returnCategory(R.drawable.outline_savings_24, "#FF92A6".toColorInt(), "Savings")
        }

        //business//
        val businessIcon = findViewById<LinearLayout>(R.id.business_tab)
        businessIcon.setOnClickListener {
            returnCategory(
                R.drawable.outline_business_center_24,
                "#DB81DB".toColorInt(),
                "Business"
            )
        }


    }
    private fun returnCategory(iconResId: Int, color: Int, categoryName: String) {
        val resultIntent = Intent()
        resultIntent.putExtra(CreateGoalActivity.EXTRA_ICON_RES_ID, iconResId)
        resultIntent.putExtra(CreateGoalActivity.EXTRA_ICON_COLOR, color)
        resultIntent.putExtra(CreateGoalActivity.EXTRA_CATEGORY_NAME, categoryName)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}