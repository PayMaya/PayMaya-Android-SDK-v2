package com.paymaya.sdk.android.common.serialization

import android.annotation.SuppressLint
import com.paymaya.sdk.android.checkout.models.AuthorizationType
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = AuthorizationType::class)
object AuthorizationTypeSerializer {
    override val descriptor: SerialDescriptor
        get() = StringDescriptor
    @SuppressLint("DefaultLocale")
    override fun deserialize(decoder: Decoder): AuthorizationType {
        return AuthorizationType.valueOf(decoder.decodeString().toUpperCase())
    }
    @SuppressLint("DefaultLocale")
    override fun serialize(encoder: Encoder, obj: AuthorizationType) {
        encoder.encodeString(obj.name.toUpperCase())
    }
}