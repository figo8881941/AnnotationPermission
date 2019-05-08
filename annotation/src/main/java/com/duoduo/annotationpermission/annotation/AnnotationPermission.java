package com.duoduo.annotationpermission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * AnnotationPermission
 * 用于标记使用AnnotationPermission的类
 */
public @interface AnnotationPermission {
}
