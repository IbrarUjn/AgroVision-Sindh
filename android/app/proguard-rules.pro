# Add project specific ProGuard rules here.
# Keep OkHttp classes
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
# Keep Gson classes
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
# Keep our model class
-keep class com.agrovision.sindh.PredictionResult { *; }
-keep class com.agrovision.sindh.PredictionResult$Top3Item { *; }
