package com.mtan.supereventbus.compiler;

import com.google.auto.service.AutoService;
import com.mtan.supereventbus.common.ISubscription;
import com.mtan.supereventbus.common.MethodInfo;
import com.mtan.supereventbus.common.Subscribe;
import com.mtan.supereventbus.common.ThreadMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes(value = {"com.mtan.supereventbus.common.Subscribe"})
public class SuperEventBusProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // TODO

        return false;

//        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(
//                com.mtan.supereventbus.common.Subscribe.class);
//        if (elements == null || elements.size() <= 0) {
//            return false;
//        }
//
//        Map<Class<?>, List<MethodInfo>> map = new HashMap<>();
//
//        for (Element e : elements) {
//            if (!(e instanceof ExecutableElement)) {
//                continue;
//            }
//            String methodName = e.getSimpleName().toString();
//            List<? extends TypeParameterElement> paramsTypes = ((ExecutableElement) e).getTypeParameters();
//            if (methodName == null || paramsTypes == null || paramsTypes.size() != 1) {
//                continue;
//            }
//
//            TypeElement classElement = (TypeElement) e.getEnclosingElement();
//            ClassName className = ClassName.get(classElement);
//
//            messager.printMessage(Diagnostic.Kind.NOTE, className.packageName());
//            messager.printMessage(Diagnostic.Kind.NOTE, className.simpleName());
//
//            Method[] methods = Class.forName(className.packageName() + "." + className.simpleName()).getDeclaredMethods();
//            for (Method method : methods) {
//                if (method.getName().equals(methodName)) {
//                    // 先不管参数了，方法名一样就行
//                    Subscribe annotation = e.getAnnotation(com.mtan.supereventbus.common.Subscribe.class);
//                    ThreadMode threadMode = annotation.threadMode();
//                    MethodInfo methodInfo = new MethodInfo(method, threadMode);
//
//                    Class<?> clazz = method.getParameters()[0].getClass();
//                    List<MethodInfo> list = map.get(clazz);
//                    if (list == null) {
//                        list = new ArrayList<>();
//                        map.put(clazz, list);
//                    }
//                    list.add(methodInfo);
//                    break;
//                }
//            }
//        }
//
//        // 写入
//        try {
//
//            MethodSpec.Builder getMethodInfoMap = MethodSpec.methodBuilder("getMethodInfoMap")
//                    .addAnnotation(Override.class)
//                    .addModifiers(Modifier.PUBLIC);
//
//            // Map<Class<?>, List<MethodInfo>> indexs
//            Set<Class<?>> keys = map.keySet();
//            int i = 0;
//            for (Class<?> clazz : keys) {
//                List<MethodInfo> list = map.get(clazz);
//                getMethodInfoMap.addStatement("$T<$T> list" + i + " = new $T<>()", List.class, MethodInfo.class, ArrayList.class);
//                for (MethodInfo methodInfo : list) {
//                    getMethodInfoMap.addStatement("list" + i + ".add(new $T())", MethodInfo.class);
//                }
//                getMethodInfoMap.addStatement("indexs.put($T, $")
//            }
//
//            String rootFileName = "SubscribeIndex";
//            JavaFile.builder("com.mtan.supereventbus",
//                    TypeSpec.classBuilder(rootFileName)
//                            .addSuperinterface(ISubscription.class)
//                            .addJavadoc("NOT MODIFY!!!")
//                            .addModifiers(Modifier.PUBLIC)
//                            .addMethod(getMethodInfoMap.build())
//                            .build()
//            ).build().writeTo(mFiler);
//        } catch (Throwable t) {
//            messager.printMessage(Diagnostic.Kind.ERROR, t.getMessage());
//        }
//
//
//        return true;
    }
}
