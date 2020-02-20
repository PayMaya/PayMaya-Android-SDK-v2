package com.paymaya.sdk.android.demo.ui.shop

import com.paymaya.sdk.android.demo.model.ShopProduct

interface ShopContract {

    interface View {
        fun populateView(productsList: List<ShopProduct>)
        fun updateBadgeCounter(value: Int)
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun addToCartClicked(product: ShopProduct)
        fun viewResumed()
    }
}