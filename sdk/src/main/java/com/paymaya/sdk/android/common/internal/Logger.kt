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

