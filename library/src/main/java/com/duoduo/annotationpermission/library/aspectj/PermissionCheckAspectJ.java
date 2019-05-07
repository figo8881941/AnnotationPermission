package com.duoduo.annotationpermission.library.aspectj;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

import com.duoduo.annotationpermission.library.ICheckAndRequestPermissionListener;
import com.duoduo.annotationpermission.library.IContextHolder;
import com.duoduo.annotationpermission.library.PermissionUtils;
import com.duoduo.annotationpermission.library.annotation.NeedPermission;
import com.duoduo.annotationpermission.library.annotation.OnDeniedPermission;
import com.duoduo.annotationpermission.library.annotation.OnGrantedPermission;
import com.duoduo.annotationpermission.library.annotation.OnShowRationable;
import com.duoduo.annotationpermission.library.entity.DeniedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.GrantedPermissionEntity;
import com.duoduo.annotationpermission.library.entity.ShowRationaleEntity;
import com.duoduo.annotationpermission.library.utils.ReflectUtils;
import com.yanzhenjie.permission.RequestExecutor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 权限检查注解的AOP处理
 */
@Aspect
public class PermissionCheckAspectJ {

    private static HashMap<Class, Method> classGrantedPermissionMethodCache = new HashMap<Class, Method>();
    private static HashMap<Class, Method> classDeniedPermissionMethodCache = new HashMap<Class, Method>();
    private static HashMap<Class, Method> classShowRationableMethodCache = new HashMap<Class, Method>();
    private static HashMap<Class, Field> classContextFieldCache = new HashMap<Class, Field>();
    private static HashSet<Method> onceMethodRecord = new HashSet<Method>();

    private final String TAG = "PermissionCheckAspectJ";

    @Pointcut("execution(@com.duoduo.annotationpermission.library.annotation.NeedPermission * *(..))")
    public void needPermission() {
    }

