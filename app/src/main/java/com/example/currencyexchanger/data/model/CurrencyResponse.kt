package com.example.currencyexchanger.data.model

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)