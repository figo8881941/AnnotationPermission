package com.duoduo.annotationpermission.library.entity;

import com.duoduo.annotationpermission.library.denied.AlwaysDeniedExecutor;

/**
 * 权限一直拒绝不再提示Entity
 */
public class AlwaysDeniedPermissionEntity {

    private String[] permissions;
    private int requestCode;
    private AlwaysDeniedExecutor executor;

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public AlwaysDeniedExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(AlwaysDeniedExecutor executor) {
        this.executor = executor;
    }
}
