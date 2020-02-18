package com.paymaya.sdk.android.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.paymaya.sdk.android.PayMayaEnvironment
import com.paymaya.sdk.android.checkout.*
import com.paymaya.sdk.android.checkout.exceptions.BadRequestException
import com.paymaya.sdk.android.checkout.models.*
import com.paymaya.sdk.android.demo.common.clearStack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CartViewActions {

    private val navigationController: NavController by lazy { findNavController(R.id.navigation_host_fragment) }
    private val payMayaClient = PayMayaCheckout.Builder()
        .clientKey("pk-NCLk7JeDbX1m22ZRMDYO9bEPowNWT5J4aNIKIbcTy2a")
        .environment(PayMayaEnvironment.SANDBOX)
        .logLevel(Log.VERBOSE)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigationMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = payMayaClient.onActivityResult(requestCode, resultCode, data)
        if (result != null) {
            processResult(result)
        }
    }

    override fun payWithCheckout(checkout: Checkout) {
        payMayaClient.execute(this, checkout)
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
