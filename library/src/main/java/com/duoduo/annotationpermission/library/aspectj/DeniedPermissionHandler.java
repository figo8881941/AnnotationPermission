package com.duoduo.annotationpermission.library.aspectj;

import com.duoduo.annotationpermission.library.data.ClassInfoCache;
import com.duoduo.annotationpermission.library.entity.DeniedPermissionEntity;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class DeniedPermissionHandler {

    /**
     * 处理授权拒绝
     *
     * @param joinPoint
     * @param permissions
     */
    public static boolean handleDeniedPermission(ProceedingJoinPoint joinPoint, int requestCode
            , String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            ClassInfoCache cache = ClassInfoCache.getInstance();
            Method method = cache.getClassHandleDeniedPermissionMethod(targetObjectClass);
            if (method != null) {
                method.setAccessible(true);
                DeniedPermissionEntity entity = new DeniedPermissionEntity();
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
