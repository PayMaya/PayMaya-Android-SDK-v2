package com.paymaya.sdk.android.demo.mapper

import com.paymaya.sdk.android.common.models.RedirectUrl
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest

class WalletLinkMapper {

    fun run(): CreateWalletLinkRequest =
        CreateWalletLinkRequest(
            getRequestReferenceNumber(),
            getRedirectUrl()
        )

    private fun getRequestReferenceNumber(): String =
        "REQ_3"


    private fun getRedirectUrl(): RedirectUrl =
        RedirectUrl(
            success = "http://success.com",
            failure = "http://failure.com",
            cancel = "http://cancel.com"
        )
}