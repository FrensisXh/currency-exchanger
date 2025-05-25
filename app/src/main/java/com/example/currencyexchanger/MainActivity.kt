package com.example.currencyexchanger

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencyexchanger.databinding.ActivityMainBinding
import com.example.currencyexchanger.viewmodels.CurrencyViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupExchangeUI()
    }

    private fun setupExchangeUI() {
        binding.btnConvert.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            val fromCurrency = binding.spinnerFrom.selectedItem.toString()
            val toCurrency = binding.spinnerTo.selectedItem.toString()

            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (resultMessage, success) = viewModel.calculateConversion(amount, fromCurrency, toCurrency)

            if (success) {
                showResultDialog(resultMessage)
                updateBalanceViews()
            } else {
                Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBalanceViews() {
        TODO("Not yet implemented")
    }

    private fun showResultDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Currency converted")
            .setMessage(message)
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}