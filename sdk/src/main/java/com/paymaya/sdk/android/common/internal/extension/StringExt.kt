package com.paymaya.sdk.android.common.internal.extension

internal fun String.takeFirst(n: Int): String {
    require(n >= 0) { "Requested character count $n is less than zero." }
    return substring(0, n.coerceAtMost(length))
}
