-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.paymaya.sdk.android.**$$serializer { *; }
-keepclassmembers class com.paymaya.sdk.android.** {
    *** Companion;
}
-keepclasseswithmembers class com.paymaya.sdk.android.** {
    kotlinx.serialization.KSerializer serializer(...);
}
