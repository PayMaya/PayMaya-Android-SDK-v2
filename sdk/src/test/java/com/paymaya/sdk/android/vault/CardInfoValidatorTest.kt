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

package com.paymaya.sdk.android.vault

import com.paymaya.sdk.android.vault.internal.helpers.CardInfoValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class CardInfoValidatorTest {

    private lateinit var cardInfoValidator: CardInfoValidator

    @Before
    fun setup() {
        cardInfoValidator =
            CardInfoValidator(Calendar.getInstance())
    }

    @Test
    fun `empty card number`() {
        assertFalse(cardInfoValidator.validateNumber(""))
    }

    @Test
    fun `card number too short`() {
        assertFalse(cardInfoValidator.validateNumber("1234"))
    }

    @Test
    fun `card number OK`() {
        assertTrue(cardInfoValidator.validateNumber("1234123412341238"))
    }

    @Test
    fun `card number invalid`() {
        assertFalse(cardInfoValidator.validateNumber("1234123412341234"))
    }

    @Test
    fun `card number too long`() {
        assertFalse(cardInfoValidator.validateNumber("12341234123412381234"))
    }

    @Test
    fun `CVC too short`() {
        assertFalse(cardInfoValidator.validateCvc("12"))
    }

    @Test
    fun `CVC too long`() {
        assertFalse(cardInfoValidator.validateCvc("12345"))
    }

    @Test
    fun `CVC OK (3 digits)`() {
        assertTrue(cardInfoValidator.validateCvc("123"))
    }

    @Test
    fun `CVC OK (4 digits)`() {
        assertTrue(cardInfoValidator.validateCvc("1234"))
    }

    @Test
    fun `month number invalid #1`() {
        assertFalse(cardInfoValidator.validateMonth("00"))
    }

    @Test
    fun `month number invalid #2`() {
        assertFalse(cardInfoValidator.validateMonth("13"))
    }

    @Test
    fun `month number OK #1`() {
        assertTrue(cardInfoValidator.validateMonth("11"))
    }

    @Test
    fun `month number OK #2`() {
        assertTrue(cardInfoValidator.validateMonth("2"))
    }

    @Test
    fun `year number invalid #1`() {
        assertFalse(cardInfoValidator.validateYear("0"))
    }

    @Test
    fun `year number invalid #2`() {
        assertFalse(cardInfoValidator.validateYear("222"))
    }

    @Test
    fun `year number OK`() {
        assertTrue(cardInfoValidator.validateYear("22"))
    }

    @Test
    fun `expiration date OK`() {
        val year = 2020
        val month = 5

        val date = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // month numbers starts from 0
        }

        val cardInfoValidator =
            CardInfoValidator(date)
        assertTrue(cardInfoValidator.validateFutureDate(month.toString(), year.toString()))
    }

    @Test
    fun `expiration date invalid`() {
        val year = 2020
        val month = 5

        val date = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // month numbers starts from 0
        }

        val cardInfoValidator =
            CardInfoValidator(date)
        assertFalse(cardInfoValidator.validateFutureDate((month - 1).toString(), year.toString()))
    }
}