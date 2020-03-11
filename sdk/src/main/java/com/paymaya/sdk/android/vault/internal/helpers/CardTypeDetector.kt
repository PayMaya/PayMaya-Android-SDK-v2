/*
 * Copyright (c) 2020  PayMaya Philippines, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.paymaya.sdk.android.vault.internal.helpers

import com.paymaya.sdk.android.common.internal.extension.takeFirst

internal class CardTypeDetector {
    fun detectType(cardNumber: String): CardType {
        val cardPrefix = cardNumber.takeFirst(SCHEME_PREFIX_LENGTH)
        if (cardPrefix.isEmpty()) return CardType.UNKNOWN

        return when {
            matchPrefixWithPrefixesRanges(cardPrefix, VISA_PREFIXES_RANGES) -> CardType.VISA
            matchPrefixWithPrefixesRanges(cardPrefix, AMEX_PREFIXES_RANGES) -> CardType.AMEX
            matchPrefixWithPrefixesRanges(cardPrefix, JCB_PREFIXES_RANGES) -> CardType.JCB
            matchPrefixWithPrefixesRanges(cardPrefix, MASTER_CARD_PREFIXES_RANGES) -> CardType.MASTER_CARD
            else -> CardType.UNKNOWN
        }
    }

    private fun matchPrefixWithPrefixesRanges(
        prefix: String,
        prefixesRanges: Array<IntRange>
    ): Boolean {
        prefixesRanges.forEach { prefixesRange ->
            if (matchPrefixWithPrefixesRange(prefix, prefixesRange)) {
                return true
            }
        }
        return false
    }

    private fun matchPrefixWithPrefixesRange(
        prefix: String,
        prefixesRange: IntRange
    ): Boolean =
        prefixesRange
            .map { it.toString() }
            .firstOrNull {
                it.startsWith(prefix) || prefix.startsWith(it)
            } != null

    companion object {
        private const val SCHEME_PREFIX_LENGTH = 4
        private val VISA_PREFIXES_RANGES = arrayOf(4..4)
        private val AMEX_PREFIXES_RANGES = arrayOf(34..34, 37..37)
        private val JCB_PREFIXES_RANGES = arrayOf(3528..3589)
        private val MASTER_CARD_PREFIXES_RANGES = arrayOf(51..55, 2221..2720)
    }
}