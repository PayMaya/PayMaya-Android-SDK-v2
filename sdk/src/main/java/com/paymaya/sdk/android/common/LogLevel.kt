package com.paymaya.sdk.android.common

enum class LogLevel {
    // Includes Http request and response lines, headers and bodies
    VERBOSE,
    // Includes Http request and response lines and headers
    DEBUG,
    // Includes Http request and response lines
    INFO,
    WARN,
    ERROR,
    ASSERT
}
