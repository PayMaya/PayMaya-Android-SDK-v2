package com.paymaya.sdk.android.demo.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.demo.Constants.DECIMALS
import com.paymaya.sdk.android.demo.R
import kotlinx.android.synthetic.main.holder_cart_product.view.*
import java.math.BigDecimal

class CartItemAdapter(
    private val onRemoveFromCartRequestListener: OnRemoveFromCartRequestListener
) : RecyclerView.Adapter<CartItemAdapter.ItemViewHolder>() {

    private val items: MutableList<Item> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_cart_product, parent, false)
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

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(product: Item) {
            itemView.product_name.text = product.name
            itemView.product_total_amount.text =
                "${product.totalAmount.value.setScale(DECIMALS, BigDecimal.ROUND_HALF_DOWN)} ${product.totalAmount?.currency}"
            itemView.cart_product_container.setBackgroundResource(R.drawable.rectangle)
            itemView.product_count.text = product.quantity.toString()
            itemView.add_to_cart_button.setOnClickListener {
                onRemoveFromCartRequestListener.invoke(product)
            }
        }
    }
}