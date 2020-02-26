package com.paymaya.sdk.android.vault

/**
 * PayMayaVaultResult representing appropriate class for success or cancel status
 */
sealed class PayMayaVaultResult {

    /**
     * Success class represent success status for paymaya vault result.
     *
     * @property paymentTokenId Payment token id.
     * @property state State.
     * @property createdAt CreatedAt.
     * @property updatedAt UpdatedAt.
     * @property issuer Issuer.
     */
    class Success(
        val paymentTokenId: String,
        val state: String,
        val createdAt: String,
        val updatedAt: String,
        val issuer: String
    ) : PayMayaVaultResult()

    /**
     * Cancel object represent cancel status for paymaya vault result.
     */
    object Cancel : PayMayaVaultResult()
}
