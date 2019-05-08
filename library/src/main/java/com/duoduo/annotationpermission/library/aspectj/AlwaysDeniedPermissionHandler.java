package com.duoduo.annotationpermission.library.aspectj;

import com.duoduo.annotationpermission.library.data.ClassInfoCache;
import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutor;
import com.duoduo.annotationpermission.library.entity.AlwaysDeniedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.DeniedPermissionEntity;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class AlwaysDeniedPermissionHandler {


    /**
     * 处理授权拒绝并不再提示
     *
     * @param joinPoint
     * @param permissions
     */
    public static boolean handleAlwaysDeniedPermission(ProceedingJoinPoint joinPoint, int requestCode
            , AlwaysDeniedExecutor executor, String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            ClassInfoCache cache = ClassInfoCache.getInstance();
            Method method = cache.getClassHandleAlwaysDeniedPermissionMethod(targetObjectClass);
            if (method != null) {
                method.setAccessible(true);
                AlwaysDeniedPermissionEntity entity = new AlwaysDeniedPermissionEntity();
                entity.setPermissions(permissions);
                entity.setRequestCode(requestCode);
                entity.setExecutor(executor);
                method.invoke(targetObject, entity);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
