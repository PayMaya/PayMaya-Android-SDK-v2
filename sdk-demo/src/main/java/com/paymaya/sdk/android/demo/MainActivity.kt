package com.paymaya.sdk.android.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.paymaya.sdk.android.checkout.PayMayaCheckoutResult
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.PayMayaCheckout
import com.paymaya.sdk.android.common.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.models.Buyer
import com.paymaya.sdk.android.checkout.models.CheckoutRequest
import com.paymaya.sdk.android.checkout.models.Item
import com.paymaya.sdk.android.common.models.*
import com.paymaya.sdk.android.demo.common.clearStack
import com.paymaya.sdk.android.paywithpaymaya.CreateWalletLinkResult
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMaya
import com.paymaya.sdk.android.paywithpaymaya.PayWithPayMayaResult
import com.paymaya.sdk.android.paywithpaymaya.SinglePaymentResult
import com.paymaya.sdk.android.paywithpaymaya.models.SinglePaymentRequest
import com.paymaya.sdk.android.paywithpaymaya.models.CreateWalletLinkRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal

class MainActivity : AppCompatActivity(), CartViewActions {

    private val navigationController: NavController by lazy { findNavController(R.id.navigation_host_fragment) }
    private val payMayaCheckoutClient = PayMayaCheckout.Builder()
        .clientKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    private val payWityPayMayaClient = PayWithPayMaya.Builder()
        .clientKey("pk-MOfNKu3FmHMVHtjyjG7vhr7vFevRkWxmxYL1Yq6iFk5")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigationMenu()

//        checkoutButton.setOnClickListener {
//            payCheckout()
//        }
//
//        payWithPayMayaButton.setOnClickListener {
//            payWithPayMayaSinglePayment()
//        }
//
//        payWithPayMayaCreateWalletLinkButton.setOnClickListener {
//            payWithPayMayaCreateWalletLink()
//        }
    }

    private fun payCheckout() {
        val checkoutModel = CheckoutRequest(
            totalAmount = TotalAmount(
                value = BigDecimal(99999),
                currency = "PHP"
            ),
            buyer = Buyer(
                firstName = "John",
                lastName = "Doe"
            ),
            items = listOf(
                Item(
                    name = "shoes",
                    quantity = 1,
                    totalAmount = TotalAmount(
                        BigDecimal(10),
                        "PHP"
                    )
                )
            ),
            requestReferenceNumber = "REQ_1",
            redirectUrl = RedirectUrl(
                success = "http://success.com",
                failure = "http://failure.com",
                cancel = "http://cancel.com"
            )
        )
        payMayaCheckoutClient.execute(this, checkoutModel)
    }

    private fun payWithPayMayaSinglePayment() {
        val requestData = SinglePaymentRequest(
            totalAmount = TotalAmount(
                value = BigDecimal(99999),
                currency = "PHP"
            ),
            requestReferenceNumber = "REQ_2",
            redirectUrl = RedirectUrl(
                success = "http://success.com",
                failure = "http://failure.com",
                cancel = "http://cancel.com"
            )
        )
        payWityPayMayaClient.executeSinglePayment(this, requestData)
    }

    private fun payWithPayMayaCreateWalletLink() {
        val requestData = CreateWalletLinkRequest(
            requestReferenceNumber = "REQ_3",
            redirectUrl = RedirectUrl(
                success = "http://success.com",
                failure = "http://failure.com",
                cancel = "http://cancel.com"
            )
        )
        payWityPayMayaClient.executeCreateWalletLink(this, requestData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val checkoutResult = payMayaCheckoutClient.onActivityResult(requestCode, resultCode, data)
        checkoutResult?.let {
            processCheckoutResult(it)
            return
        }

        val payWithPayMayaResult = payWityPayMayaClient.onActivityResult(requestCode, resultCode, data)
        payWithPayMayaResult?.let {
            processPayWithWithPayMayaResult(it)
            return
        }
    }

    override fun payWithCheckout(checkout: CheckoutRequest) {
        payMayaCheckoutClient.execute(this, checkout)
    }

    private fun processCheckoutResult(result: PayMayaCheckoutResult) {
        when (result) {
            is PayMayaCheckoutResult.Success -> {
                val message = "Success, checkoutId: ${result.checkoutId}"
                Log.i(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is PayMayaCheckoutResult.Cancel -> {
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                Log.w(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is PayMayaCheckoutResult.Failure -> {
                val message =
                    "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun processPayWithWithPayMayaResult(result: PayWithPayMayaResult) {
        when (result) {
            is SinglePaymentResult.Success -> {
                val message = "Success, paymentId: ${result.paymentId}"
                Log.i(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is SinglePaymentResult.Cancel -> {
                val message = "Canceled, paymentId: ${result.paymentId}"
                Log.w(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is SinglePaymentResult.Failure -> {
                val message =
                    "Failure, paymentId: ${result.paymentId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
            is CreateWalletLinkResult.Success -> {
                val message = "Success, linkId: ${result.linkId}"
                Log.i(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is CreateWalletLinkResult.Cancel -> {
                val message = "Canceled, linkId: ${result.linkId}"
                Log.w(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is CreateWalletLinkResult.Failure -> {
                val message =
                    "Failure, linkId: ${result.linkId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).error.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun updateBadgeCounter(value: Int) {
        bottomNavigationView
            .getOrCreateBadge(R.id.menu_item_cart)
            ?.number = value
    }

    private fun initBottomNavigationMenu() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_shop -> navigateTo(R.id.shopFragment)
                R.id.menu_item_cart -> navigateTo(R.id.cartFragment)
            }
            true
        }
    }

    private fun navigateTo(navFragmentId: Int) {
        navigationController.clearStack()
        navigationController.navigate(navFragmentId)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
