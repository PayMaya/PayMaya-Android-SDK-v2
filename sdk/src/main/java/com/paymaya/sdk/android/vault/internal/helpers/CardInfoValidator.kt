package com.paymaya.sdk.android.vault.internal.helpers

import java.util.*

internal class CardInfoValidator(
    private val today: Calendar
) {

    fun validateNumber(value: String): Boolean =
        value.length in 16..19 && checkLuhnChecksum(value)

    fun validateDateFormat(value: String): Boolean =
        value.length == 5 && value[2] == '/' // MM/YY

    fun validateMonth(value: String): Boolean =
        value.length in 1..2 &&
                value.toIntOrNull() != null &&
                value.toInt() in 1..12

    fun validateYear(value: String): Boolean =
        value.length == 2 &&
                value.toIntOrNull() != null

    fun validateCvc(value: String): Boolean =
        value.length in 3..4 &&
                value.toIntOrNull() != null

    fun validateFutureDate(month: String, year: String): Boolean {
        require(month.length in 1..2)
        require(year.length == 4)

        val todayAdjusted = today.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val expiration = Calendar.getInstance().apply {
            set(Calendar.YEAR, year.toInt())
            set(Calendar.MONTH, month.toInt() - 1) // month numbers starts from 0
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR, 1) // set to 1, so milliseconds don't count
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        return todayAdjusted.before(expiration)
    }

    private fun checkLuhnChecksum(input: String): Boolean =
        checksum(input) % 10 == 0

    private fun checksum(input: String) =
        addends(input).sum()

    private fun addends(input: String) =
        input.digits().mapIndexed { i, j ->
            when {
                (input.length - i + 1) % 2 == 0 -> j
                j >= 5 -> j * 2 - 9
                else -> j * 2
            }
        }

    private fun String.digits(): List<Int> =
        this.map(Character::getNumericValue)
}