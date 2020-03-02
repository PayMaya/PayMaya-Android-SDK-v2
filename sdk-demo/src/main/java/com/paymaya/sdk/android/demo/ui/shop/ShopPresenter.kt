package com.paymaya.sdk.android.demo.ui.shop

import com.paymaya.sdk.android.demo.model.ShopItem
import com.paymaya.sdk.android.demo.usecase.FetchShopDataUseCase
import com.paymaya.sdk.android.demo.usecase.FetchTotalCountFromCartUseCase
import com.paymaya.sdk.android.demo.usecase.SaveProductInCartUseCase

class ShopPresenter(
    private val fetchShopDataUseCase: FetchShopDataUseCase,
    private val saveProductInCartUseCase: SaveProductInCartUseCase,
    private val fetchTotalCountFromCartUseCase: FetchTotalCountFromCartUseCase
) : ShopContract.Presenter {

    private var view: ShopContract.View? = null

    override fun viewCreated(view: ShopContract.View) {
        this.view = view
        val products = fetchShopDataUseCase.run()
        view.populateView(products)
    }

    override fun viewResumed() {
        val totalCount = fetchTotalCountFromCartUseCase.run()
        view?.updateBadgeCounter(totalCount)
    }

    override fun addToCartButtonClicked(product: ShopItem) {
        saveProductInCartUseCase.run(product)
        val totalCount = fetchTotalCountFromCartUseCase.run()
        view?.updateBadgeCounter(totalCount)
    }

    override fun viewDestroyed() {
        this.view = null
    }
}