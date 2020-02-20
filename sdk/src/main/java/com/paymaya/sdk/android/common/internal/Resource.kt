package com.paymaya.sdk.android.common.internal

import android.content.Context
import androidx.annotation.StringRes

internal sealed class Resource {
    companion object {
        operator fun invoke(text: String): Resource = Complete(text)
        operator fun invoke(id: Int): Resource = Contextual(id)
    }

    abstract fun inContext(context: Context): String

    data class Contextual(@StringRes val resourceId: Int) : Resource() {
        override fun inContext(context: Context): String = context.getString(resourceId)
    }

    data class Complete(val text: String) : Resource() {
        override fun inContext(context: Context): String = text
    }
}
