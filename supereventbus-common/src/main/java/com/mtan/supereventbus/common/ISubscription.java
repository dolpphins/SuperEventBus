package com.mtan.supereventbus.common;

import java.util.List;
import java.util.Map;

public interface ISubscription {

    Map<Class<?>, List<MethodInfo>> getMethodInfoMap();

}
