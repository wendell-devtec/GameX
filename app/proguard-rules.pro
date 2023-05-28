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

#Model class
-keep class com.tw4rs.gamex.model.AppModel { *;}
-keep class com.tw4rs.gamex.model.HistoryModel { *;}
-keep class com.tw4rs.gamex.model.EventModel { *;}
-keep class com.tw4rs.gamex.model.VideoModel { *;}
-keep class com.tw4rs.gamex.model.TotalModel {*;}
-keep class com.tw4rs.gamex.model.WithdrawModel{*;}

-keep class com.applovin.impl.adview.activity.a.c {*;}


#google ads
-keep public class com.google.android.gms.** { public protected *; }


-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
    public <init>(...);
 }


-dontwarn android.app.Activity

-keep public class com.smaato.sdk.** { *; }
-keep public interface com.smaato.sdk.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn java.beans.Beans
-dontwarn javax.security.auth.callback.NameCallback
-dontwarn javax.security.sasl.RealmCallback
-dontwarn javax.security.sasl.RealmChoiceCallback
-dontwarn javax.security.sasl.Sasl
-dontwarn javax.security.sasl.SaslClient
-dontwarn javax.security.sasl.SaslException

-dontwarn com.bytedance.JProtect



#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

-keep class com.bytedance.sdk.** { *; }
-keep class com.pgl.sys.ces.* {*;}




