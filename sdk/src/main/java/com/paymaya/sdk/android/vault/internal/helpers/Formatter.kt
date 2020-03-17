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
