package com.paymaya.sdk.android.demo.ui.shop

import com.paymaya.sdk.android.demo.model.ShopItem

interface ShopContract {

    interface View {
        fun populateView(productsList: List<ShopItem>)
        fun updateBadgeCounter(value: Int)
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun viewResumed()
        fun viewDestroyed()

        fun addToCartButtonClicked(product: ShopItem)
    }
}