package com.example.currencyexchanger.data.api

import com.example.currencyexchanger.data.model.CurrencyResponse
import retrofit2.http.GET

interface ExchangeRateApi {
    @GET("currency-exchange-rates")
    suspend fun getExchangeRates(): CurrencyResponse
}