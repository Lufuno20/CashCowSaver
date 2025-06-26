package com.example.cashcowsaver

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.cashcowsaver.databinding.CalculatordialogBinding
import java.text.NumberFormat
import java.util.*

class CalculatorDialog(
    context: Context,
    private val onAmountEntered: (Double) -> Unit
) : Dialog(context) {

    private lateinit var binding: CalculatordialogBinding
    private var input = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = CalculatordialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {
        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                input += index.toString()
                updateDisplay()
            }
        }

        binding.btnDot.setOnClickListener {
            if (!input.contains(".")) {
                input += "."
                updateDisplay()
            }
        }

        binding.btnClear.setOnClickListener {
            input = ""
            updateDisplay()
        }

        binding.btnBack.setOnClickListener {
            if (input.isNotEmpty()) {
                input = input.dropLast(1)
                updateDisplay()
            }
        }

        binding.btnConfirm.setOnClickListener {
            val amount = input.toDoubleOrNull()
            if (amount != null && amount > 0) {
                onAmountEntered(amount)
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateDisplay() {
        val formatted = input.toDoubleOrNull()?.let {
            NumberFormat.getCurrencyInstance(Locale("en", "ZA")).format(it)
        } ?: "R0.00"
        binding.calculatorDisplay.text = formatted
    }
}
