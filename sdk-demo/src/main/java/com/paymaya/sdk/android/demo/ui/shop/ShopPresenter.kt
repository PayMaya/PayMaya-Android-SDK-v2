package com.paymaya.sdk.android.demo.ui.shop

import com.paymaya.sdk.android.demo.data.BackendRepository
import com.paymaya.sdk.android.demo.data.CartRepository
import com.paymaya.sdk.android.demo.model.ShopItem

class ShopPresenter(
    private val backendRepository: BackendRepository,
    private val cartRepository: CartRepository
) : ShopContract.Presenter {

    private var view: ShopContract.View? = null

    override fun viewCreated(view: ShopContract.View) {
        this.view = view
        val products = backendRepository.getShopItems()
        view.populateView(products)
    }

    override fun viewResumed() {
        val totalCount = cartRepository.getTotalCount()
        view?.updateBadgeCounter(totalCount)
    }

    override fun addToCartButtonClicked(product: ShopItem) {
        cartRepository.addItem(product)
        val totalCount = cartRepository.getTotalCount()
        view?.updateBadgeCounter(totalCount)
    }

    override fun viewDestroyed() {
        this.view = null
    }
}