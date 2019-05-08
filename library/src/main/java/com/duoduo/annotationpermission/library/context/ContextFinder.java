package com.duoduo.annotationpermission.library.context;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Context获取类
 */
public class ContextFinder {

    private static HashMap<Class, Field> classContextFieldCache = new HashMap<Class, Field>();

    private static Context sContext;

    public static Context findContext(ProceedingJoinPoint joinPoint) {
        if (sContext != null) {
            return sContext;
        }
        synchronized (ContextFinder.class) {
            if (sContext != null) {
                return sContext;
            }
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
                    sContext = context.getApplicationContext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sContext;
    }

    public static void setContext(Context context) {
        synchronized (ContextFinder.class) {
            sContext = context;
        }
    }

    /**
     * 获取类Context字段
     *
     * @param targetClass
     */
    private static Field getClassContextField(Class targetClass) {
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
