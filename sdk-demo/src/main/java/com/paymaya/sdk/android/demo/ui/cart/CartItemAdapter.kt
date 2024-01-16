package com.paymaya.sdk.android.demo.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.demo.Constants.DECIMALS
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.databinding.HolderCartProductBinding
import java.math.BigDecimal

class CartItemAdapter(
    private val onRemoveFromCartRequestListener: OnRemoveFromCartRequestListener
) : RecyclerView.Adapter<CartItemAdapter.ItemViewHolder>() {

    private val items: MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            HolderCartProductBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(items[position])
    }

    fun setItems(items: List<Item>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: HolderCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(product: Item) {
            val amount = product.totalAmount.value.setScale(DECIMALS, BigDecimal.ROUND_HALF_DOWN).toString()

            binding.productName.text = product.name
            binding.productTotalAmount.text = "$amount ${product.totalAmount.currency}"
            binding.cartProductContainer.setBackgroundResource(R.drawable.rectangle)
            binding.productCount.text = "(${product.quantity})"
            binding.addToCartButton.setOnClickListener {
                onRemoveFromCartRequestListener.invoke(product)
            }
        }
    }
}