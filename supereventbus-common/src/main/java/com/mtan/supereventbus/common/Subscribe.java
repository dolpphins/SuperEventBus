package com.mtan.supereventbus.common;

import com.mtan.supereventbus.common.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Subscribe {

    ThreadMode threadMode() default ThreadMode.POSTING;
}
