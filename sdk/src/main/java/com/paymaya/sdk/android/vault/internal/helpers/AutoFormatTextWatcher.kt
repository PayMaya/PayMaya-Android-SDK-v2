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

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

internal class AutoFormatTextWatcher(
    private val editText: EditText,
    private val formatter: Formatter
) : TextWatcher {
    private var enabledTextWatcher = true
    private var backspaceOnSeparator = false
    private var deletedSeparatorPosition: Int? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (enabledTextWatcher) {
            backspaceOnSeparator = count == 1 && after == 0 && s[start] == formatter.separator
            deletedSeparatorPosition = if (backspaceOnSeparator) start else null
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // no-op
    }

    override fun afterTextChanged(s: Editable) {
        if (!enabledTextWatcher) {
            return
        }
        enabledTextWatcher = false

        var text = s.toString()
        val cursorPosition: Int

        if (backspaceOnSeparator) {
            text = removeCharBeforeSeparator(text)
            cursorPosition = editText.selectionEnd - 1
        } else {
            cursorPosition = editText.selectionEnd
        }

        val cursorPositionNoSeparators = cursorPosition - countSeparatorsBeforePosition(text, cursorPosition)
        val textNoSeparators = removeSeparators(text)
        val (newText, newSeparatorsBeforeCursorCount) =
            formatter.format(textNoSeparators, cursorPositionNoSeparators)

        editText.setText(newText)

        // Trim new cursor position in case a longer text was pasted into the edit text (filter does not prevent it)
        val newCursorPosition = (cursorPositionNoSeparators + newSeparatorsBeforeCursorCount)
            .coerceAtMost(formatter.lengthWithSeparators)
        editText.setSelection(newCursorPosition)

        enabledTextWatcher = true
    }

    private fun removeCharBeforeSeparator(text: String): String {
        val charToBeDeletedPosition = requireNotNull(deletedSeparatorPosition) - 1
        return text.removeRange(charToBeDeletedPosition..charToBeDeletedPosition)
    }

    private fun countSeparatorsBeforePosition(text: String, cursorPosition: Int): Int =
        text.substring(0, cursorPosition)
            .count { char -> char == formatter.separator }

    private fun removeSeparators(text: String): String =
        text.replace(oldValue = "${formatter.separator}", newValue = "")
}
