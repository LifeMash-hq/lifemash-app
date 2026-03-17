# LifeMash ProGuard Rules

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class org.bmsk.lifemash.**$$serializer { *; }
-keepclassmembers class org.bmsk.lifemash.** {
    *** Companion;
}
-keepclasseswithmembers class org.bmsk.lifemash.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Ktor ---
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# --- Compose ---
-dontwarn androidx.compose.**

# --- Koin ---
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# --- Coil ---
-dontwarn coil3.**
