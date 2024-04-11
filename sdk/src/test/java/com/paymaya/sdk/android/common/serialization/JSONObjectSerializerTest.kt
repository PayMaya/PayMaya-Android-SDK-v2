package com.paymaya.sdk.android.common.serialization

import io.github.nomisrev.JsonPath
import io.github.nomisrev.int
import io.github.nomisrev.path
import io.github.nomisrev.string
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.junit.Test

class JSONObjectSerializerTest {

    @Serializable
    data class SerializerTest(
        @Serializable(with = JSONObjectSerializer::class)
        val jsonObject: JSONObject? = null
    )

    @Test
    fun serialize() {
        val testObject = SerializerTest(jsonObject = JSONObject("{\"test1\":\"value\", \"test2\":{\"test3\":1}}"))

        val result = Json.encodeToString(SerializerTest.serializer(), testObject)
        val deserialized = Json.parseToJsonElement(result)
        assert(JsonPath.path("jsonObject.test1").string.getOrNull(deserialized) == "value")
        assert(JsonPath.path("jsonObject.test2.test3").int.getOrNull(deserialized) == 1)
    }

    @Test
    fun deserialize() {
        val result = Json.decodeFromString(SerializerTest.serializer(), "{\"jsonObject\":{\"test1\":\"value\", \"test2\":{\"test3\":1}}}")

        assert(result.jsonObject?.getString("test1") == "value")
        assert(result.jsonObject?.getJSONObject("test2")?.getInt("test3") == 1)
    }
}