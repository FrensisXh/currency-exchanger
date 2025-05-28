package com.example.currencyexchanger

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchanger.data.model.BalanceItem
import com.example.currencyexchanger.databinding.ActivityMainBinding
import com.example.currencyexchanger.ui.BalanceAdapter
import com.example.currencyexchanger.viewmodels.CurrencyViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private lateinit var balanceAdapter: BalanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the status bar color
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        // Set light icons in the status bar
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setupExchangeUI()
        setupBalanceRecycler()
    }

    private fun setupExchangeUI() {
        // Observe the list of available currency codes and populate spinners
        viewModel.currencyCodesLiveData.observe(this) { codes ->
            if (codes.isEmpty()) return@observe

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                codes
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            binding.spinnerFrom.adapter = adapter
            binding.spinnerTo.adapter = adapter

            // Set default selections
            binding.spinnerFrom.setSelection(codes.indexOf("EUR"))
            binding.spinnerTo.setSelection(codes.indexOf("USD"))
            // Trigger preview once spinners are populated
            updateConvertedAmountPreview()
        }

        binding.btnConvert.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            val fromCurrency = binding.spinnerFrom.selectedItem.toString()
            val toCurrency = binding.spinnerTo.selectedItem.toString()

            if (fromCurrency == toCurrency) {
                Toast.makeText(this, "Please, select two different currencies", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (amount == null || fromCurrency.isBlank() || toCurrency.isBlank()) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (resultMessage, success) = viewModel.calculateConversion(
                amount,
                fromCurrency,
                toCurrency
            )

            if (success) {
                showResultDialog(resultMessage)
                binding.etAmount.text?.clear()
                binding.tvConvertedAmount.text = ""
                updateBalanceList()
            } else {
                Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Live preview of receive amount
        binding.etAmount.addTextChangedListener {
            updateConvertedAmountPreview()
        }

        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateConvertedAmountPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateConvertedAmountPreview()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupBalanceRecycler() {
        balanceAdapter = BalanceAdapter()
        binding.balancesRv.adapter = balanceAdapter
        binding.balancesRv.layoutManager = LinearLayoutManager(this)
        updateBalanceList()
    }

    private fun updateBalanceList() {
        val balances = viewModel.getAllBalances().entries.map { BalanceItem(it.key, it.value) }
        balanceAdapter.submitList(balances)
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

    private fun updateConvertedAmountPreview() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: return
        val toCurrency = binding.spinnerTo.selectedItem?.toString() ?: return
        val rate = viewModel.getRate(toCurrency) ?: return

        val converted = amount * rate
        val formatted = "+%.2f".format(converted)
        binding.tvConvertedAmount.text = formatted
    }
}