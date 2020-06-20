package com.mtan.supereventbus.compiler;

import com.google.auto.service.AutoService;
import com.mtan.supereventbus.common.ISubscription;
import com.mtan.supereventbus.common.MethodInfo;
import com.mtan.supereventbus.common.Subscribe;
import com.mtan.supereventbus.common.ThreadMode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
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

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(
                com.mtan.supereventbus.common.Subscribe.class);
        if (elements == null || elements.size() <= 0) {
            return false;
        }

        // 写入
        try {

            FieldSpec indexFieldSpec = getIndexFieldSpec();
            MethodSpec constructorMethodSpec = getConstructorMethodSpec(elements);
            MethodSpec getMethodInfoMapMethodSpec = getGetMethodInfoMapMethodSpec();


            String rootFileName = "SubscribeIndex";
            JavaFile.builder("com.mtan.supereventbus",
                    TypeSpec.classBuilder(rootFileName)
                            .addSuperinterface(ISubscription.class)
                            .addJavadoc("NOT MODIFY!!!")
                            .addModifiers(Modifier.PUBLIC)
                            .addField(indexFieldSpec)
                            .addMethod(constructorMethodSpec)
                            .addMethod(getMethodInfoMapMethodSpec)
                            .build()
            ).build().writeTo(mFiler);
        } catch (Throwable t) {
            messager.printMessage(Diagnostic.Kind.ERROR, t.getLocalizedMessage());
            messager.printMessage(Diagnostic.Kind.ERROR, t.getLocalizedMessage());
            messager.printMessage(Diagnostic.Kind.ERROR, t.getMessage());
        }


        return true;
    }

    private FieldSpec getIndexFieldSpec() {
        TypeName indexType = getIndexTypeName();
        FieldSpec indexFieldSpec = FieldSpec.builder(indexType, "indexs")
                .addModifiers(Modifier.PRIVATE)
                .initializer("new $T<>()", ClassName.get(HashMap.class))
                .build();
        return indexFieldSpec;
    }

    private MethodSpec getConstructorMethodSpec(Set<? extends Element> elements) throws Exception {
        Map<Class<?>, List<MethodInfo>> map = new HashMap<>();
        for (Element e : elements) {
            if (!(e instanceof ExecutableElement)) {
                continue;
            }

            String methodName = e.getSimpleName().toString();
            List<? extends VariableElement> parameters = ((ExecutableElement) e).getParameters();
            if (methodName == null || parameters == null || parameters.size() != 1) {
                continue;
            }
            TypeElement classElement = (TypeElement) e.getEnclosingElement();
            ClassName className = ClassName.get(classElement);

            Subscribe annotation = e.getAnnotation(com.mtan.supereventbus.common.Subscribe.class);
            ThreadMode threadMode = annotation.threadMode();
            MethodInfo methodInfo = new MethodInfo(null, methodName, null, threadMode);
            methodInfo.setTag(className);

            TypeMirror methodParameterType = parameters.get(0).asType();
            if (methodParameterType instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) methodParameterType;
                methodParameterType = typeVariable.getUpperBound();

            }
            Class<?> paramsClazz = Class.forName(methodParameterType.toString());
            messager.printMessage(Diagnostic.Kind.NOTE, methodParameterType.toString());

            List<MethodInfo> list = map.get(paramsClazz);
            if (list == null) {
                list = new ArrayList<>();
                map.put(paramsClazz, list);
            }
            list.add(methodInfo);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "" + "事件类型数：" + map.size());

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        Set<Class<?>> keys = map.keySet();
        int i = 0;
        for (Class<?> clazz : keys) {
            List<MethodInfo> list = map.get(clazz);
            builder.addStatement("$T<$T> list" + i + " = new $T<>()",
                    List.class, MethodInfo.class, ArrayList.class);
            for (MethodInfo methodInfo : list) {
                // TODO
                // 生成获取到Method的代码
//                ClassName className = (ClassName) methodInfo.getTag();
//                className.getClass().getDeclaredMethod()
//                builder.addStatement("$T.class.getDeclaredMethod($S)");
                builder.addStatement("list" + i + ".add(new $T($T.class, $S, null, $T.$L))",
                        MethodInfo.class,
                        methodInfo.getTag(),
                        methodInfo.getMethodName(),
                        ThreadMode.class,
                        methodInfo.getThreadMode().name());
            }
            builder.addStatement("indexs.put($T.class, list" + i + ")", clazz);
        }
        return builder.build();
    }

    private MethodSpec getGetMethodInfoMapMethodSpec() {

        MethodSpec.Builder getMethodInfoMap = MethodSpec.methodBuilder("getMethodInfoMap")
                .addAnnotation(Override.class)
                .returns(getIndexTypeName())
                .addModifiers(Modifier.PUBLIC);

        getMethodInfoMap.addStatement("return indexs");

        return getMethodInfoMap.build();
    }

    private TypeName getIndexTypeName() {
        // Map<Class<?>, List<MethodInfo>> indexs
        // Class<?>
        TypeName wildcard = WildcardTypeName.subtypeOf(Object.class);
        TypeName className = ParameterizedTypeName.get(ClassName.get(Class.class), wildcard);
        // MethodInfo
        ClassName methodInfo = ClassName.get(MethodInfo.class);
        // List
        ClassName listClassName = ClassName.get(List.class);
        // List<MethodInfo>
        TypeName listMethodInfo = ParameterizedTypeName.get(listClassName, methodInfo);
        // Map
        ClassName mapClassName = ClassName.get(Map.class);
        // Map<Class<?>, List<MethodInfo>>
        TypeName indexType = ParameterizedTypeName.get(mapClassName, className, listMethodInfo);
        return indexType;
    }
}
