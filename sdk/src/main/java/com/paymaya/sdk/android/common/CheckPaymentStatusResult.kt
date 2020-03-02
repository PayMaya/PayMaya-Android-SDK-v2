package com.paymaya.sdk.android.common

import com.paymaya.sdk.android.common.internal.models.PaymentStatus
import java.lang.Exception

sealed class CheckPaymentStatusResult {

    class Success(
        val status: PaymentStatus
    ) : CheckPaymentStatusResult()

    object Cancel
        : CheckPaymentStatusResult()

    class Failure(
        val exception: Exception
    ) : CheckPaymentStatusResult()
}
