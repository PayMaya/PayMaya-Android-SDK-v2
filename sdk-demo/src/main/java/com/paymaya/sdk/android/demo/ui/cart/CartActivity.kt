package com.paymaya.sdk.android.demo.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.di.PresenterModuleProvider
import com.paymaya.sdk.android.paywithpaymaya.CreateWalletLinkResult
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import com.paymaya.sdk.android.vault.PayMayaVault
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.vault.PayMayaVaultResult
import kotlinx.android.synthetic.main.activity_cart.*
import java.math.BigDecimal

typealias OnRemoveFromCartRequestListener = (shopProduct: CartProduct) -> Unit

class CartActivity : Activity(), CartContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val presenter: CartContract.Presenter = PresenterModuleProvider.cartPresenter
    private var adapter = CartItemAdapter(
        onRemoveFromCartRequestListener = { presenter.removeFromCartClicked(it) }
    )

    private val payMayaCheckoutClient = PayMayaCheckout.Builder()
        .clientKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    private val payWithPayMayaClient = PayWithPayMaya.Builder()
        .clientKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    private val payMayaVaultClient = PayMayaVault.Builder()
        .clientKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
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

        pay_with_checkout_button.setOnClickListener { presenter.payWithCheckoutClicked() }
        pay_with_paymaya_button.setOnClickListener { presenter.payWithPayMayaClicked() }
        create_wallet_link_button.setOnClickListener { presenter.createWalletLinkClicked() }
        pay_maya_vault_tokenize_card_button.setOnClickListener { presenter.payMayaVaultTokenizeCardClicked() }
    }

    override fun payMayaVaultTokenizeCard() {
        payMayaVaultClient.execute(this)
    }

    override fun setTotalAmount(totalAmount: BigDecimal) {
        payment_amount.text = totalAmount.toString()
    }

    override fun populateView(productsList: List<CartProduct>) {
        adapter.setItems(productsList)
    }

    override fun payWithCheckout(checkoutRequest: CheckoutRequest) {
        payMayaCheckoutClient.execute(this, checkoutRequest)
    }

    override fun payWithPayMaya(singlePaymentRequest: SinglePaymentRequest) {
        payWithPayMayaClient.executeSinglePayment(this, singlePaymentRequest)
    }

    override fun createWalletLink(walletLinkRequest: CreateWalletLinkRequest) {
        payWithPayMayaClient.executeCreateWalletLink(this, walletLinkRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val checkoutResult = payMayaCheckoutClient.onActivityResult(requestCode, resultCode, data)
        checkoutResult?.let {
            processCheckoutResult(it)
            return
        }

        val payWithPayMayaResult =
            payWithPayMayaClient.onActivityResult(requestCode, resultCode, data)
        payWithPayMayaResult?.let {
            processPayWithWithPayMayaResult(it)
            return
        }

        val vaultResult = payMayaVaultClient.onActivityResult(requestCode, resultCode, data)
        vaultResult?.let {
            processVaultResult(it)
            return
        }
    }

    private fun processCheckoutResult(result: PayMayaCheckoutResult) {
        when (result) {
            is PayMayaCheckoutResult.Success -> {
                val message = "Success, checkoutId: ${result.checkoutId}"
                Log.i(TAG, message)
            }

            is PayMayaCheckoutResult.Cancel -> {
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                Log.w(TAG, message)
            }

            is PayMayaCheckoutResult.Failure -> {
                val message =
                    "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
            }
        }
    }

    private fun processPayWithWithPayMayaResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val message = "Success, paymentId: ${result.paymentId}"
                Log.i(TAG, message)
            }

            is SinglePaymentResult.Cancel -> {
                val message = "Canceled, paymentId: ${result.paymentId}"
                Log.w(TAG, message)
            }

            is SinglePaymentResult.Failure -> {
                val message =
                    "Failure, paymentId: ${result.paymentId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
            }
            is CreateWalletLinkResult.Success -> {
                val message = "Success, linkId: ${result.linkId}"
                Log.i(TAG, message)
            }

            is CreateWalletLinkResult.Cancel -> {
                val message = "Canceled, linkId: ${result.linkId}"
                Log.w(TAG, message)
            }

            is CreateWalletLinkResult.Failure -> {
                val message =
                    "Failure, linkId: ${result.linkId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
            }
        }
    }

    private fun processVaultResult(result: PayMayaVaultResult) {
        when (result) {
            is PayMayaVaultResult.Success -> {
                val message = "Success, result: ${result.paymentTokenId}, ${result.state}"
                Log.i(TAG, message)
            }

            is PayMayaVaultResult.Cancel -> {
                val message = "Canceled"
                Log.w(TAG, message)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
