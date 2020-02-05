package com.paymaya.sdk.android.common

import android.os.Parcel
import kotlinx.android.parcel.Parceler
import org.json.JSONObject

object JSONObjectParceler : Parceler<JSONObject?> {
    override fun create(parcel: Parcel): JSONObject? =
        parcel.readString()?.let {
            JSONObject(it)
        }

    override fun JSONObject?.write(parcel: Parcel, flags: Int) {
        this?.let {
            parcel.writeString(it.toString())
        }
    }
}
