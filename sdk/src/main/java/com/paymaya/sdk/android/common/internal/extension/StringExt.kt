package com.paymaya.sdk.android.common.internal.extension

fun String.takeFirst(n: Int): String {
    require(n >= 0) { "Requested character count $n is less than zero." }
    val length = length
    return substring(0, n.coerceAtMost(length))
}
