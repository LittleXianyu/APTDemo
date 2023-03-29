package com.example.apt_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by gavin
 * date 2018/4/22
 * 创建Java文件代理类
 */

/** 目标生成JavaActivity_ViewBinding.java
 * package com.example.roomviewmodel;
 *
 * public class JavaActivity_ViewBinding {
 *   public void bind(JavaActivity host) {
 *     host.button = (android.widget.Button)(((android.app.Activity)host).findViewById( 2131230818));
 *   }
 * }
 */
public class ClassCreatorProxy {
    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;// 注解变量所在的类：JavaActivity
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        System.out.println("packageName: "+packageName+ " className: "+className);
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding"; //最后生成的类名
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    /**
     * 创建Java代码
     * javapoet
     * 合成类
     * @return
     */
    public TypeSpec generateJavaCode2() {
        System.out.println("xianyu >> mBindingClassName>> "+mBindingClassName);
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods2())
                .build();
        return bindingClass;

    }

    /**
     * 生成方法
     * javapoet
     */
    private MethodSpec generateMethods2() {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        // 定义方法名，可见性，返回类型，参数类型和形参名
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");

        for (int id : mVariableElementMap.keySet()) { // 注解的参数值，也就是被注解的button，获取他的参数名字，参数类型
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));");
        }
        return methodBuilder.build();
    }


    public String getPackageName() {
        return mPackageName;
    }
}