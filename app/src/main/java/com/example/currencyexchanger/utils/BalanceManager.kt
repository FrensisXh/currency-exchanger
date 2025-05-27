package com.example.currencyexchanger.utils

import android.content.Context
import org.json.JSONObject

class BalanceManager(context: Context) {
    private val prefs = context.getSharedPreferences("balances", Context.MODE_PRIVATE)

    fun saveBalances(balances: Map<String, Double>) {
        val json = JSONObject()
        for ((currency, amount) in balances) {
            json.put(currency, amount)
        }
        prefs.edit().putString("balance_json", json.toString()).apply()
    }

    fun loadBalances(): MutableMap<String, Double> {
        val jsonString = prefs.getString("balance_json", null) ?: return defaultBalances()

        return try {
            val json = JSONObject(jsonString)
            val result = mutableMapOf<String, Double>()
            val keys = json.keys().asSequence().toList()
            keys.forEach { key ->
                result[key] = json.getDouble(key)
            }
            result
        } catch (e: Exception) {
            defaultBalances()
        }
    }

    private fun defaultBalances(): MutableMap<String, Double> {
        return mutableMapOf(
            "EUR" to 1000.0,
            "USD" to 0.0,
            "BGN" to 0.0
        )
    }

    fun saveTransactionCount(count: Int) {
        prefs.edit().putInt("transaction_count", count).apply()
    }

    fun loadTransactionCount(): Int {
        return prefs.getInt("transaction_count", 0)
    }
}