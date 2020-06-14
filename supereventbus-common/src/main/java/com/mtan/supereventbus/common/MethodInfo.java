package com.mtan.supereventbus.common;

import java.lang.reflect.Method;

public class MethodInfo {

    private Method mMethod;
    private ThreadMode mThreadMode;

    public MethodInfo(Method method, ThreadMode threadMode) {
        mMethod = method;
        mThreadMode = threadMode;
    }

    public Method getMethod() {
        return mMethod;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }
}
