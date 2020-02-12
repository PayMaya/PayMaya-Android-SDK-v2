package com.paymaya.sdk.android.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import org.json.JSONObject

@Serializer(forClass = JSONObject::class)
object JSONObjectSerializer : KSerializer<JSONObject> {
    override val descriptor: SerialDescriptor =
        StringDescriptor

    override fun serialize(encoder: Encoder, obj: JSONObject) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): JSONObject {
        return JSONObject(decoder.decodeString())
    }
}
