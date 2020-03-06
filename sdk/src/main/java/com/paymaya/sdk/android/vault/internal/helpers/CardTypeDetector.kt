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