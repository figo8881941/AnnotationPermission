package com.duoduo.annotationpermission.library.listener;

import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutor;
import com.yanzhenjie.permission.RequestExecutor;

/**
 * 检查和申请权限的监听器接口
 */
public interface ICheckAndRequestPermissionListener {

    /**
     * 获取到授权的回调
     * @param permissions
     */
    public void onGrantedPermission(final String... permissions);

    /**
     * 拒绝授权的回调
     * @param permissions
     */
    public void onDeniedPermission(final String... permissions);

    /**
     * 拒绝授权并不再提示的回调
     * @param permissions
     */
    public void onAlwaysDeniedPermission(final AlwaysDeniedExecutor executor, final String... permissions);

    /**
     * 跟用户解释提示的回调
     * @param permissions
     */
    public void onShowRationale(final RequestExecutor executor, final String... permissions);
}
