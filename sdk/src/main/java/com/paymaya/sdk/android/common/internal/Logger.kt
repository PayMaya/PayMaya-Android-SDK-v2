package com.paymaya.sdk.android.common.internal

import android.util.Log

// TODO JIRA PS-16
internal object Logger {
    var level: Int = Log.INFO

    fun e(tag: String, message: String, t: Throwable? = null) {
        if (level <= Log.ERROR) {
            Log.e(tag, message, t)
        }
    }

    fun w(tag: String, message: String) {
        if (level <= Log.WARN) {
            Log.w(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (level <= Log.INFO) {
            Log.i(tag, message)
        }
    }

    fun d(tag: String, message: String) {
        if (level <= Log.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun v(tag: String, message: String) {
        if (level <= Log.VERBOSE) {
            Log.v(tag, message)
        }
    }
}
