package com.paymaya.sdk.android.vault.internal.helpers

internal class DateFormatter : Formatter() {
    override val separator: Char
        get() = SEPARATOR

    override val lengthWithSeparators: Int
        get() = DATE_LENGTH_WITH_SEPARATORS

    override fun format(text: String, cursorPosition: Int): Pair<String, Int> =
        formatWithBlocks(
            text = text,
            separator = SEPARATOR,
            cursorPosition = cursorPosition,
            blockSize = DATE_BLOCK_SIZE,
            totalLength = DATE_LENGTH
        )

    companion object {
        private const val SEPARATOR = '/'
        private const val DATE_LENGTH = 4
        private const val DATE_LENGTH_WITH_SEPARATORS = 5
        private const val DATE_BLOCK_SIZE = 2
    }
}
