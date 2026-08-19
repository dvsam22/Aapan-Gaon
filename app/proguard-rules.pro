# ==============================================================================
# ProGuard & R8 Optimization Rules for Aapan Gaon App (Production Hardened)
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. Android Core & Application Architecture
# ------------------------------------------------------------------------------
-keepattributes *Annotation*, SourceFile, LineNumberTable, Signature, InnerClasses, EnclosingMethod
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.Unit

# Android Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# Enums support
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    java.lang.reflect.Method preHandler;
}

# ------------------------------------------------------------------------------
# 2. Kotlinx Serialization & App Navigation Routes
# ------------------------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Keep serializable classes, companions, and serializers
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable class *;
}
-keepclassmembers class **$$serializer {
    public static final **$$serializer INSTANCE;
}
-keepclasseswithmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    *** Companion;
}

# Keep All Navigation Routes & Enums
-keep class com.dv.apna.core.navigation.** { *; }
-keep class com.dv.apna.core.config.** { *; }
-keep class com.dv.apna.core.ads.** { *; }
-keep class com.dv.apna.core.datastore.** { *; }

# ------------------------------------------------------------------------------
# 3. Data Models & Domain Models (Firestore / Serialization / Reflection)
# ------------------------------------------------------------------------------
# Keep entire model packages without any field or method renaming
-keep class com.dv.apna.feature.**.domain.model.** { *; }
-keep class com.dv.apna.feature.**.data.model.** { *; }
-keep class com.dv.apna.feature.**.presentation.state.** { *; }
-keep class com.dv.apna.feature.**.presentation.effect.** { *; }
-keep class com.dv.apna.feature.**.presentation.event.** { *; }

# Keep no-arg and all constructors for Firestore deserialization
-keepclassmembers class com.dv.apna.feature.**.domain.model.** {
    <init>(...);
    <fields>;
    <methods>;
}
-keepclassmembers class com.dv.apna.feature.**.data.model.** {
    <init>(...);
    <fields>;
    <methods>;
}

# ------------------------------------------------------------------------------
# 4. Hilt & Dagger
# ------------------------------------------------------------------------------
-dontwarn dagger.hilt.**
-keep class * extends dagger.hilt.internal.UnsafeCasts { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.ComponentEntryPoint { *; }
-keep class * implements dagger.hilt.android.internal.builders.ActivityComponentBuilder { *; }
-keep class * implements dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder { *; }
-keep class * implements dagger.hilt.android.internal.builders.FragmentComponentBuilder { *; }
-keep class * implements dagger.hilt.android.internal.builders.ServiceComponentBuilder { *; }
-keep class * implements dagger.hilt.android.internal.builders.ViewComponentBuilder { *; }
-keep class * implements dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder { *; }

# ------------------------------------------------------------------------------
# 5. Firebase (Firestore, Messaging, Crashlytics, RemoteConfig, Analytics)
# ------------------------------------------------------------------------------
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.remoteconfig.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.analytics.** { *; }

# ------------------------------------------------------------------------------
# 6. Google Mobile Ads (AdMob)
# ------------------------------------------------------------------------------
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep public class com.google.ads.** {
    public *;
}
-keep class com.google.android.gms.ads.nativead.NativeAdView { *; }
-keep class com.google.android.gms.ads.nativead.MediaView { *; }
-keep class com.google.android.gms.ads.nativead.NativeAd { *; }
-keep class com.google.android.gms.ads.appopen.AppOpenAd { *; }
-keep class com.google.android.gms.ads.interstitial.InterstitialAd { *; }
-dontwarn com.google.android.gms.ads.**

# ------------------------------------------------------------------------------
# 7. Google Play In-App Update
# ------------------------------------------------------------------------------
-keep class com.google.android.play.core.appupdate.** { *; }
-keep class com.google.android.play.core.install.** { *; }
-dontwarn com.google.android.play.core.**

# ------------------------------------------------------------------------------
# 8. Jetpack Compose, Coil, Lottie, SDP/SSP
# ------------------------------------------------------------------------------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

-keep class coil.** { *; }
-dontwarn coil.**

-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

-keep class com.intuit.sdp.** { *; }
-keep class com.intuit.ssp.** { *; }

# ------------------------------------------------------------------------------
# 9. AndroidX WorkManager, Room & App Startup
# ------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

-keep class androidx.work.** { *; }
-keep class androidx.work.impl.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-dontwarn androidx.work.**

-keep class androidx.startup.** { *; }
-keep class * implements androidx.startup.Initializer { *; }
-dontwarn androidx.startup.**
