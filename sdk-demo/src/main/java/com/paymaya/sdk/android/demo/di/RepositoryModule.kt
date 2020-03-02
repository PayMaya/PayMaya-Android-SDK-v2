package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.data.BackendRepository
import com.paymaya.sdk.android.demo.data.CartRepository

internal object RepositoryModule {
    val cartRepository: CartRepository by lazy { CartRepository() }
    val backendRepository: BackendRepository by lazy { BackendRepository() }
}
