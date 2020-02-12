package com.paymaya.sdk.android.paywithpaymaya

import com.paymaya.sdk.android.BuildConfig
import com.paymaya.sdk.android.common.PayMayaEnvironment
import com.paymaya.sdk.android.paywithpaymaya.internal.PayWithPayMayaRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test

class PayWithPayMayaRepositoryTest {

    private lateinit var json: Json

    @Before
    fun setup() {
        json = Json(JsonConfiguration.Stable)
    }

    @Test
    fun sandbox() {
        val repository =
            PayWithPayMayaRepository(PayMayaEnvironment.SANDBOX,
                CLIENT_KEY, json, httpClient = OkHttpClient())

        assert(repository.baseUrl == BuildConfig.API_PAY_WITH_PAYMAYA_BASE_URL_SANDBOX)
    }

    @Test
    fun production() {
        val repository =
            PayWithPayMayaRepository(PayMayaEnvironment.PRODUCTION,
                CLIENT_KEY, json, httpClient = OkHttpClient())

        assert(repository.baseUrl == BuildConfig.API_PAY_WITH_PAYMAYA_BASE_URL_PRODUCTION)
    }

    companion object {
        private const val CLIENT_KEY = "SOME KEY"
    }
}