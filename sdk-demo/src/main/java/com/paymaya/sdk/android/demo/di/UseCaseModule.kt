package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.usecase.*

internal object UseCaseModule {

    fun getFetchShopDataUseCase(): FetchShopDataUseCase =
        FetchShopDataUseCase()

    fun getSaveProductInCartUseCase(): SaveProductInCartUseCase =
        SaveProductInCartUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )

    fun getFetchProductsFromCartUseCase(): FetchProductsFromCartUseCase =
        FetchProductsFromCartUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )

    fun getRemoveProductFromCartUseCase(): RemoveProductFromCartUseCase =
        RemoveProductFromCartUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )

    fun getCreateCheckoutRequestUseCase(): CreateCheckoutRequestUseCase =
        CreateCheckoutRequestUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )

    fun getCreateSinglePaymentsRequestUseCase(): CreateSinglePaymentsRequestUseCase =
        CreateSinglePaymentsRequestUseCase(
            RepositoryModuleProvider.cartProductsRepository
        )

    fun getCreateWalletLinkRequestUseCase(): CreateWalletLinkRequestUseCase =
        CreateWalletLinkRequestUseCase()

}