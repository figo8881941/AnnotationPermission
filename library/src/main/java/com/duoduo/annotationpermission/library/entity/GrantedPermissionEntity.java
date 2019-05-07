package com.duoduo.annotationpermission.library.entity;

/**
 * 权限授予Entity
 */
public class GrantedPermissionEntity {

    private String[] permissions;
    private int requestCode;

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
}
