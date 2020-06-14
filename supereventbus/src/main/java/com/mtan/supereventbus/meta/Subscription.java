package com.mtan.supereventbus.meta;

import com.mtan.supereventbus.common.MethodInfo;

public class Subscription {

    private Object mSubscriber;
    private MethodInfo mMethodInfo;

    public Subscription(Object subscriber, MethodInfo methodInfo) {
        mSubscriber = subscriber;
        mMethodInfo = methodInfo;
    }

    public Object getSubscriber() {
        return mSubscriber;
    }

    public MethodInfo getMethodInfo() {
        return mMethodInfo;
    }
}
