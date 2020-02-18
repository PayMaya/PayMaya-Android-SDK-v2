package com.paymaya.sdk.android.demo.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.paymaya.sdk.android.checkout.models.Checkout
import com.paymaya.sdk.android.demo.BaseFragment
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.di.PresenterModuleProvider
import com.paymaya.sdk.android.demo.model.CartProduct
import com.paymaya.sdk.android.demo.ui.cart.item.CartItemAdapter
import kotlinx.android.synthetic.main.fragment_cart.*
import java.math.BigDecimal

typealias OnRemoveFromCartRequestListener = (shopProduct: CartProduct) -> Unit

class CartFragment : BaseFragment(), CartContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val presenter: CartContract.Presenter by lazy { PresenterModuleProvider.cartPresenter }
    private var adapter = CartItemAdapter(
        onRemoveFromCartRequestListener = { presenter.removeFromCartClicked(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        presenter.viewCreated(this)
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(context)
        cart_products_list.layoutManager = linearLayoutManager
        cart_products_list.adapter = adapter

        pay_with_checkout_button.setOnClickListener { presenter.payWithCheckoutClicked() }
        pay_with_paymaya_button.setOnClickListener { presenter.payWithPayMayaClicked() }
    }

    override fun setTotalAmount(totalAmount: BigDecimal) {
        payment_amount.text = totalAmount.toString()
    }

    override fun clearBadgeCounter() {
        cartViewActions?.updateBadgeCounter(DEFAULT_BADGE_VALUE)
    }

    override fun populateView(productsList: List<CartProduct>) {
        adapter.setItems(productsList)
    }

    override fun payWithCheckout(checkoutPaymentModel: Checkout) {
        cartViewActions?.payWithCheckout(checkoutPaymentModel)
    }

    override fun payWithPayMaya() {
//        TODO
    }

    companion object {
        private const val DEFAULT_BADGE_VALUE = 0
    }
}