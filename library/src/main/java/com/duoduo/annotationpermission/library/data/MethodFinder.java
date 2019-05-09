package com.duoduo.annotationpermission.library.data;

import com.duoduo.annotationpermission.library.annotation.OnAlwaysDeniedPermission;
import com.duoduo.annotationpermission.library.annotation.OnDeniedPermission;
import com.duoduo.annotationpermission.library.annotation.OnGrantedPermission;
import com.duoduo.annotationpermission.library.annotation.OnShowRationable;
import com.duoduo.annotationpermission.library.entity.AlwaysDeniedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.DeniedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.GrantedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.ShowRationaleEntity;
import com.duoduo.annotationpermission.library.utils.ReflectUtils;

import java.lang.reflect.Method;

public class MethodFinder {

    public static Method findClassHandleGrantedPermissionMethod(Class targetClass) {
        if (targetClass == null) {
            return null;
        }
        Method method = ReflectUtils.getMethodByAnnotation(targetClass, OnGrantedPermission.class);
        if (method != null) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length != 1 ||
                    !parameterTypes[0].equals(GrantedPermissionEntity.class)) {
                method = null;
            }
        }
        return method;
    }

    public static Method findClassHandleDeniedPermissionMethod(Class targetClass) {
        if (targetClass == null) {
            return null;
        }
        Method method = ReflectUtils.getMethodByAnnotation(targetClass, OnDeniedPermission.class);
        if (method != null) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length != 1 ||
                    !parameterTypes[0].equals(DeniedPermissionEntity.class)) {
                method = null;
            }
        }
        return method;
    }

    public static Method findClassHandleAlwaysDeniedPermissionMethod(Class targetClass) {
        if (targetClass == null) {
            return null;
        }
        Method method = ReflectUtils.getMethodByAnnotation(targetClass, OnAlwaysDeniedPermission.class);
        if (method != null) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length != 1 ||
                    !parameterTypes[0].equals(AlwaysDeniedPermissionEntity.class)) {
                method = null;
            }
        }
        return method;
    }

    public static Method findClassHandleShowRationableMethod(Class targetClass) {
        if (targetClass == null) {
            return null;
        }
        Method method = ReflectUtils.getMethodByAnnotation(targetClass, OnShowRationable.class);
        if (method != null) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length != 1 ||
                    !parameterTypes[0].equals(ShowRationaleEntity.class)) {
                method = null;
            }
        }
        return method;
    }
}
