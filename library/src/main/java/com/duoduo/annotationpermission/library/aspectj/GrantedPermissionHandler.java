package com.duoduo.annotationpermission.library.aspectj;

import com.duoduo.annotationpermission.library.annotation.OnGrantedPermission;
import com.duoduo.annotationpermission.library.data.ClassInfoCache;
import com.duoduo.annotationpermission.library.entity.GrantedPermissionEntity;
import com.duoduo.annotationpermission.library.utils.ReflectUtils;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class GrantedPermissionHandler {

    /**
     * 处理授权通过
     *
     * @param joinPoint
     * @param permissions
     */
    public static boolean handleGrantedPermission(ProceedingJoinPoint joinPoint, int requestCode
            , String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            ClassInfoCache cache = ClassInfoCache.getInstance();
            Method method = cache.getClassHandleGrantedPermissionMethod(targetObjectClass);
            if (method != null) {
                method.setAccessible(true);
                GrantedPermissionEntity entity = new GrantedPermissionEntity();
                entity.setPermissions(permissions);
                entity.setRequestCode(requestCode);
                method.invoke(targetObject, entity);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
