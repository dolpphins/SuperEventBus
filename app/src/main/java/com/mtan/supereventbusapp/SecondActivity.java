package com.mtan.supereventbusapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mtan.supereventbus.SuperEventBus;
import com.mtan.supereventbus.common.Subscribe;
import com.mtan.supereventbus.common.ThreadMode;

public class SecondActivity extends Activity {

    private static final String TAG = "SecondActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long time = System.currentTimeMillis();
        SuperEventBus.getDefault().register(this);
        Log.i(TAG, "register consume time:" + (System.currentTimeMillis() - time) + "ms");
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(Object event) {
        Log.i(TAG, "onEvent, thread:" + Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SuperEventBus.getDefault().unregister(this);
    }
}
