package com.paymaya.sdk.android.demo

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    protected var cartViewActions: CartViewActions? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartViewActions) {
            cartViewActions = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        cartViewActions = null
    }
}