# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class com.lssgoo.planner.data.model.** { *; }

# AWS SDK rules
-keep class com.amazonaws.** { *; }
-keep class com.amazonaws.services.s3.** { *; }
-dontwarn com.amazonaws.**

# Jetpack Compose rules (usually handled by AGP, but added for safety)
-keep class androidx.compose.** { *; }

# Prevent obfuscation of BuildConfig
-keep class com.lssgoo.planner.BuildConfig { *; }