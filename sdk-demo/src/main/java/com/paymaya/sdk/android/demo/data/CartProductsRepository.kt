package com.paymaya.sdk.android.demo.data

import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.checkout.models.ItemAmount
import com.paymaya.sdk.android.common.models.AmountDetails
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.demo.model.ShopItem
import java.math.BigDecimal

class CartProductsRepository {

    private val items: MutableList<Item> = mutableListOf()
    private var totalAmount: BigDecimal = BigDecimal(0)
    private var totalCount: Int = 0

    fun getItems(): List<Item> =
        items.toList()

    fun getTotalAmount(): BigDecimal =
        totalAmount

    fun getTotalCount(): Int =
        totalCount

    fun addItem(shopItem: ShopItem) {
        val index = items.indexOfFirst { it.name == shopItem.name }

        totalAmount = totalAmount.add(shopItem.value)
        ++totalCount

        if (index == -1) {
            val newItem = createItem(shopItem)
            items.add(newItem)
        } else {
            val updatedItem = increaseQuantity(items[index])
            items[index] = updatedItem
        }
    }

    fun removeItem(item: Item) {
        val index = items.indexOf(item)
        require(index != -1)

        val amount = requireNotNull(item.amount)
        totalAmount = totalAmount.subtract(amount.value)
        --totalCount

        val updatedItem = decreaseQuantity(item)
        if (updatedItem.quantity == 0) {
            items.removeAt(index)
        } else {
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
        val amount = requireNotNull(item.amount)
        val quantity = requireNotNull(item.quantity)

        return item.copy(
            quantity = quantity.plus(1),
            totalAmount = item.totalAmount
                .copy(value = item.totalAmount.value + amount.value)
        )
    }

    private fun decreaseQuantity(item: Item): Item {
        val amount = requireNotNull(item.amount)
        val quantity = requireNotNull(item.quantity)

        return item.copy(
            quantity = quantity.minus(1),
            totalAmount = item.totalAmount
                .copy(value = item.totalAmount.value - amount.value)
        )
    }
}