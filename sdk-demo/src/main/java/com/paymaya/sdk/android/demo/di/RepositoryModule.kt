package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.data.CartProductsRepository

internal object RepositoryModule {
    val cartProductsRepository: CartProductsRepository by lazy { CartProductsRepository() }
}
