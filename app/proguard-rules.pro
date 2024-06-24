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

# Keep all public classes and methods in com.xdroid.app.changewallpaper
-keep class com.xdroid.app.changewallpaper.** { *; }

# Keep public constructors in com.xdroid.app.changewallpaper
-keepclassmembers class com.xdroid.app.changewallpaper.** {
    public <init>(...);
}

# Keep custom views and their constructors
-keep class com.xdroid.app.changewallpaper.** extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep Parcelable implementations and their CREATOR fields
-keepclassmembers class com.xdroid.app.changewallpaper.** implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all annotations
-keep interface **

# Keep classes used by reflection
-keep class com.xdroid.app.changewallpaper.** { *; }

# Preserve line number information for debugging
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name in stack traces
-renamesourcefileattribute SourceFile