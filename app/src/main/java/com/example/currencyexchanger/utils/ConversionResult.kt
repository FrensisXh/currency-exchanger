package com.example.currencyexchanger.utils

// to represent the result of a currency conversion attempt
sealed class ConversionResult {
    data class Success(val message: String) : ConversionResult()
    data class Error(val message: String) : ConversionResult()
}
