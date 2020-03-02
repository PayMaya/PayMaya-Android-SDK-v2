package com.paymaya.sdk.android.demo.di

import com.paymaya.sdk.android.demo.usecase.*

internal object UseCaseModule {

    fun getFetchShopDataUseCase(): FetchShopDataUseCase =
        FetchShopDataUseCase()

    fun getSaveProductInCartUseCase(): SaveProductInCartUseCase =
        SaveProductInCartUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getFetchProductsFromCartUseCase(): FetchProductsFromCartUseCase =
        FetchProductsFromCartUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getFetchTotalAmountFromCartUseCase(): FetchTotalAmountFromCartUseCase =
        FetchTotalAmountFromCartUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getFetchTotalCountFromCartUseCase(): FetchTotalCountFromCartUseCase =
        FetchTotalCountFromCartUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getRemoveProductFromCartUseCase(): RemoveProductFromCartUseCase =
        RemoveProductFromCartUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getCreateCheckoutRequestUseCase(): CreateCheckoutRequestUseCase =
        CreateCheckoutRequestUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getCreateSinglePaymentsRequestUseCase(): CreateSinglePaymentsRequestUseCase =
        CreateSinglePaymentsRequestUseCase(
            RepositoryModule.cartProductsRepository
        )

    fun getCreateWalletLinkRequestUseCase(): CreateWalletLinkRequestUseCase =
        CreateWalletLinkRequestUseCase()
}