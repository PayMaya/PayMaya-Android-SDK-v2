package com.paymaya.sdk.android.demo.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private val payMayaCheckoutClient = PayMayaCheckout.Builder()
        .clientKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.VERBOSE)
        .build()

    private val payWithPayMayaClient = PayWithPayMaya.Builder()
        .clientKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.VERBOSE)
        .build()

    private val payMayaVaultClient = PayMayaVault.Builder()
        .clientKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(LogLevel.VERBOSE)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initView()
        presenter.viewCreated(this)
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(this)
        cart_products_list.layoutManager = linearLayoutManager
        cart_products_list.adapter = adapter

        pay_with_checkout_button.setOnClickListener { presenter.payWithCheckoutButtonClicked() }
        pay_with_single_payment_button.setOnClickListener { presenter.payWithSinglePaymentButtonClicked() }
        create_wallet_link_button.setOnClickListener { presenter.createWalletLinkButtonClicked() }
        pay_maya_vault_button.setOnClickListener { presenter.payMayaVaultButtonClicked() }
    }

    override fun setTotalAmount(totalAmount: BigDecimal) {
        payment_amount.text = totalAmount.setScale(DECIMALS, BigDecimal.ROUND_HALF_DOWN).toString()
    }

    override fun populateView(productsList: List<Item>) {
        adapter.setItems(productsList)
    }

    override fun payWithCheckout(checkoutRequest: CheckoutRequest) {
        payMayaCheckoutClient.execute(this, checkoutRequest)
    }

    override fun payWithSinglePayment(singlePaymentRequest: SinglePaymentRequest) {
        payWithPayMayaClient.executeSinglePayment(this, singlePaymentRequest)
    }

    override fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest) {
        payWithPayMayaClient.executeCreateWalletLink(this, walletLinkRequest)
    }

    override fun payMayaVaultTokenizeCard() {
        payMayaVaultClient.execute(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
