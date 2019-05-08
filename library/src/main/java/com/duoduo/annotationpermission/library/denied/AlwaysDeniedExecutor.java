package com.duoduo.annotationpermission.library.denied;

/**
 * 拒绝授权并不再提示的处理器接口
 */
public interface AlwaysDeniedExecutor {

    public void execute();

    public void cancel();
}
