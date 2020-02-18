package com.paymaya.sdk.android.demo.ui.cart.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.ui.cart.OnRemoveFromCartRequestListener
import kotlinx.android.synthetic.main.holder_cart_product.view.*

class CartItemAdapter(
    private val onRemoveFromCartRequestListener: OnRemoveFromCartRequestListener
) : RecyclerView.Adapter<CartItemAdapter.ItemViewHolder>() {

    private val items: MutableList<CartProduct> = mutableListOf()

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

    fun setItems(items: List<CartProduct>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(product: CartProduct) {
            itemView.product_name.text = product.name
            itemView.product_total_amount.text =
                product.totalAmount?.toString() + " ${product.items.first().currency}"
            itemView.cart_product_container.setBackgroundResource(R.drawable.rectangle)
            itemView.add_to_cart_button.setOnClickListener {
                onRemoveFromCartRequestListener.invoke(product)
                notifyDataSetChanged()
            }
        }
    }

}