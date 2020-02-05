package com.paymaya.sdk.android

import com.paymaya.sdk.android.internal.PayMayaGatewayRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test

class PayMayaGatewayRepositoryTest {

    private lateinit var json: Json

    @Before
    fun setup() {
        json = Json(JsonConfiguration.Stable)
    }

    @Test
    fun sandbox() {
        val repository =
            PayMayaGatewayRepository(PayMayaEnvironment.SANDBOX, CLIENT_KEY, json, httpClient = OkHttpClient())

        assert(repository.checkoutBaseUrl == BuildConfig.API_CHECKOUT_BASE_URL_SANDBOX)
    }

    @Test
    fun production() {
        val repository =
            PayMayaGatewayRepository(PayMayaEnvironment.PRODUCTION, CLIENT_KEY, json, httpClient = OkHttpClient())

        assert(repository.checkoutBaseUrl == BuildConfig.API_CHECKOUT_BASE_URL_PRODUCTION)
    }

    companion object {
        private const val CLIENT_KEY = "SOME KEY"
    }
}