# AnnotationPermission
AnnotationPermission

# How to use it?
1.Config the AspectJ plugin
```groovy
(1)Add AspectJ dependency in the project build.gradle
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
2.Config the AnnotationPermission

3.Add the annotation in your code

4.Config the Proguard
