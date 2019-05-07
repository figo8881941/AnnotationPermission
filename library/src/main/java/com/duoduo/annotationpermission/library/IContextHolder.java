package com.duoduo.annotationpermission.library;

import android.content.Context;

/**
 * Context持有者接口
 */
public interface IContextHolder {

    /**
     * 获取Context接口
     * @return
     */
    public Context getContext();
}