package com.paymaya.sdk.android.demo.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.paymaya.sdk.android.demo.BaseFragment
import com.paymaya.sdk.android.demo.R
import com.paymaya.sdk.android.demo.di.PresenterModuleProvider
import com.paymaya.sdk.android.demo.model.ShopProduct
import com.paymaya.sdk.android.demo.ui.shop.item.ProductItemAdapter
import kotlinx.android.synthetic.main.fragment_shop.*

typealias OnAddToCartRequestListener = (shopProduct: ShopProduct) -> Unit

class ShopFragment : BaseFragment(), ShopContract.View {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val presenter: ShopContract.Presenter by lazy { PresenterModuleProvider.shopPresenter }
    private var adapter =
        ProductItemAdapter(
            onAddToCartRequestListener = {
                presenter.addToCartClicked(it)
            }
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        presenter.viewCreated(this)
    }

    override fun populateView(productsList: List<ShopProduct>) {
        adapter.setItems(productsList)
    }

    private fun initView() {
        linearLayoutManager = LinearLayoutManager(context)
        shop_products_list.layoutManager = linearLayoutManager
        shop_products_list.adapter = adapter
    }

    override fun updateBadgeCounter(value: Int) {
        shopViewActions?.updateBadgeCounter(value)
    }
}
