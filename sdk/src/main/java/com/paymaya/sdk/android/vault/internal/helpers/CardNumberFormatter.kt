package com.paymaya.sdk.android.vault.internal.helpers

internal class CardNumberFormatter : Formatter() {
    override val separator: Char
        get() = SEPARATOR

    override val lengthWithSeparators: Int
        get() = CARD_NUMBER_LENGTH_WITH_SEPARATORS

    override fun format(text: String, cursorPosition: Int): Pair<String, Int> =
        formatWithBlocks(
            text = text,
            separator = SEPARATOR,
            cursorPosition = cursorPosition,
            blockSize = CARD_NUMBER_BLOCK_SIZE,
            totalLength = CARD_NUMBER_LENGTH
        )

    companion object {
        private const val SEPARATOR = ' '
        private const val CARD_NUMBER_LENGTH = 19
        private const val CARD_NUMBER_LENGTH_WITH_SEPARATORS = 23
        private const val CARD_NUMBER_BLOCK_SIZE = 4
    }
}
