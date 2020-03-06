package com.paymaya.sdk.android.vault

import com.paymaya.sdk.android.vault.internal.helpers.CardNumberFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberFormatterTest {
    private val formatter = CardNumberFormatter()

    @Test
    fun `no changes`() {
        val input = "123"
        val cursorPosition = 3
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("123", newText)
        assertEquals(0, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `add space at the end of a block`() {
        val input = "1234"
        val cursorPosition = 3
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("1234 ", newText)
        assertEquals(0, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `cursor in the first block`() {
        val input = "12341234"
        val cursorPosition = 3
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("1234 1234 ", newText)
        assertEquals(0, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `cursor at the end`() {
        val input = "1234123412341234"
        val cursorPosition = 16
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("1234 1234 1234 1234 ", newText)
        assertEquals(4, newSeparatorsBeforeCursorCount)
    }
}