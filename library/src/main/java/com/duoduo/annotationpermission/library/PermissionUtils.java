package com.duoduo.annotationpermission.library;

import android.content.Context;

import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutorImpl;
import com.duoduo.annotationpermission.library.utils.ListUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

/**
 * 权限工具类
 */
public class PermissionUtils {

    /**
     * 检查和申请权限的方法
     *
     * @param context
     * @param listener            监听器
     * @param permissions         请求权限
     */
    public static void checkAndRequestPermission(final Context context, final ICheckAndRequestPermissionListener listener, final String... permissions) {
        boolean hasPermissions = AndPermission.hasPermissions(context, permissions);
        if (hasPermissions) {
            //已经有权限
            if (listener != null) {
                listener.onGrantedPermission(permissions);
            }
        } else {
            //没有权限
            AndPermission.with(context).runtime().permission(permissions).onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    //授权成功
                    if (listener != null) {
                        listener.onGrantedPermission(permissions);
                    }
                }
            }).onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    //授权拒绝
                    //查看是否勾选了"不再提示"
                    String[] deniedPermissions = ListUtils.stringListToArray(data);
                    boolean hasAlwaysDeniedPermission = AndPermission.hasAlwaysDeniedPermission(context, deniedPermissions);
                    if (hasAlwaysDeniedPermission) {
                        //如果是勾选了"不再提示"
                        if (listener != null) {
                            AlwaysDeniedExecutorImpl executor = new AlwaysDeniedExecutorImpl(context, listener, permissions);
                            listener.onAlwaysDeniedPermission(executor, deniedPermissions);
                        }
                    } else {
                        //如果没有勾选"不再提示"，进行拒绝处理
                        if (listener != null) {
                            listener.onDeniedPermission(deniedPermissions);
                        }
                    }
                }
            }).rationale(new Rationale<List<String>>() {
                @Override
                public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                    if (listener != null) {
                        listener.onShowRationale(executor, ListUtils.stringListToArray(data));
                    }

                }
            }).start();
        }
    }
}
