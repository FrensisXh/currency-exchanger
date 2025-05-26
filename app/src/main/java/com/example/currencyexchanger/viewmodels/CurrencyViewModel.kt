package com.example.currencyexchanger.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchanger.data.model.CurrencyResponse
import com.example.currencyexchanger.data.repository.CurrencyRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map


class CurrencyViewModel : ViewModel() {
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


    //track balances
    private var balances = mutableMapOf(
        "EUR" to 1000.0,
        "USD" to 0.0,
        "BGN" to 0.0
    )

    private var transactionCount = 0

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

    fun calculateConversion(
        amount: Double,
        from: String,
        to: String
    ): Pair<String, Boolean> {
        //extract the rate and handle nulls
        val rate = _rates.value?.rates?.get(to) ?: return Pair("Rate is N/A", false)
        val commissionFee = if (transactionCount >= 5) amount * 0.007 else 0.0
        val totalDeducted = amount + commissionFee

        if ((balances[from] ?: 0.0) < totalDeducted) {
            return Pair("Insufficient balance", false)
        }

        val convertedAmount = amount * rate

        // update the balances
        balances[from] = (balances[from] ?: 0.0) - totalDeducted
        balances[to] = (balances[to] ?: 0.0) + convertedAmount

        transactionCount++

        val msg = if (commissionFee > 0)
            "You have converted $amount $from to %.2f $to. Commission Fee - %.2f $from.".format(
                convertedAmount, commissionFee
            )
        else
            "You have converted $amount $from to %.2f $to.".format(convertedAmount)

        return Pair(msg, true)
    }

    fun getBalance(currency: String): Double {
        return balances[currency] ?: 0.0
    }

    fun getRate(to: String): Double? {
        return _rates.value?.rates?.get(to)
    }

}