# AnnotationPermission
AnnotationPermission

# How to use it?
1.Config the AspectJ plugin

(1)Add AspectJ dependency in the project build.gradle file
```groovy
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
(2)Apply AspectJ plugin in the app module(the application module) build.gradle file
```groovy
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
