package com.paymaya.sdk.android.vault.internal

import com.paymaya.sdk.android.common.internal.extension.takeFirst

enum class CardType {
    VISA,
    MASTER_CARD,
    JCB,
    AMEX,
    UNKNOWN;
}

class CardTypeDetector {
    fun detectType(cardNumber: String): CardType {
        val cardPrefix = cardNumber.takeFirst(SCHEME_IDENTIFICATION_LENGTH)
        if (cardPrefix.isEmpty()) return CardType.UNKNOWN

        return when {
            compareCardPrefixWithIdentifier(cardPrefix, VISA_IDENTIFIER) -> CardType.VISA
            compareCardPrefixWithIdentifier(cardPrefix, AMEX_IDENTIFIER) -> CardType.AMEX
            compareCardPrefixWithIdentifier(cardPrefix, JCB_IDENTIFIER) -> CardType.JCB
            compareCardPrefixWithIdentifier(cardPrefix, MASTER_CARD_IDENTIFIER) -> CardType.MASTER_CARD
            else -> CardType.UNKNOWN
        }
    }

    private fun compareCardPrefixWithIdentifier(
        cardPrefix: String,
        cardIdentifierRange: Array<IntRange>
    ): Boolean {
        cardIdentifierRange.forEach { identifierRange ->
            if (identifierRange.contains(cardPrefix.toInt())) {
                return true
            }
            if (checkPrefixMatchesCardIdentifier(cardPrefix, identifierRange)) {
                return true
            }
        }
        return false
    }

    private fun checkPrefixMatchesCardIdentifier(
        prefixNumber: String,
        identifierRange: IntRange
    ): Boolean =
        identifierRange
            .map { it.toString() }
            .firstOrNull {
                it.startsWith(prefixNumber) || prefixNumber.startsWith(it)
            } != null

    companion object {
        private const val SCHEME_IDENTIFICATION_LENGTH = 4
        private val VISA_IDENTIFIER = arrayOf(4..4)
        private val AMEX_IDENTIFIER = arrayOf(34..34, 37..37)
        private val JCB_IDENTIFIER = arrayOf(3528..3589)
        private val MASTER_CARD_IDENTIFIER = arrayOf(51..55, 2221..2720)
    }
}