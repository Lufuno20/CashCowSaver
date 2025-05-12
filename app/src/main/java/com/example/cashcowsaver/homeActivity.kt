package com.example.cashcowsaver


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var navDrawer: LinearLayout
    private lateinit var overlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        //click listeners fro the income and expenses//
        val incomeLayout = findViewById<LinearLayout>(R.id.addIncome)
        val expenseLayout = findViewById<LinearLayout>(R.id.addExpense)

        //nav//
        navDrawer = findViewById(R.id.navView)
        val profileIcon = findViewById<ImageView>(R.id.profile_pic)
        navDrawer = findViewById(R.id.navView)

        profileIcon.setOnClickListener {
            showDrawer()
        }
        overlay.setOnClickListener {
            hideDrawer()
        }

        setNavClickListeners()


        //set item listeners//
        findViewById<LinearLayout>(R.id.home_tab).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        /* findViewById<LinearLayout>(R.id.savings_tab).setOnClickListener {
             startActivity(Intent(this, SavingsActivity::class.java))
         }*/

        findViewById<LinearLayout>(R.id.transact_tab).setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        /*  findViewById<LinearLayout>(R.id.report_tab).setOnClickListener {
              startActivity(Intent(this, ReportActivity::class.java))
          }

          findViewById<LinearLayout>(R.id.history_tab).setOnClickListener {
              startActivity(Intent(this, HistoryActivity))
          }

          findViewById<LinearLayout>(R.id.setting_tab).setOnClickListener {
              startActivity(Intent(this, SettingActivity))
          }

          findViewById<LinearLayout>(R.id.logoff_tab).setOnClickListener {
              startActivity(Intent(this, LogOffActivity))
          }
          */



        incomeLayout.setOnClickListener() {
            val intent = Intent(this, TransactionActivity::class.java)
            intent.putExtra("transaction_type", "income") // Pass "income" type
            startActivity(intent)
            finish()
        }

        expenseLayout.setOnClickListener() {
            val intent = Intent(this, TransactionActivity::class.java)
            intent.putExtra("transaction_type", "expense") // Pass "expense" type
            startActivity(intent)
            finish()
        }

    }

    private fun showDrawer() {
        navDrawer.animate().translationX(-navDrawer.width.toFloat()).setDuration(300).start()
        overlay.visibility = View.VISIBLE
    }

    private fun hideDrawer() {
        navDrawer.animate().translationX(-navDrawer.width.toFloat()).setDuration(300).start()
        overlay.visibility = View.GONE
    }

    private fun setNavClickListeners() {
        val linkMap = mapOf(
            R.id.home_tab to MainActivity::class.java,
            //  R.id.savings_tab to SavingsActivity::class.java,
            R.id.transact_tab to TransactionActivity::class.java,
            //   R.id.history_tab to HistoryActivity::class.java,
            /*   R.id.report_tab to ReportActivity::class.java,
               R.id.setting_tab to SettingsActivity::class.java,
               R.id.logoff LogOffActivity::class.java*/
        )

        linkMap.forEach { (id, activityClass) ->
            findViewById<TextView>(id).setOnClickListener {
                startActivity(Intent(this, activityClass))
                hideDrawer()
            }
        }
    }
}




