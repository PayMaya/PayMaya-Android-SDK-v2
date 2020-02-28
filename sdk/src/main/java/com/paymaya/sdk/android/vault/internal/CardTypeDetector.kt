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

        val prefixNumber = cardPrefix.toInt()
        return when {
            compareCardPrefixWithIdentifier(prefixNumber, VISA_IDENTIFIER) -> CardType.VISA
            compareCardPrefixWithIdentifier(prefixNumber, MASTER_CARD_IDENTIFIER) -> CardType.MASTER_CARD
            compareCardPrefixWithIdentifier(prefixNumber, JCB_IDENTIFIER) -> CardType.JCB
            compareCardPrefixWithIdentifier(prefixNumber, AMEX_IDENTIFIER) -> CardType.AMEX
            else -> CardType.UNKNOWN
        }
    }

    private fun compareCardPrefixWithIdentifier(
        prefixNumber: Int,
        cardIdentifierRange: Array<IntRange>
    ): Boolean {
        cardIdentifierRange.forEach { identifierRange ->
            if (identifierRange.contains(prefixNumber)) {
                return true
            } else {
                if (prefixConsistentWithCardIdentifier(prefixNumber, identifierRange)) {
                    return true
                }
            }
        }
        return false
    }

    private fun prefixConsistentWithCardIdentifier(
        prefixNumber: Int,
        identifierRange: IntRange
    ): Boolean =
        identifierRange
            .map { it.toString() }
            .firstOrNull {
                it.startsWith(prefixNumber.toString()) || prefixNumber.toString().startsWith(it)
            } != null

    companion object {
        private const val SCHEME_IDENTIFICATION_LENGTH = 4
        private val VISA_IDENTIFIER = arrayOf(4..4)
        private val MASTER_CARD_IDENTIFIER = arrayOf(51..55, 2221..2720)
        private val JCB_IDENTIFIER = arrayOf(3528..3589)
        private val AMEX_IDENTIFIER = arrayOf(34..34, 37..37)
    }
}