package com.paymaya.sdk.android.demo.ui.shop

import com.paymaya.sdk.android.demo.model.ShopProduct
import com.paymaya.sdk.android.demo.usecase.FetchProductsFromCartUseCase
import com.paymaya.sdk.android.demo.usecase.FetchShopDataUseCase
import com.paymaya.sdk.android.demo.usecase.SaveProductInCartUseCase

class ShopPresenter(
    private val fetchShopDataUseCase: FetchShopDataUseCase,
    private val saveProductInCartUseCase: SaveProductInCartUseCase,
    private val fetchProductsFromCartUseCase: FetchProductsFromCartUseCase
) : ShopContract.Presenter {

    private var view: ShopContract.View? = null
    private var productsInCartCount = 0

    override fun viewCreated(view: ShopContract.View) {
        this.view = view
        view.populateView(fetchShopDataUseCase.run())
    }

    override fun viewRestarted() {
        view?.updateBadgeCounter(getProductsInCartCount())
    }

    override fun addToCartClicked(product: ShopProduct) {
        saveProductInCartUseCase.run(product)
        productsInCartCount++
        view?.updateBadgeCounter(productsInCartCount)
    }

    private fun getProductsInCartCount(): Int {
        var productsInCartCount = 0
        fetchProductsFromCartUseCase.run().forEach {
            productsInCartCount += it.quantity
        }
        return productsInCartCount
    }
}