package com.paymaya.sdk.android.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.paymaya.sdk.android.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.*
import com.paymaya.sdk.android.checkout.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.models.*
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal

class MainActivity : Activity() {

    private val payMayaClient = PayMayaCheckout.Builder()
        .clientKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkoutButton.setOnClickListener {
            payCheckout()
        }
    }

    private fun payCheckout() {
        val checkoutModel = Checkout(
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
                    totalAmount = TotalAmount(BigDecimal(10), "PHP")
                )
            ),
            requestReferenceNumber = "REQ_1",
            redirectUrl = RedirectUrl(
                success = "http://success.com",
                failure = "http://failure.com",
                cancel = "http://cancel.com"
            )
        )
        payMayaClient.execute(this, checkoutModel)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = payMayaClient.onActivityResult(requestCode, resultCode, data)
        if (result != null) {
            processResult(result)
        }
    }

    private fun processResult(result: PayMayaResult) {
        when (result) {
            is ResultSuccess -> {
                val message = "Success, checkoutId: ${result.checkoutId}"
                Log.i(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is ResultCancel -> {
                val message = "Canceled, checkoutId: ${result.checkoutId}"
                Log.w(TAG, message)
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            is ResultFailure -> {
                val message =
                    "Failure, checkoutId: ${result.checkoutId}, exception: ${result.exception}"
                Log.e(TAG, message)
                if (result.exception is BadRequestException) {
                    Log.d(TAG, (result.exception as BadRequestException).payMayaError.toString())
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
