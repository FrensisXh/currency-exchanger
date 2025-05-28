package com.example.currencyexchanger

class StandardCommissionPolicy : CommissionAmountPolicy {
    override fun calculateCommissionAmount(transactionCount: Int, amount: Double): Double {
        return when {
            transactionCount < 5 -> 0.0
            transactionCount % 10 == 0 -> 0.0
            amount <= 150.0 -> 0.0
            else -> amount * 0.007
        }
    }
}