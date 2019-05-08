package com.duoduo.annotationpermission.library.denied;

import android.content.Context;

import com.duoduo.annotationpermission.library.ICheckAndRequestPermissionListener;
import com.duoduo.annotationpermission.library.utils.ListUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Setting;

import java.util.ArrayList;

/**
 * 拒绝授权并不再提示的处理器实现
 */
public class AlwaysDeniedExecutorImpl implements AlwaysDeniedExecutor {

    private Context context;
    private ICheckAndRequestPermissionListener listener;
    private String[] permissions;

    public AlwaysDeniedExecutorImpl(Context context,
                                    ICheckAndRequestPermissionListener listener,
                                    String[] permissions) {
        this.context = context;
        this.listener = listener;
        this.permissions = permissions;
    }

    @Override
    public void execute() {
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
    }

    @Override
    public void cancel() {

    }

    /**
     * 逐一检查权限是否授权的方法
     * @param context
     * @param permissions
     * @return
     */
    private String[] checkDeniedPermissionOneByOne(final Context context, final String... permissions) {
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
