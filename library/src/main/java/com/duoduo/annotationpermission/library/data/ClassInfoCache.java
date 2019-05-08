package com.duoduo.annotationpermission.library.data;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ClassInfoCache {

    private static ClassInfoCache sInstance;

    private HashMap<Class, ClassMethodInfo> infos = new HashMap<Class, ClassMethodInfo>();

    private ClassInfoCache() {

    }

    public static ClassInfoCache getInstance() {
        if (sInstance == null) {
            synchronized (ClassInfoCache.class) {
                if (sInstance == null) {
                    sInstance = new ClassInfoCache();
                }
            }
        }
        return sInstance;
    }

    public Method getClassHandleGrantedPermissionMethod(Class targetClass) {
        Method method = null;
        if (targetClass == null) {
            return method;
        }
        synchronized (ClassInfoCache.class) {
            ClassMethodInfo info = getAndCreateInfo(targetClass);
            if (info != null) {
                method = info.getOnGrantedPermissionMethod();
                if (method != null) {
                    return method;
                }
            }
            method = MethodFinder.findClassHandleGrantedPermissionMethod(targetClass);
            info.setOnGrantedPermissionMethod(method);
        }
        return method;
    }

    public Method getClassHandleDeniedPermissionMethod(Class targetClass) {
        Method method = null;
        if (targetClass == null) {
            return method;
        }
        synchronized (ClassInfoCache.class) {
            ClassMethodInfo info = getAndCreateInfo(targetClass);
            if (info != null) {
                method = info.getOnDeniedPermissionMethod();
                if (method != null) {
                    return method;
                }
            }
            method = MethodFinder.findClassHandleDeniedPermissionMethod(targetClass);
            info.setOnDeniedPermissionMethod(method);
        }
        return method;
    }

    public Method getClassHandleAlwaysDeniedPermissionMethod(Class targetClass) {
        Method method = null;
        if (targetClass == null) {
            return method;
        }
        synchronized (ClassInfoCache.class) {
            ClassMethodInfo info = getAndCreateInfo(targetClass);
            if (info != null) {
                method = info.getOnAlwaysDeniedPermissionMethod();
                if (method != null) {
                    return method;
                }
            }
            method = MethodFinder.findClassHandleAlwaysDeniedPermissionMethod(targetClass);
            info.setOnAlwaysDeniedPermissionMethod(method);
        }
        return method;
    }

    public Method getClassHandleShowRationableMethod(Class targetClass) {
        Method method = null;
        if (targetClass == null) {
            return method;
        }
        synchronized (ClassInfoCache.class) {
            ClassMethodInfo info = getAndCreateInfo(targetClass);
            if (info != null) {
                method = info.getOnShowRationableMethod();
                if (method != null) {
                    return method;
                }
            }
            method = MethodFinder.findClassHandleShowRationableMethod(targetClass);
            info.setOnShowRationableMethod(method);
        }
        return method;
    }

    private ClassMethodInfo getAndCreateInfo(Class targetClass) {
        ClassMethodInfo info = infos.get(targetClass);
        if (info == null) {
            info = new ClassMethodInfo();
            info.setClassObject(targetClass);
            infos.put(targetClass, info);
        }
        return info;
    }
}
