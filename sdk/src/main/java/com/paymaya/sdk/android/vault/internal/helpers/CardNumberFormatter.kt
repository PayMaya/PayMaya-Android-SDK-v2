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
