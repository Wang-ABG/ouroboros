# ArXivAI ProGuard Rules

# Keep Retrofit and OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep SimpleXML
-keep class org.simpleframework.** { *; }
-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}
-keep class * implements org.simpleframework.xml.core.Validator { *; }

# Keep Room entities
-keep class com.arxivai.data.local.entity.** { *; }
-keep class com.arxivai.data.remote.** { *; }

# Keep Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# General
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}