package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.checkout.models.ItemAmount
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.model.ShopItem

class CartProductsRepository {

    private val items: MutableList<Item> = mutableListOf()

    fun addItem(shopItem: ShopItem) {
        val index = items.indexOfFirst { it.name == shopItem.name }

        if (index == -1) {
            val newItem = createItem(shopItem)
            items.add(newItem)
        } else {
            val updatedItem = increaseQuantity(items[index])
            items[index] = updatedItem
        }
    }

    private fun createItem(shopItem: ShopItem): Item =
        Item(
            quantity = 1,
            totalAmount = TotalAmount(
                shopItem.value,
                shopItem.currency,
                AmountDetails(shopItem.discount)
            ),
            name = shopItem.name,
            amount = ItemAmount(
                shopItem.value,
                AmountDetails(shopItem.discount)
            ),
            description = shopItem.description,
            code = shopItem.code
        )

    private fun increaseQuantity(item: Item): Item {
        val amount = item.amount
        require(amount != null)

        return item.copy(
            quantity = item.quantity?.plus(1),
            totalAmount = item.totalAmount
                .copy(value = item.totalAmount.value + amount.value)
        )
    }

    fun removeItem(item: Item) {
        val index = items.indexOf(item)
        require(index != -1)

        val updatedItem = decreaseQuantity(item)
        items[index] = updatedItem
        if (updatedItem.quantity == 0) {
            items.remove(updatedItem)
        }
    }

    private fun decreaseQuantity(item: Item): Item {
        val amount = item.amount
        require(amount != null)

        return item.copy(
            quantity = item.quantity?.minus(1),
            totalAmount = item.totalAmount
                .copy(value = item.totalAmount.value - amount.value)
        )
    }

    fun fetchItems(): List<Item> =
        items
}