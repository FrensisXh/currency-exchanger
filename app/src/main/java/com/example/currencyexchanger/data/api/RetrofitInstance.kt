package com.example.currencyexchanger.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ExchangeRateApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://developers.paysera.com/tasks/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExchangeRateApi::class.java)
    }
}