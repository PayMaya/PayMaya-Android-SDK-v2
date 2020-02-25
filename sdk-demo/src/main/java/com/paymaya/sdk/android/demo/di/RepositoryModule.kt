package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.data.CartProductsRepository

internal object RepositoryModuleProvider {
    val cartProductsRepository: CartProductsRepository by lazy { CartProductsRepository() }
}