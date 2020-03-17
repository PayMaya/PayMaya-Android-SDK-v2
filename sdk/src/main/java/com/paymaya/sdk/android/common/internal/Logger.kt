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

package com.paymaya.sdk.android.common.internal

import android.util.Log
import com.paymaya.sdk.android.common.LogLevel
import okhttp3.logging.HttpLoggingInterceptor

internal class Logger(
    private val logLevel: LogLevel
) {

    fun e(tag: String, message: String, t: Throwable? = null) {
        if (logLevel.ordinal <= mapToAndroidLogLevel(LogLevel.ERROR)) {
            Log.e(tag, message, t)
        }
    }

    fun w(tag: String, message: String) {
        if (logLevel.ordinal <= mapToAndroidLogLevel(LogLevel.WARN)) {
            Log.w(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (logLevel.ordinal <= mapToAndroidLogLevel(LogLevel.INFO)) {
            Log.i(tag, message)
        }
    }

    fun d(tag: String, message: String) {
        if (logLevel.ordinal <= mapToAndroidLogLevel(LogLevel.DEBUG)) {
            Log.d(tag, message)
        }
    }

    fun v(tag: String, message: String) {
        if (logLevel.ordinal <= mapToAndroidLogLevel(LogLevel.VERBOSE)) {
            Log.v(tag, message)
        }
    }

    private fun mapToAndroidLogLevel(level: LogLevel): Int =
        when (level) {
            LogLevel.VERBOSE -> Log.VERBOSE
            LogLevel.DEBUG -> Log.DEBUG
            LogLevel.INFO -> Log.INFO
            LogLevel.WARN -> Log.WARN
            LogLevel.ERROR -> Log.ERROR
            LogLevel.ASSERT -> Log.ASSERT
        }

    companion object {
        fun mapToOkHttpLogLevel(level: LogLevel): HttpLoggingInterceptor.Level =
            when (level) {
                LogLevel.VERBOSE -> HttpLoggingInterceptor.Level.BODY
                LogLevel.DEBUG -> HttpLoggingInterceptor.Level.HEADERS
                LogLevel.INFO -> HttpLoggingInterceptor.Level.BASIC
                else -> HttpLoggingInterceptor.Level.NONE
            }
    }
}
