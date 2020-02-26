package com.paymaya.sdk.android.checkout.models

import android.os.Parcelable
import com.paymaya.sdk.android.common.models.TotalAmount
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

/**
 * Item model. Contains all information about item chosen by buyer.
 *
 * @property name Item's name
 * @property quantity Item's quantity
 * @property code Item's code
 * @property description Item's description
 * @property amount Model of item's amount. Contains information about amount value and details.
 * @property totalAmount Model of item's total amount. Contains information about amount currency,
 * total value and details.
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
