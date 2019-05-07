package com.duoduo.annotationpermission.library;

import android.content.Context;

import com.duoduo.annotationpermission.library.utils.ListUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.ArrayList;
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
                    boolean hasAlwaysDeniedPermission = AndPermission.hasAlwaysDeniedPermission(context, ListUtils.stringListToArray(data));
                    if (hasAlwaysDeniedPermission) {
                        //如果是勾选了"不再提示"，就跳设置界面
                        AndPermission.with(context)
                                .runtime()
                                .setting()
                                .onComeback(new Setting.Action() {
                                    @Override
                                    public void onAction() {
                                        //设置界面返回
                                        //再检查一次是否有权限
                                        boolean hasPermissions = AndPermission.hasPermissions(context, permissions);
                                        if (hasPermissions) {
                                            //如果有权限
                                            if (listener != null) {
                                                listener.onGrantedPermission(permissions);
                                            }
                                        } else {
                                            //如果没有权限，进行拒绝处理
                                            if (listener != null) {
                                                String[] deniedPermissions = checkDeniedPermissionOneByOne(context, permissions);
                                                listener.onDeniedPermission(deniedPermissions);
                                            }
                                        }
                                    }
                                })
                                .start();
                    } else {
                        //如果没有勾选"不再提示"，进行拒绝处理
                        if (listener != null) {
                            listener.onDeniedPermission(ListUtils.stringListToArray(data));
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

    /**
     * 逐一检查权限是否授权的方法
     * @param context
     * @param permissions
     * @return
     */
    private static String[] checkDeniedPermissionOneByOne(final Context context, final String... permissions) {
        if (context == null || permissions == null || permissions.length <= 0) {
            return null;
        }
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean hasPermission = AndPermission.hasPermissions(context, permission);
            if (!hasPermission) {
                deniedPermissions.add(permission);
            }
        }
        return ListUtils.stringListToArray(deniedPermissions);
    }
}
