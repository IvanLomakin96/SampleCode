# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontoptimize
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * implements java.io.Serializable
-keep public class com.airwatch.log.eventreporting.** {
    *;
}
-keep public class com.airwatch.login.ui.jsonmodel.** {
 *;
 }
 -keepattributes *Annotation*

-keep public class com.airwatch.crypto.openssl.OpenSSLWrapper
-keepclassmembers public class com.airwatch.crypto.openssl.OpenSSLWrapper {
  <fields>;
  <methods>;
}

-keep class com.airwatch.app.** {*;}

-keep class org.jcodec.** { *; }
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }


-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

-keep public class com.airwatch.core.AirWatchDevice
-keepclassmembers public class com.airwatch.core.AirWatchDevice {
  <fields>;
  <methods>;
}

-dontwarn org.spongycastle.**
-keep class org.spongycastle.** {
  public protected *;
}

-keep class net.sqlcipher.** {
    *;
}

-keep class org.apache.commons.** { *; }

-keep public class com.airwatch.sdk.profile.AnalyticsEvent {
  <fields>;
  <methods>;
}

-keep public class com.airwatch.sdk.profile.AnalyticsEventQueue {
  <fields>;
  <methods>;
}

-keep public class com.airwatch.sdk.webkit.AWSdkWebViewClient {
  <fields>;
  <methods>;
}

-keep class com.airwatch.clipboard.CopyPasteDelegate {*; }

-keep public class android.webkit.WebViewClientClassicExt {
    *;
}
-keep public class android.webkit.ClientCertRequestHandler {
    *;
}

-keep public class com.aw.repackage.org.apache.** {
  <fields>;
  <methods>;
}

-ignorewarnings

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * extends android.webkit.WebChromeClient {
	public void openFileChooser(android.webkit.ValueCallback);
	public void openFileChooser(android.webkit.ValueCallback, java.lang.String);
	public void openFileChooser(android.webkit.ValueCallback, java.lang.String, java.lang.String);
}

-assumenosideeffects class com.airwatch.util.Logger {
    public static *** d(...);
    public static *** v(...);
}

-keep class com.btr.proxy.selector.pac.** {
	*;
}

-dontwarn org.mozilla.**
-keep class org.mozilla.** {
	*;
}

-keep class java.lang.** {
	*;
}

-dontwarn io.netty.**

-keepattributes Signature, InnerClasses, *Annotation*
-keepclasseswithmembers class io.netty.** {
    *;
}
-keepnames class io.netty.** {
    *;
}

-dontwarn org.apache.log4j.**

-keep class org.apache.log4j.** {
 *;
 }

 -keep class androidx.security.crypto.** {
  *;
  }

-dontwarn org.littleshoot.proxy.**
-keep class org.littleshoot.proxy.** {
	*;
}

-dontwarn com.google.common.**

-dontwarn com.aw.repackage.org.apache.**
-keep class com.aw.repackage.org.apache.** {
	*;
}

-keep class android.support.v7.** {
    *;
}
-keep interface android.support.v7.** {
    *;
}

-keep class android.support.v4.** {
    *;
}
-keep interface android.support.v4.** {
    *;
}

-keep public class com.airwatch.util.DeviceCompromiseUtility {
    public *;
}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keep public class com.airwatch.login.ui.jsonmodel.** {
*;
}

-keep public class com.airwatch.log.eventreporting.** {
    *;
}

-keepattributes SourceFile, LineNumberTable

-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

-keep public class com.airwatch.signaturevalidation.**{
*;
}
