package com.printit.mobile.core.util

import com.printit.mobile.BuildConfig

object Constants {
    val BASE_URL: String = BuildConfig.PRINTIT_API_BASE_URL.ensureTrailingSlash()

    val GOOGLE_OAUTH_URL: String =
        BuildConfig.PRINTIT_OAUTH_BASE_URL.ensureTrailingSlash() + "api/auth/google/mobile"

    private fun String.ensureTrailingSlash(): String {
        return if (endsWith("/")) this else "$this/"
    }
}
