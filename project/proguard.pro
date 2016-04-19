-ignorewarnings

-keepparameternames
-keepattributes Exceptions,InnerClasses,Signature,*Annotation*,EnclosingMethod
-keep class scala.Dynamic

#-printseeds  logs/printseeds
#-printusage  logs/printusage

-keep class retrofit.** { *; }
-keepclasseswithmembers class * { @retrofit.http.* <methods>; }
-keepclasseswithmembers class * { @com.fasterxml.jackson.annotation.* <methods>; }
-keep class scala.reflect.ScalaSignature { *; }

-dontwarn retrofit.**
-dontwarn javax.**
-dontwarn com.squareup.okhttp.**
