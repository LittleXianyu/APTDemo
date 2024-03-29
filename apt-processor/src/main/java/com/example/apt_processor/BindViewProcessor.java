package com.example.apt_processor;

import com.example.apt_annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @AutoService 是 Google 发布的一个注解处理器框架 AutoService 中提供的注解之一。
 * 它的作用是自动生成 META-INF/services 目录下的配置文件，用于注册实现了某个接口或抽象类的类。
 * BindViewProcessor是实现了Processor接口
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        mMessager.printMessage(Diagnostic.Kind.NOTE, ">> xianyu init...");
        System.out.println(">> xianyu>> init");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        mProxyMap.clear();
        ////获取BindView注解的所有元素
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "element: "+element.getSimpleName());
            VariableElement variableElement = (VariableElement) element;//因为注解的作用域是成员变量，所以这里可以直接强转成 VariableElement

            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();//获得外部元素对象，也就是成员变量所在类
            String fullClassName = classElement.getQualifiedName().toString();//用于获取当前类或接口的全限定名
            mMessager.printMessage(Diagnostic.Kind.NOTE, "classElement: "+fullClassName);

            //elements的信息保存到mProxyMap中
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElementUtils, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            // 每个@BindView注解的成员变量都会添加到处理类ClassCreatorProxy的map中，key是id，参数是被注解变量的Element元素
            proxy.putElement(id, variableElement);
        }
        //通过遍历mProxyMap，创建java文件
        //通过javapoet生成
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);//fullClassName: 例如JavaActivity_ViewBinding

            // 这里的key是注解成员变量的所在类，比如某个Activity，对应的生成这个类的专属的Activity_ViewBinding类文件，类方法bind()
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCode2()).build();
            try {
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process finish ...");
        return true;
    }

}
