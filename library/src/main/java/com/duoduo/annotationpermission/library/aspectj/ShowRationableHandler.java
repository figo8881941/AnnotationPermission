package com.duoduo.annotationpermission.library.aspectj;

import com.duoduo.annotationpermission.library.data.ClassInfoCache;
import com.duoduo.annotationpermission.library.entity.ShowRationaleEntity;
import com.yanzhenjie.permission.RequestExecutor;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class ShowRationableHandler {


    /**
     * 处理展示权限说明对话框
     *
     * @param joinPoint
     * @param executor
     * @param permissions
     */
    public static boolean handleShowRationable(ProceedingJoinPoint joinPoint, int requestCode, RequestExecutor executor, String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            ClassInfoCache cache = ClassInfoCache.getInstance();
            Method method = cache.getClassHandleShowRationableMethod(targetObjectClass);
            if (method != null) {
                method.setAccessible(true);
                ShowRationaleEntity entity = new ShowRationaleEntity();
                entity.setPermissions(permissions);
                entity.setExecutor(executor);
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
