package com.paymaya.sdk.android.vault

import com.paymaya.sdk.android.vault.internal.helpers.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterTest {

    private val formatter = DateFormatter()

    @Test
    fun `no changes`() {
        val input = "1"
        val cursorPosition = 0
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("1", newText)
        assertEquals(cursorPosition, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `add separator after the first block, cursor at the beginning`() {
        val input = "12"
        val cursorPosition = 0
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("12/", newText)
        assertEquals(cursorPosition, newSeparatorsBeforeCursorCount)
    }


    @Test
    fun `add separator after the first block, cursor at the end`() {
        val input = "12"
        val cursorPosition = 2
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("12/", newText)
        assertEquals(1, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `add separator between blocks`() {
        val input = "123"
        val cursorPosition = 3
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("12/3", newText)
        assertEquals(1, newSeparatorsBeforeCursorCount)
    }

    @Test
    fun `add separator between blocks, maximum input length`() {
        val input = "1234"
        val cursorPosition = 3
        val (newText, newSeparatorsBeforeCursorCount) = formatter.format(input, cursorPosition)
        assertEquals("12/34", newText)
        assertEquals(1, newSeparatorsBeforeCursorCount)
    }
}