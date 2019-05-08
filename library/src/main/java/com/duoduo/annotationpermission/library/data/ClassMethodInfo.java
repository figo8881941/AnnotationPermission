package com.duoduo.annotationpermission.library.data;

import java.lang.reflect.Method;

public class ClassMethodInfo {

    private Class classObject;

    private Method onGrantedPermissionMethod;

    private Method onDeniedPermissionMethod;

    private Method onAlwaysDeniedPermissionMethod;

    private Method onShowRationableMethod;

    public Class getClassObject() {
        return classObject;
    }

    public void setClassObject(Class classObject) {
        this.classObject = classObject;
    }

    public Method getOnGrantedPermissionMethod() {
        return onGrantedPermissionMethod;
    }

    public void setOnGrantedPermissionMethod(Method onGrantedPermissionMethod) {
        this.onGrantedPermissionMethod = onGrantedPermissionMethod;
    }

    public Method getOnDeniedPermissionMethod() {
        return onDeniedPermissionMethod;
    }

    public void setOnDeniedPermissionMethod(Method onDeniedPermissionMethod) {
        this.onDeniedPermissionMethod = onDeniedPermissionMethod;
    }

    public Method getOnAlwaysDeniedPermissionMethod() {
        return onAlwaysDeniedPermissionMethod;
    }

    public void setOnAlwaysDeniedPermissionMethod(Method onAlwaysDeniedPermissionMethod) {
        this.onAlwaysDeniedPermissionMethod = onAlwaysDeniedPermissionMethod;
    }

    public Method getOnShowRationableMethod() {
        return onShowRationableMethod;
    }

    public void setOnShowRationableMethod(Method onShowRationableMethod) {
        this.onShowRationableMethod = onShowRationableMethod;
    }
}
