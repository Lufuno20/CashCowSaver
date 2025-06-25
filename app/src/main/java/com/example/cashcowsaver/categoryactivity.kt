package com.example.cashcowsaver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt


class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)



        //house//
        val houseIcon = findViewById<LinearLayout>(R.id.house_tab)
        houseIcon.setOnClickListener {
            returnCategory(R.drawable.baseline_house_24, ("#2DA9F6".toColorInt()), "House")
        }

        //school//
        val schoolIcon = findViewById<LinearLayout>(R.id.school_tab)
        schoolIcon.setOnClickListener {
            returnCategory(R.drawable.outline_apartment_24, "#846501".toColorInt(), "School")
        }

        //car//
        val carIcon = findViewById<LinearLayout>(R.id.car_tab)
        carIcon.setOnClickListener {
            returnCategory(R.drawable.outline_directions_car_24, "#DB81DB".toColorInt(), "Car")
        }

        //retail//
        val storeIcon = findViewById<LinearLayout>(R.id.retail_tab)
        storeIcon.setOnClickListener {
            returnCategory(R.drawable.outline_shopping_bag_24, "#FF92A6".toColorInt(), "Retail")
        }

        //health//
        val healthIcon = findViewById<LinearLayout>(R.id.health_tab)
        healthIcon.setOnClickListener {
            returnCategory(R.drawable.outline_health_metrics_24, "#5B0702".toColorInt(), "Health")
        }

        //food//
        val foodIcon = findViewById<LinearLayout>(R.id.food_tab)
        foodIcon.setOnClickListener {
            returnCategory(R.drawable.outline_fastfood_24, "#34C759".toColorInt(), "Food")
        }

        //furniture//
        val furnitureIcon = findViewById<LinearLayout>(R.id.furniture_tab)
        furnitureIcon.setOnClickListener {
            returnCategory(R.drawable.outline_chair_24, "#8E8E93".toColorInt(), "Furniture")
        }

        //drinks//
        val drinksIcon = findViewById<LinearLayout>(R.id.drinks_tab)
        drinksIcon.setOnClickListener {
            returnCategory(R.drawable.outline_glass_cup_24, "#0502D4".toColorInt(), "Drinks")
        }

        //electronics//
        val electronicIcon = findViewById<LinearLayout>(R.id.technology_tab)
        electronicIcon.setOnClickListener {
            returnCategory(R.drawable.outline_connected_tv_24, "#A2845E".toColorInt(), "Electronic")
        }

        //bus//
        val transportIcon = findViewById<LinearLayout>(R.id.transport_tab)
        transportIcon.setOnClickListener {
            returnCategory(
                R.drawable.outline_directions_bus_24,
                "#846501".toColorInt(),
                "Transport"
            )
        }

        //entertainment//
        val entertainmentIcon = findViewById<LinearLayout>(R.id.entertain_tab)
        entertainmentIcon.setOnClickListener {
            returnCategory(R.drawable.outline_comedy_mask_24, "#F91642".toColorInt(), "Entertain")
        }

        //travel//
        val travelIcon = findViewById<LinearLayout>(R.id.travel_tab)
        travelIcon.setOnClickListener {
            returnCategory(
                R.drawable.outline_travel_luggage_and_bags_24,
                "#808080".toColorInt(),
                "Travel"
            )
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

        //salary//
        val salaryIcon = findViewById<LinearLayout>(R.id.salary_tab)
        salaryIcon.setOnClickListener {
            returnCategory(
                R.drawable.outline_account_balance_wallet_24,
                "#008000".toColorInt(),
                "Salary"
            )
        }

        //grocery//
        val groceryIcon = findViewById<LinearLayout>(R.id.grocery_tab)
        groceryIcon.setOnClickListener {
            returnCategory(R.drawable.outline_shopping_cart_24, "#EBD280".toColorInt(), "Grocery")
        }

        //savings//
        val savingsIcon = findViewById<LinearLayout>(R.id.savings_tab)
        savingsIcon.setOnClickListener {
            returnCategory(R.drawable.outline_savings_24, "#FF92A6".toColorInt(), "Savings")
        }

    }

    private fun returnCategory(iconResId: Int, color: Int, categoryName: String) {
        val resultIntent = Intent()
        resultIntent.putExtra(TransactionActivity.EXTRA_ICON_RES_ID, iconResId)
        resultIntent.putExtra(TransactionActivity.EXTRA_ICON_COLOR, color)
        resultIntent.putExtra(TransactionActivity.EXTRA_CATEGORY_NAME, categoryName)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


}