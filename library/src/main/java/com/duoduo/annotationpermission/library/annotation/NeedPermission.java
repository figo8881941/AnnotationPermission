package com.duoduo.annotationpermission.library.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * 权限请求注解
 */
public @interface NeedPermission {
    /**
     * 需要检查的权限
     */
    String[] permissions();

    /**
     * 是否忽略向用户展示权限说明对话框
     * @return
     */
    boolean ignoreShowRationale() default false;

    /**
     * 请求码
     * @return
     */
    int requestCode() default -1;

    /**
     * 当授权拒绝时，是否继续执行原方法
     * @return
     */
    boolean continueWhenDenied() default false;

    /**
     * 一次程序运行，是否只进行一次权限检查
     * @return
     */
    boolean once() default false;

    /**
     * 是否有获得授权后的回调处理方法
     * 在默认的情况下，如果获得了授权，应该继续执行原来的方法
     * 如果该设置返回true，则会查看是否有@OnGrantedPermission标注的方法
     * 如果有，就回调该方法，不继续执行原来的方法
     * 如果没有，就继续执行原来的方法
     * @return
     */
    boolean hasGrantedCallback() default false;
}
