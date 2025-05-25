package com.example.currencyexchanger.data.repository

import com.example.currencyexchanger.data.api.RetrofitInstance
import com.example.currencyexchanger.data.model.CurrencyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository {

    suspend fun getExchangeRates(): CurrencyResponse {
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getExchangeRates()
        }
    }
}