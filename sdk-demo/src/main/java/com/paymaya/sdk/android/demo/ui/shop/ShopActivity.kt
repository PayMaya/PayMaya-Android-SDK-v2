package com.paymaya.sdk.android.demo.ui.shop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.di.PresenterModuleProvider
import com.paymaya.sdk.android.demo.model.ShopItem
import com.paymaya.sdk.android.demo.ui.cart.CartActivity
import kotlinx.android.synthetic.main.activity_shop.*

typealias OnAddToCartRequestListener = (shopItem: ShopItem) -> Unit

class ShopActivity : Activity(), ShopContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val presenter: ShopContract.Presenter by lazy { PresenterModuleProvider.getShopPresenter() }
    private var adapter =
        ShopItemAdapter(
            onAddToCartRequestListener = {
                presenter.addToCartButtonClicked(it)
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        initView()
        presenter.viewCreated(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.viewResumed()
    }

    override fun populateView(productsList: List<ShopItem>) {
        adapter.setItems(productsList)
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(this)
        shop_products_list.layoutManager = linearLayoutManager
        shop_products_list.adapter = adapter

        go_to_cart_button.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun updateBadgeCounter(value: Int) {
        go_to_cart_button.text = if (value > 0) "Go to Cart ($value)" else "Go to Cart"
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }
}
