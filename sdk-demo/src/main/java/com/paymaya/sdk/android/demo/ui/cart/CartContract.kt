package com.paymaya.sdk.android.demo.ui.cart

import com.paymaya.sdk.android.checkout.models.Checkout
import com.paymaya.sdk.android.demo.model.CartProduct
import java.math.BigDecimal

interface CartContract {

    interface View {
        fun populateView(productsList: List<CartProduct>)
        fun setTotalAmount(totalAmount: BigDecimal)
        fun clearBadgeCounter()
        fun payWithCheckout(checkoutPaymentModel: Checkout)
        fun payWithPayMaya()
    }

    interface Presenter {
        fun viewCreated(view: View)
        fun removeFromCartClicked(product: CartProduct)
        fun payWithCheckoutClicked()
        fun payWithPayMayaClicked()
    }
}