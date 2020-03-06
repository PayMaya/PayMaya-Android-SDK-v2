package com.paymaya.sdk.android.vault.internal.helpers

internal abstract class Formatter {
    abstract val separator: Char

    abstract val lengthWithSeparators: Int

    abstract fun format(text: String, cursorPosition: Int): Pair<String, Int>

    protected fun formatWithBlocks(
        text: String,
        separator: Char,
        cursorPosition: Int,
        blockSize: Int,
        totalLength: Int
    ): Pair<String, Int> {
        val sb = StringBuilder()
        var separatorsBeforeCursorCount = 0

        for (position in text.indices) {
            sb.append(text[position])

            val nextPosition = position + 1
            val isEndOfBlock = (nextPosition % blockSize) == 0

            if (isEndOfBlock && (nextPosition < totalLength)) {
                sb.append(separator)
                if (nextPosition <= cursorPosition) {
                    ++separatorsBeforeCursorCount
                }
            }
        }
        return Pair(sb.toString(), separatorsBeforeCursorCount)
    }
}
