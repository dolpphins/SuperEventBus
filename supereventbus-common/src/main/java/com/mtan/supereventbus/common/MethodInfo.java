package com.mtan.supereventbus.common;

import java.lang.reflect.Method;

public class MethodInfo {

    private Class<?> mSubscriberClass;
    private String mMethodName;
    private Method mMethod;
    private ThreadMode mThreadMode;

    public MethodInfo(Class<?> subscriberClass,
                      String methodName,
                      Method method,
                      ThreadMode threadMode) {
        mSubscriberClass = subscriberClass;
        mMethodName = methodName;
        mMethod = method;
        mThreadMode = threadMode;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public void setMethod(Method method) {
        mMethod = method;
    }

    public Method getMethod() {
        return mMethod;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }

    public Class<?> getSubscriberClass() {
        return mSubscriberClass;
    }

    private Object mTag;

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }
}
