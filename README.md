# AnnotationPermission
AnnotationPermission

# How to use it?
1.Config the AspectJ plugin

* Add AspectJ dependency in the project build.gradle file
```gradle
    buildscript {
        repositories {
            google()
            jcenter()
        }
        dependencies {
            ......
            //aspectj
            classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
        }
    }
```
* Apply AspectJ plugin in the app module(the application module) build.gradle file
```gradle
apply plugin: 'com.android.application'
//aspectj plugin
apply plugin: 'android-aspectjx'

android {
    compileSdkVersion 27
    defaultConfig {
        applicationId "com.duoduo.demo"
        minSdkVersion 14
        targetSdkVersion 27
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
       .....
    }
}
```
2.Config the AnnotationPermission

3.Add the annotation in your code

4.Config the Proguard
```progurad
#Keep Annotation
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

#Keep AndPermission
-dontwarn com.yanzhenjie.permission.**

#Keep AnnotationPermission
-keep @com.duoduo.annotationpermission.library.annotation.AnnotationPermission class * {*;}
-keepclassmembers class * {
    @com.duoduo.annotationpermission.library.annotation.NeedPermission <methods>;
    @com.duoduo.annotationpermission.library.annotation.OnDeniedPermission <methods>;
    @com.duoduo.annotationpermission.library.annotation.OnShowRationable <methods>;
}
```
License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
