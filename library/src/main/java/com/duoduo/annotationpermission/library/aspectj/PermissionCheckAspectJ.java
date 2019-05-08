package com.duoduo.annotationpermission.library.aspectj;

import android.content.Context;

import com.duoduo.annotationpermission.annotation.NeedPermission;
import com.duoduo.annotationpermission.library.context.ContextFinder;
import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutor;
import com.duoduo.annotationpermission.library.listener.ICheckAndRequestPermissionListener;
import com.duoduo.annotationpermission.library.utils.PermissionUtils;
import com.yanzhenjie.permission.RequestExecutor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.HashSet;

/**
 * 权限检查注解的AOP处理
 */
@Aspect
public class PermissionCheckAspectJ {

    private static HashSet<Method> onceMethodRecord = new HashSet<Method>();

    private final String TAG = "PermissionCheckAspectJ";

    @Pointcut("execution(@com.duoduo.annotationpermission.annotation.NeedPermission * *(..))")
    public void needPermission() {
    }

    @Around("needPermission()")
    public void checkPermission(final ProceedingJoinPoint joinPoint) throws Throwable {

        //尝试获取Context
        final Context context = ContextFinder.findContext(joinPoint);

        //获取注解参数
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        NeedPermission needPermission = method.getAnnotation(NeedPermission.class);
        String[] permissons = needPermission.permissions();
        final boolean ignoreShowRationale = needPermission.ignoreShowRationale();
        final int requestCode = needPermission.requestCode();
        final boolean continueWhenDenied = needPermission.continueWhenDenied();
        final boolean once = needPermission.once();
        final boolean hasGrantedCallback = needPermission.hasGrantedCallback();
        final boolean hasAlwaysDeniedCallback = needPermission.hasAlwaysDeniedCallback();

        //处理只进行一次权限检查的方法
        if (once) {
            if (methodHasCheckPermissionsOnce(method)) {
                //已经检查过一次，直接执行
                joinPoint.proceed();
                return;
            }
        }

        addMethodOnceRecord(method);

        //如果context为空，走授权失败
        if (context == null) {
            if (continueWhenDenied) {
                joinPoint.proceed();
            } else {
                DeniedPermissionHandler.handleDeniedPermission(joinPoint, requestCode, permissons);
            }
            return;
        }

        //调用工具方法进行权限检查
        PermissionUtils.checkAndRequestPermission(context, new ICheckAndRequestPermissionListener() {
            @Override
            public void onGrantedPermission(String... permissions) {
                //已授权
                if (!hasGrantedCallback || !GrantedPermissionHandler.handleGrantedPermission(joinPoint, requestCode, permissions)) {
                    //如果没有授权回调处理方法或者对应的方法没有处理成功，则继续执行原来的方法
                    try {
                        joinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }

            @Override
            public void onDeniedPermission(String... permissions) {
                //授权拒绝
                if (continueWhenDenied) {
                    try {
                        joinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else {
                    DeniedPermissionHandler.handleDeniedPermission(joinPoint, requestCode, permissions);
                }
            }

            @Override
            public void onAlwaysDeniedPermission(AlwaysDeniedExecutor executor, String... permissions) {
                if (!hasAlwaysDeniedCallback || !AlwaysDeniedPermissionHandler.handleAlwaysDeniedPermission(joinPoint, requestCode, executor, permissions)) {
                    executor.execute();
                }
            }

            @Override
            public void onShowRationale(RequestExecutor executor, String... permissions) {
                if (ignoreShowRationale || !ShowRationableHandler.handleShowRationable(joinPoint, requestCode, executor, permissions)) {
                    //如果忽略或者是对应的处理方法没有处理成功，直接再次申请权限
                    executor.execute();
                }
            }
        }, permissons);
    }

    /**
     * 方法是否已经进行过一次权限检查
     *
     * @param targetMethod
     * @return
     */
    private boolean methodHasCheckPermissionsOnce(Method targetMethod) {
        synchronized (onceMethodRecord) {
            return onceMethodRecord.contains(targetMethod);
        }
    }

    /**
     * 把方法添加到进行过一次权限检查的记录里面
     */
    private void addMethodOnceRecord(Method targetMethod) {
        synchronized (onceMethodRecord) {
            onceMethodRecord.add(targetMethod);
        }
    }

}
