package com.example.currencyexchanger.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.data.model.CurrencyResponse
import com.example.currencyexchanger.data.repository.CurrencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.example.currencyexchanger.CommissionAmountPolicy
import com.example.currencyexchanger.StandardCommissionPolicy
import com.example.currencyexchanger.utils.BalanceManager
import com.example.currencyexchanger.utils.ConversionResult
import kotlinx.coroutines.flow.map


class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val commissionPolicy: CommissionAmountPolicy = StandardCommissionPolicy()

    private val balanceManager = BalanceManager(application.applicationContext)
    private var balances = balanceManager.loadBalances()


    //to fetch data from the AP
    private val repository = CurrencyRepository()

    // expose currency codes for UI
    private val _rates = MutableStateFlow<CurrencyResponse?>(null)
    val rates: StateFlow<CurrencyResponse?> = _rates

    val currencyCodesLiveData = _rates
        //transforms _rates into a sorted list of currency codes
        .map { it?.rates?.keys?.toList()?.sorted() ?: emptyList() }
        //makes it easy to observe in the Activity with .observe()
        .asLiveData()


    private var transactionCount = balanceManager.loadTransactionCount()

    //start fetching exchange rates every 5 seconds
    init {
        fetchRatesPeriodically()
    }

    private fun fetchRatesPeriodically() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = repository.getExchangeRates()
                    Log.d("CurrencyDebug", "API success — rates: ${response.rates.keys}")
                    _rates.value = response
                } catch (e: Exception) {
                    Log.e("CurrencyDebug", "API error: ${e.localizedMessage}")
                    e.printStackTrace()
                }
                delay(5000L)
            }
        }

    }

    fun calculateConversion(amount: Double, from: String, to: String): ConversionResult {
        if (from == to) {
            return ConversionResult.Error("Please to proceed select two different currencies")
        }

        // get the exchange rate for the target currency
        val rate = _rates.value?.rates?.get(to) ?: return ConversionResult.Error("Rate is N/A")

        // calculate commission fee based on commission policy
        val commissionFee = commissionPolicy.calculateCommissionAmount(transactionCount, amount)
        val totalDeducted = amount + commissionFee

        // to ensure the user has enough balance to make the conversion
        if ((balances[from] ?: 0.0) < totalDeducted) {
            return ConversionResult.Error("Insufficient balance")
        }

        val convertedAmount = amount * rate
        balances[from] = (balances[from] ?: 0.0) - totalDeducted
        balances[to] = (balances[to] ?: 0.0) + convertedAmount

        // save the updated transaction count and balances
        transactionCount++
        balanceManager.saveTransactionCount(transactionCount)
        balanceManager.saveBalances(balances)

        val msg = if (commissionFee > 0)
            "You have converted $amount $from to %.2f $to. Commission Fee - %.2f $from.".format(
                convertedAmount,
                commissionFee
            )
        else
            "You have converted $amount $from to %.2f $to.".format(convertedAmount)

        return ConversionResult.Success(msg)
    }

    fun getAllBalances(): Map<String, Double> = balances

    fun getRate(to: String): Double? {
        return _rates.value?.rates?.get(to)
    }
}