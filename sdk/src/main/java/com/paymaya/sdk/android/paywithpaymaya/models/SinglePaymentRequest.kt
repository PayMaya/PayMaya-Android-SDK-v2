package com.paymaya.sdk.android.paywithpaymaya.models

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
 * Single payment request model encapsulating exhaustive information about transaction amount,
 * request reference number, URLs and metadata.
 *
 * @property totalAmount Model of total amount. Contains information about amount currency,
 * total value and details for whole items list.
 * @property requestReferenceNumber Reference number assigned by the merchant to identify a transaction.
 * @property redirectUrl Model defining redirect URL's used to redirect to specific pages once the payment
 * process is done.
 * @property metadata Model defining metadata.
 */
@Parcelize
@Serializable
data class SinglePaymentRequest(
    val totalAmount: TotalAmount,
    val requestReferenceNumber: String,
    override val redirectUrl: RedirectUrl,
    // TODO makes sense to have this? maybe metadata should be a string?
    @Serializable(with = JSONObjectSerializer::class)
    val metadata: @WriteWith<JSONObjectParceler> JSONObject? = null
) : PayMayaRequest
