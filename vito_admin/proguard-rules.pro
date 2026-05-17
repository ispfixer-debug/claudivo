# ProGuard rules for Vito Admin App

# Keep Kotlin metadata
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-dontnote kotlin.reflect.jvm.internal.**.*

# Kotlin Serialization
-keepattributes SourceFile,LineNumberTable
-keep class kotlinx.serialization.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Supabase
-keep class io.supabase.** { *; }
-keepclassmembers class io.supabase.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Firebase
-keepnames class com.google.firebase.** { *; }
-keep class com.google.firebase.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Vico Charts
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.maps.**

# General Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends androidx.fragment.Fragment

# Vito Models
-keep class com.vito.core.domain.model.** { *; }
-keep class com.vito.core.data.remote.dto.** { *; }