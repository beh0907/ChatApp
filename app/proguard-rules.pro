# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Existing rules
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter { <init>(); }
-keep class com.google.googlesignin.** { *; }
-keep class retrofit2.** { *; }
-keepattributes *Annotation*

# Gemini API 관련 규칙 추가
-keep class com.google.ai.client.** { *; }
-keep class com.google.ai.generativelanguage.** { *; }

# OkHttp 관련 규칙 보강
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.** { *; }
-keepnames interface okhttp3.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okio.** { *; }

# Java Management 관련 경고 무시
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Ktor 디버그 감지 관련
-keep class io.ktor.util.debug.** { *; }
-keepclassmembers class io.ktor.util.debug.** { *; }

# 추가 안전장치
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 리플렉션 관련 규칙 보강
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# API 관련 클래스 보존
-keep class com.skymilk.chatapp.** { *; }
-keep class * implements java.io.Serializable { *; }

# Fix for R8 warnings
-keep class * implements androidx.versionedparcelable.VersionedParcelable { <init>(); }
-keep class * implements com.google.firebase.components.ComponentRegistrar { <init>(); }
-keep class * extends androidx.startup.Initializer { <init>(); }
#-keep class * implements com.bumptech.glide.module.GlideModule { <init>(); }
-keep class okhttp3.** { <init>(); }
#-keep class io.ktor.client.engine.** implements io.ktor.client.HttpClientEngineContainer { <init>(); }
-keep class * extends androidx.navigation.Navigator { <init>(); }

# Dagger Hilt
-keep,allowobfuscation,allowshrinking @dagger.hilt.EntryPoint class * { <init>(); }
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class * { <init>(); }
-keep,allowobfuscation,allowshrinking @dagger.hilt.internal.ComponentEntryPoint class * { <init>(); }
-keep,allowobfuscation,allowshrinking @dagger.hilt.internal.GeneratedEntryPoint class * { <init>(); }

# Compose
-keep,allowshrinking class * extends androidx.compose.ui.node.ModifierNodeElement { <init>(); }

# Keep annotations
-keep @interface androidx.annotation.Keep { <init>(); }
-keep @androidx.annotation.Keep class * { <init>(); }
-keep @interface com.google.android.gms.common.annotation.KeepName { <init>(); }
-keep,allowshrinking @com.google.android.gms.common.annotation.KeepName class * { <init>(); }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation { <init>(); }

# Coroutines volatile fields
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Standard library SafeContinuation
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Warnings to ignore
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal
-dontwarn java.lang.ClassValue
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Firebase and Google Play Services
-keep class com.google.android.gms.common.internal.ReflectedParcelable { <init>(); }
-keep,allowshrinking class * implements com.google.android.gms.common.internal.ReflectedParcelable { <init>(); }
-keep @interface com.google.android.gms.common.util.DynamiteApi { <init>(); }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep,allowobfuscation interface * extends retrofit2.Call { <init>(); }
-keep,allowobfuscation,allowshrinking class retrofit2.Response { <init>(); }