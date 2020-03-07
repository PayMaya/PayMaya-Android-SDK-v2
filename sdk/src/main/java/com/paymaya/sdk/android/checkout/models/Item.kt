package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.TotalAmount
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents the entity that can be purchased from a merchant's shop.
 *
 * @property name Item's product name.
 * @property quantity Item's quantity.
 * @property code Item's SKU code.
 * @property description Item's description.
 * @property amount Item's unit price.
 * @property totalAmount Item's total price.
 */
@Parcelize
@Serializable
data class Item(
    val name: String,
    val quantity: Int? = null,
    val code: String? = null,
    val description: String? = null,
    val amount: ItemAmount? = null,
    val totalAmount: TotalAmount
) : Parcelable
