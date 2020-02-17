package com.paymaya.sdk.android.demo

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    protected var shopViewActions: ShopViewActions? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShopViewActions) {
            shopViewActions = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        shopViewActions = null
    }
}