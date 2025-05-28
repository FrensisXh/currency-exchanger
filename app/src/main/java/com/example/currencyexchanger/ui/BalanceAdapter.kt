package com.example.currencyexchanger.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchanger.data.model.BalanceItem
import com.example.currencyexchanger.databinding.BalanceItemBinding

class BalanceAdapter : ListAdapter<BalanceItem, BalanceAdapter.BalanceViewHolder>(DiffCallBack()) {
    class BalanceViewHolder(private val binding: BalanceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BalanceItem) {
            binding.currencyName.text = item.currencyCode
            binding.amount.text = "%.2f".format(item.amount)
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<BalanceItem>() {
        override fun areItemsTheSame(oldItem: BalanceItem, newItem: BalanceItem) =
            oldItem.currencyCode == newItem.currencyCode

        override fun areContentsTheSame(oldItem: BalanceItem, newItem: BalanceItem) =
            oldItem == newItem

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BalanceViewHolder {
        val binding = BalanceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BalanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}