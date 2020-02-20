package com.paymaya.sdk.android.vault

sealed class PayMayaVaultResult {

    class Success(
        val paymentTokenId: String,
        val state: String,
        val createdAt: String,
        val updatedAt: String,
        val issuer: String
    ) : PayMayaVaultResult()

    object Cancel : PayMayaVaultResult()
}
