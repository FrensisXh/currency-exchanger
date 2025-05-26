package com.example.currencyexchanger

interface CommissionAmountPolicy {
    fun calculateCommissionAmount(transactionCount: Int, amount: Double): Double
}