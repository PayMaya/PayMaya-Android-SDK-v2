package com.paymaya.sdk.android.demo.ui.shop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paymaya.sdk.android.demo.databinding.ActivityShopBinding
import com.paymaya.sdk.android.demo.di.PresenterModule
import com.paymaya.sdk.android.demo.model.ShopItem
import com.paymaya.sdk.android.demo.ui.cart.CartActivity

typealias OnAddToCartRequestListener = (shopItem: ShopItem) -> Unit

class ShopActivity : Activity(), ShopContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var binding: ActivityShopBinding
    private val presenter: ShopContract.Presenter by lazy { PresenterModule.getShopPresenter() }
    private var adapter =
        ShopItemAdapter(
            onAddToCartRequestListener = {
                presenter.addToCartButtonClicked(it)
            }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.shopProductsList.layoutManager = linearLayoutManager
        binding.shopProductsList.adapter = adapter

        binding.goToCartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun updateBadgeCounter(value: Int) {
        binding.goToCartButton.text = if (value > 0) "Go to Cart ($value)" else "Go to Cart"
    }

    override fun onDestroy() {
        presenter.viewDestroyed()
        super.onDestroy()
    }
}
