package com.paymaya.sdk.android.checkout.models

import com.paymaya.sdk.android.common.internal.models.PayMayaRequest
import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.common.models.TotalAmount
import com.paymaya.sdk.android.common.serialization.JSONObjectParceler
import com.paymaya.sdk.android.common.serialization.JSONObjectSerializer
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.WriteWith
import kotlinx.serialization.Serializable
import org.json.JSONObject

/**
 * Model with data required to initiate checkout payment.
 * Contains information about the buyer, the items inside the cart,
 * transaction amount, status of payment and other details.
 *
 * @property totalAmount Transaction amount details.
 * @property buyer Details of the buyer.
 * @property items List of items.
 * @property requestReferenceNumber Reference number assigned by the merchant to identify a transaction.
 * @property redirectUrl Set of redirect URLs upon process completion.
 * @property metadata Additional data about the specific checkout. This can be
 *           supplied during the checkout creation.
 */
@Parcelize
@Serializable
data class CheckoutRequest(
    val totalAmount: TotalAmount,
    val buyer: Buyer? = null,
    val items: List<Item>,
    val requestReferenceNumber: String,
    override val redirectUrl: RedirectUrl,
    // TODO makes sense to have this? maybe metadata should be a string?
    @Serializable(with = JSONObjectSerializer::class)
    val metadata: @WriteWith<JSONObjectParceler> JSONObject? = null
) : PayMayaRequest
