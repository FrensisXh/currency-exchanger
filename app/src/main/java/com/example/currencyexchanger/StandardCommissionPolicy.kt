package com.example.currencyexchanger

 class StandardCommissionPolicy: CommissionAmountPolicy {
     override fun calculateCommissionAmount(transactionCount: Int, amount: Double): Double {
         return if (transactionCount >= 5) amount * 0.007 else 0.0
     }
}