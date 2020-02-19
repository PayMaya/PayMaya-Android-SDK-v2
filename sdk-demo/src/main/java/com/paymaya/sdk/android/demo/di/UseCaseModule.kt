package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.usecase.*

internal object UseCaseModuleProvider {
    val fetchShopDataUseCase: FetchShopDataUseCase = FetchShopDataUseCase()
    val saveProductInCartUseCase: SaveProductInCartUseCase = SaveProductInCartUseCase(
        RepositoryModuleProvider.cartProductsRepository
    )
    val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase by lazy {
        FetchProductsFromCartUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )
    }
    val removeProductFromCartUseCase: RemoveProductFromCartUseCase by lazy {
        RemoveProductFromCartUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )
    }
    val createCheckoutRequestUseCase: CreateCheckoutRequestUseCase by lazy {
        CreateCheckoutRequestUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )
    }
    val createSinglePaymentsRequestUseCase: CreateSinglePaymentsRequestUseCase by lazy {
        CreateSinglePaymentsRequestUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )
    }
    val createWalletLinkRequestUseCase: CreateWalletLinkRequestUseCase by lazy {
        CreateWalletLinkRequestUseCase()
    }
}