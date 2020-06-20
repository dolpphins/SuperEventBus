package com.mtan.supereventbus;

import android.os.Handler;
import android.os.Looper;

import com.mtan.supereventbus.common.ISubscription;
import com.mtan.supereventbus.common.Subscribe;
import com.mtan.supereventbus.common.MethodInfo;
import com.mtan.supereventbus.common.ThreadMode;
import com.mtan.supereventbus.meta.Subscription;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperEventBus {

    private static class Holder {
        private static final SuperEventBus sInstance = new SuperEventBus();
    }

    public static SuperEventBus getDefault() {
        return Holder.sInstance;
    }

    private SuperEventBus() {

    }

    private Map<Class<?>, List<Subscription>> mSubscribers = new HashMap<>();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService mAsyncExecutor = Executors.newCachedThreadPool();

    private ISubscription mSubscriptionIndex;

    public synchronized void register(Object subscriber) {
        Map<Class<?>, List<MethodInfo>> map = new HashMap<>();

        if (mSubscriptionIndex != null) {
            // index
            map = mSubscriptionIndex.getMethodInfoMap();
        } else {
            // 反射
            Class<?> clazz = subscriber.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation != null) {
                    Class<?> eventClazz = method.getParameterTypes()[0];
                    List<MethodInfo> list = map.get(eventClazz);
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(eventClazz, list);
                    }
                    list.add(new MethodInfo(clazz, method.getName(), method, annotation.threadMode()));
                }
            }
        }

        for (Class<?> eventClazz : map.keySet()) {
            List<Subscription> list = mSubscribers.get(eventClazz);
            if (list == null) {
                list = new ArrayList<>();
                mSubscribers.put(eventClazz, list);
            }
            List<MethodInfo> methodInfoList = map.get(eventClazz);
            for (MethodInfo methodInfo : methodInfoList) {
                Subscription subscription = new Subscription(subscriber, methodInfo);
                list.add(subscription);
            }
        }
    }

    public synchronized void unregister(Object subscriber) {
        Class<?> clazz = subscriber.getClass();
        Set<Class<?>> keys = mSubscribers.keySet();
        for (Class<?> eventClazz : keys) {
            List<Subscription> subscriptions = mSubscribers.get(eventClazz);
            if (subscriptions != null) {
                Iterator<Subscription> iterator = subscriptions.iterator();
                while (iterator.hasNext()) {
                    Subscription subscription = iterator.next();
                    if (subscription.getSubscriber().getClass() == clazz) {
                        iterator.remove();
                    }
                }
            }
            if (subscriptions.size() == 0) {
                mSubscribers.remove(eventClazz);
            }
        }
    }

    public synchronized void post(final Object event) {
        Class<?> eventClazz = event.getClass();
        List<Subscription> subscriptions = mSubscribers.get(eventClazz);
        if (subscriptions == null) {
            return;
        }
        for (final Subscription subscription : subscriptions) {
            MethodInfo methodInfo = subscription.getMethodInfo();
            ThreadMode threadMode = methodInfo.getThreadMode();
            if (threadMode == ThreadMode.POSTING) {
                invokeEvent(subscription, event);
            } else if (threadMode == ThreadMode.MAIN) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    invokeEvent(subscription, event);
                } else {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeEvent(subscription, event);
                        }
                    });
                }
            } else if (threadMode == ThreadMode.ASYNC) {
                mAsyncExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        invokeEvent(subscription, event);
                    }
                });
            }
        }
    }

    private void invokeEvent(Subscription subscription, Object event) {
        try {
            subscription.getMethodInfo().getMethod().invoke(subscription.getSubscriber(), event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
