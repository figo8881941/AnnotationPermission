package com.duoduo.annotationpermission.library.listener;

import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutor;
import com.yanzhenjie.permission.RequestExecutor;

/**
 * 检查和获取授权的监听默认实现
 */
public class DefaultCheckRequestListener implements ICheckAndRequestPermissionListener{
    @Override
    public void onGrantedPermission(String... permissions) {

    }

    @Override
    public void onDeniedPermission(String... permissions) {

    }

    @Override
    public void onAlwaysDeniedPermission(AlwaysDeniedExecutor executor, String... permissions) {

    }


    @Override
    public void onShowRationale(RequestExecutor executor, String... permissions) {

    }
}
