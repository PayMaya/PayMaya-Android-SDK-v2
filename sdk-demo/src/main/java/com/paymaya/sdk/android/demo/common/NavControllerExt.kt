package com.paymaya.sdk.android.demo.common

import androidx.navigation.NavController

fun NavController.clearStack() {
    while (popBackStack()) {
        // nothing
    }
}