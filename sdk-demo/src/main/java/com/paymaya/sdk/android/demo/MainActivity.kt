package com.paymaya.sdk.android.demo

import android.app.Activity
import android.os.Bundle
import com.paymaya.sdk.android.checkout.PayMayaCheckout

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PayMayaCheckout().init()
    }
}

