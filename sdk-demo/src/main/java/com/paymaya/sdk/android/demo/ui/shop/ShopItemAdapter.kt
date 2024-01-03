package com.paymaya.sdk.android.demo.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paymaya.sdk.android.demo.Constants.DECIMALS
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.databinding.HolderShopProductBinding
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal

class ShopItemAdapter(
    private val onAddToCartRequestListener: OnAddToCartRequestListener
) : RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder>() {

    private val items: MutableList<ShopItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            HolderShopProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(items[position])
    }

    fun setItems(items: List<ShopItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: HolderShopProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(product: ShopItem) {
            binding.productName.text = product.name
            binding.productAmount.text =
                product.value.setScale(DECIMALS, BigDecimal.ROUND_HALF_DOWN).toString() + " ${product.currency}"
            binding.shopProductContainer.setBackgroundResource(R.drawable.rectangle)
            binding.addToCartButton.setOnClickListener {
                onAddToCartRequestListener.invoke(product)
            }
        }
    }
}