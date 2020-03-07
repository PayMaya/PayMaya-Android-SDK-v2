package com.paymaya.sdk.android.vault

/**
 * Result of the card tokenization process.
 */
sealed class PayMayaVaultResult {

    /**
     * Success result of the card tokenization process.
     */
    class Success internal constructor(
        val paymentTokenId: String,
        val state: String,
        val createdAt: String,
        val updatedAt: String,
        val issuer: String
    ) : PayMayaVaultResult()

    /**
     * Canceled result of the card tokenization process.
     */
    object Cancel : PayMayaVaultResult()
}