    @Around("needPermission()")
    public void checkPermission(final ProceedingJoinPoint joinPoint) throws Throwable {

        //尝试获取Context
        final Context context = tryToGetContext(joinPoint);

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
                handleDeniedPermission(joinPoint, requestCode, permissons);
            }
            return;
        }

        //调用工具方法进行权限检查
        PermissionUtils.checkAndRequestPermission(context, new ICheckAndRequestPermissionListener() {
            @Override
            public void onGrantedPermission(String... permissions) {
                //已授权
                if (!hasGrantedCallback || !handleGrantedPermission(joinPoint, requestCode, permissions)) {
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
                    handleDeniedPermission(joinPoint, requestCode, permissions);
                }
            }

            @Override
            public void onShowRationale(RequestExecutor executor, String... permissions) {
                if (ignoreShowRationale || !handleShowRationable(joinPoint, requestCode, executor, permissions)) {
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

    /**
     * 处理授权通过
     *
     * @param joinPoint
     * @param permissions
     */
    private boolean handleGrantedPermission(ProceedingJoinPoint joinPoint, int requestCode
            , String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            Method method = getClassHandleGrantedPermissionMethod(targetObjectClass);
            if (method != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length != 1 ||
                        !parameterTypes[0].equals(GrantedPermissionEntity.class)) {
                    return result;
                }
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

    /**
     * 获取类处理授权通过的方法
     *
     * @param targetClass
     * @return
     */
    private Method getClassHandleGrantedPermissionMethod(Class targetClass) {
        Method method = null;
        synchronized (classGrantedPermissionMethodCache) {
            method = classGrantedPermissionMethodCache.get(targetClass);
            if (method == null) {
                method = ReflectUtils.getMethodByAnnotation(targetClass, OnGrantedPermission.class);
                if (method != null) {
                    classGrantedPermissionMethodCache.put(targetClass, method);
                }
            }
        }
        return method;
    }

    /**
     * 处理授权拒绝
     *
     * @param joinPoint
     * @param permissions
     */
    private boolean handleDeniedPermission(ProceedingJoinPoint joinPoint, int requestCode
            , String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            Method method = getClassHandleDeniedPermissionMethod(targetObjectClass);
            if (method != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length != 1 ||
                        !parameterTypes[0].equals(DeniedPermissionEntity.class)) {
                    return result;
                }
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

    /**
     * 获取类处理授权拒绝的方法
     *
     * @param targetClass
     * @return
     */
    private Method getClassHandleDeniedPermissionMethod(Class targetClass) {
        Method method = null;
        synchronized (classDeniedPermissionMethodCache) {
            method = classDeniedPermissionMethodCache.get(targetClass);
            if (method == null) {
                method = ReflectUtils.getMethodByAnnotation(targetClass, OnDeniedPermission.class);
                if (method != null) {
                    classDeniedPermissionMethodCache.put(targetClass, method);
                }
            }
        }
        return method;
    }

    /**
     * 处理展示权限说明对话框
     *
     * @param joinPoint
     * @param executor
     * @param permissions
     */
    private boolean handleShowRationable(ProceedingJoinPoint joinPoint, int requestCode, RequestExecutor executor, String... permissions) {
        boolean result = false;
        try {
            Object targetObject = joinPoint.getTarget();
            Class targetObjectClass = targetObject.getClass();
            Method method = getClassHandleShowRationableMethod(targetObjectClass);
            if (method != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length != 1 ||
                        !parameterTypes[0].equals(ShowRationaleEntity.class)) {
                    return result;
                }
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

    /**
     * 获取类处理显示权限说明对话框的方法
     *
     * @param targetClass
     * @return
     */
    private Method getClassHandleShowRationableMethod(Class targetClass) {
        Method method = null;
        synchronized (classShowRationableMethodCache) {
            method = classShowRationableMethodCache.get(targetClass);
            if (method == null) {
                method = ReflectUtils.getMethodByAnnotation(targetClass, OnShowRationable.class);
                if (method != null) {
                    classShowRationableMethodCache.put(targetClass, method);
                }
            }
        }
        return method;
    }

    /**
     * 尝试获取Context的方法
     *
     * @param joinPoint
     * @return
     */
    private Context tryToGetContext(ProceedingJoinPoint joinPoint) {
        Context context = null;
        try {
            Object targetObject = joinPoint.getTarget();
            if (targetObject instanceof Context) {
                context = (Context) targetObject;
            } else if (targetObject instanceof Fragment) {
                context = ((Fragment) targetObject).getActivity();
            } else if (targetObject instanceof android.support.v4.app.Fragment) {
                context = ((android.support.v4.app.Fragment) targetObject).getActivity();
            } else if (targetObject instanceof View) {
                context = ((View) targetObject).getContext();
            } else if (targetObject instanceof IContextHolder) {
                context = ((IContextHolder) targetObject).getContext();
            } else {
                //如果上面的类型都不是
                //尝试通过反射，获取targetObject的Context字段
                //如果没有Context字段，则获取失败
                Class targetObjectClass = targetObject.getClass();
                Field field = getClassContextField(targetObjectClass);
                if (field != null) {
                    field.setAccessible(true);
                    context = (Context) field.get(targetObject);
                }

            }
            if (context != null) {
                context = context.getApplicationContext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }

    /**
     * 获取类Context字段
     *
     * @param targetClass
     */
    private Field getClassContextField(Class targetClass) {
        Field field = null;
        synchronized (classContextFieldCache) {
            field = classContextFieldCache.get(targetClass);
            if (field == null) {
                for (Class forclass = targetClass; !forclass.equals(Object.class) && field == null; forclass = forclass.getSuperclass()) {
                    Field[] fields = forclass.getDeclaredFields();
                    if (fields != null) {
                        int size = fields.length;
                        for (int i = 0; i < size; i++) {
                            Field fieldItem = fields[i];
                            Class fieldType = fieldItem.getType();
                            if (fieldType.equals(Context.class)) {
                                field = fieldItem;
                                break;
                            }
                        }
                    }
                }
                if (field != null) {
                    classContextFieldCache.put(targetClass, field);
                }
            }
        }
        return field;
    }
}
