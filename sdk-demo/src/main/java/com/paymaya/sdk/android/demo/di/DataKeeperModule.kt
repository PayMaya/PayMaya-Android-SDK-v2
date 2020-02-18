package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.data.CartProductsKeeper

internal interface DataKeeperModule {
    val cartProductsKeeper: CartProductsKeeper
}

internal object DataKeeperModuleProvider : DataKeeperModule {
    override val cartProductsKeeper: CartProductsKeeper by lazy { CartProductsKeeper() }
}