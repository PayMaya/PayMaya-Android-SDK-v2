package com.paymaya.sdk.android.checkout

import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.checkout.internal.CheckoutRepository
import com.paymaya.sdk.android.common.PayMayaEnvironment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test

class CheckoutRepositoryTest {

    private lateinit var json: Json

    @Before
    fun setup() {
        json = Json(JsonConfiguration.Stable)
    }

    @Test
    fun sandbox() {
        val repository =
            CheckoutRepository(PayMayaEnvironment.SANDBOX, CLIENT_PUBLIC_KEY, json, httpClient = OkHttpClient())

        assert(repository.baseUrl == BuildConfig.API_CHECKOUT_BASE_URL_SANDBOX)
    }

    @Test
    fun production() {
        val repository =
            CheckoutRepository(PayMayaEnvironment.PRODUCTION, CLIENT_PUBLIC_KEY, json, httpClient = OkHttpClient())

        assert(repository.baseUrl == BuildConfig.API_CHECKOUT_BASE_URL_PRODUCTION)
    }

    companion object {
        private const val CLIENT_PUBLIC_KEY = "SOME KEY"
    }
}