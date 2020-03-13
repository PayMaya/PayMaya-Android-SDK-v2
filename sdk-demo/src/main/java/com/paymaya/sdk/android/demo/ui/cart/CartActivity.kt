package com.paymaya.sdk.android.demo.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.LogLevel
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.demo.Constants.DECIMALS
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.di.PresenterModule
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.vault.PayMayaVault
import kotlinx.android.synthetic.main.activity_cart.*
import java.math.BigDecimal

typealias OnRemoveFromCartRequestListener = (cartItem: Item) -> Unit

class CartActivity : Activity(), CartContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val presenter: CartContract.Presenter = PresenterModule.getCartPresenter()
    private var adapter = CartItemAdapter(
        onRemoveFromCartRequestListener = { presenter.removeFromCartButtonClicked(it) }
    )

    private val payMayaCheckoutClient = PayMayaCheckout.newBuilder()
        .clientPublicKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.WARN)
        .build()

    private val payWithPayMayaClient = PayWithPayMaya.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.WARN)
        .build()

    private val payMayaVaultClient = PayMayaVault.newBuilder()
        .clientPublicKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.WARN)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initView()
        presenter.viewCreated(
            this,
            payMayaCheckoutClient,
            payWithPayMayaClient
        )
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(this)
        cart_products_list.layoutManager = linearLayoutManager
        cart_products_list.adapter = adapter

        pay_with_checkout_button.setOnClickListener { presenter.payWithCheckoutButtonClicked() }
        pay_with_single_payment_button.setOnClickListener { presenter.payWithSinglePaymentButtonClicked() }
        create_wallet_link_button.setOnClickListener { presenter.createWalletLinkButtonClicked() }
        pay_maya_vault_button.setOnClickListener { presenter.payMayaVaultButtonClicked() }
        check_recent_payment_status.setOnClickListener { presenter.payMayaCheckRecentPaymentStatusClicked() }
    }

    override fun setTotalAmount(totalAmount: BigDecimal, currency: String) {
        payment_amount.text = "${totalAmount.setScale(DECIMALS, BigDecimal.ROUND_HALF_DOWN)} $currency"
    }

    override fun populateView(productsList: List<Item>) {
        adapter.setItems(productsList)
    }

    override fun payWithCheckout(checkoutRequest: CheckoutRequest) {
        payMayaCheckoutClient.startCheckoutActivityForResult(this, checkoutRequest)
    }

    override fun payWithSinglePayment(singlePaymentRequest: SinglePaymentRequest) {
        payWithPayMayaClient.startSinglePaymentActivityForResult(this, singlePaymentRequest)
    }

    override fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest) {
        payWithPayMayaClient.startCreateWalletLinkActivityForResult(this, walletLinkRequest)
    }

    override fun payMayaVaultTokenizeCard() {
        payMayaVaultClient.startTokenizeCardActivityForResult(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        payMayaCheckoutClient.onActivityResult(requestCode, resultCode, data)?.let {
            presenter.checkoutCompleted(it)
            return
        }

        payWithPayMayaClient.onActivityResult(requestCode, resultCode, data)?.let {
            presenter.payWithPayMayaCompleted(it)
            return
        }

        payMayaVaultClient.onActivityResult(requestCode, resultCode, data)?.let {
            presenter.vaultCompleted(it)
        }
    }

    override fun showResultSuccessMessage(message: String) {
        Snackbar.make(cart_view_container, "Operation succeeded", Snackbar.LENGTH_SHORT).show()
        Log.i(TAG, message)
    }

    override fun showResultCancelMessage(message: String) {
        Snackbar.make(cart_view_container, "Operation canceled", Snackbar.LENGTH_SHORT).show()
        Log.w(TAG, message)
    }

    override fun showResultFailureMessage(message: String, exception: Exception) {
        Snackbar.make(cart_view_container, "Operation failure", Snackbar.LENGTH_SHORT).show()
        Log.e(TAG, message)
        if (exception is BadRequestException) {
            Log.d(TAG, exception.error.toString())
        }
    }

    override fun showPaymentIdNotAvailableMessage() {
        Snackbar.make(cart_view_container, "Payment ID not available", Snackbar.LENGTH_SHORT).show()
    }

    override fun showPaymentDetailedStatus(message: String) {
        Snackbar.make(cart_view_container, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        cart_progress_bar.visibility = VISIBLE
    }

    override fun hideProgressBar() {
        cart_progress_bar.visibility = GONE
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
