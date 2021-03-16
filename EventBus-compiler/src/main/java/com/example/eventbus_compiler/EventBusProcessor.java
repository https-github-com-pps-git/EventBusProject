package com.example.eventbus_compiler;

import com.example.eventbus_annotations.EventBus;
import com.example.eventbus_annotations.EventBusBean;
import com.example.eventbus_compiler.util.Consts;
import com.example.eventbus_compiler.util.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.lang.reflect.Type;
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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

//开启服务
@AutoService(Processor.class)

// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Consts.EVENTBUS_PAGEAGE})

// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)

public class EventBusProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;
    private Elements mElements;
    private Types mTypes;
    private Map<TypeElement, List<Element>> tempEventBusMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //生成文件的
        mFiler = processingEnvironment.getFiler();
        //打印日志的
        mMessager = processingEnvironment.getMessager();

        mElements = processingEnvironment.getElementUtils();

        //获取类型
        mTypes = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        /**
         * 获取被EventBus 注解修饰的所有类
         */
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(EventBus.class);
        for (Element element : elementsAnnotatedWith) {
            //获取当前这个字段 是属于哪个类的
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            if (tempEventBusMap.containsKey(enclosingElement)) {
                tempEventBusMap.get(enclosingElement).add(element);
            } else {
                List<Element> list = new ArrayList<>();
                list.add(element);
                tempEventBusMap.put(enclosingElement, list);
            }

        }

        // TODO 生成类文件
        // 判断是否有需要生成的类文件
        if (ProcessorUtils.isEmpty(tempEventBusMap)) return true;

        TypeElement mEventBusMapElement = mElements.getTypeElement(Consts.EVENTBUSMAP_NAME);

        /**
         * 遍历这个集合 然后生成文件
         *
         * 生成文件的信息
         *
         *   public class EventBus$$APT implements EventBusMap{
         *   @Override
         *   public Map<Class,List<EventBusBean>> getMethodMap(){
         *     Map<Class,List<EventBusBean>> mMap = new HashMap<>();
         *     List<EventBusBean> list = new ArrayList<>();
         *     list.add(new EventBusBean("setAge",Integer.class));
         *     list.add(new EventBusBean("fun1",String.class));
         *     list.add(new EventBusBean("setUser",User.class));
         *     mMap.put(MainActivity.class,list);
         *     ......
         *     return mMap;
         *   }
         * }
         *
         */

        //返回值是 Map<Class,List<EventBusBean>>
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class),//Map<
                ClassName.get(Class.class),//Map<Class,
                ParameterizedTypeName.get(ClassName.get(List.class), // Map<Class,List<
                        ClassName.get(EventBusBean.class))); //Map<Class,List<EventBusBean>>

        //开始生成方法
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Consts.EVENTBUSMAP_METHOD_NAME)
                .addAnnotation(ClassName.get(Override.class))
                .addModifiers(Modifier.PUBLIC)
                .returns(typeName);

        methodBuilder.addCode("//创建一个map集合用来存放每个class对象 对应当前类中的所有方法\n");
        //生成第一行代码    Map<Class,List<EventBusBean>> mMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T<$T>> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(Class.class),
                ClassName.get(List.class),
                ClassName.get(EventBusBean.class),
                Consts.EVENTBUSMAP_METHOD_MAP,
                ClassName.get(HashMap.class));

        methodBuilder.addCode("\n");

        for (Map.Entry<TypeElement, List<Element>> entry : tempEventBusMap.entrySet()) {
            //循环遍历集合
            TypeElement typeElement = entry.getKey();
            //获取这个字段的类名
            String className = typeElement.getSimpleName().toString();
            List<Element> elements = entry.getValue();
            //获取这个字段的名称
            TypeMirror objectTypeMirror = typeElement.asType();

            methodBuilder.addCode("//创建list集合用来装载"+className+"类中的所有带有@EventBus的方法\n");
            //这里生成代码 List<EventBusBean> list = new ArrayList<>();
            String mListName = Consts.EVENTBUSMAP_METHOD_LIST + className;
            methodBuilder.addStatement("$T<$T> $N = new $T<>()",
                    ClassName.get(List.class),
                    ClassName.get(EventBusBean.class),
                    mListName,
                    ClassName.get(ArrayList.class));

            for (Element element : elements) {
                //把它转化为 ExecutableElement  就能得到这个注解修饰的方法的参数了
                ExecutableElement executableElement = (ExecutableElement) element;
                String methodName = element.getSimpleName().toString();
                if (executableElement.getParameters().size() == 0 || executableElement.getParameters().size() > 1) {
                    mMessager.printMessage(Diagnostic.Kind.NOTE, " @EventBus注解 修饰的方法只能有一个参数  error " + className + " && " + methodName);
                    return false;
                }
                VariableElement variableElement = executableElement.getParameters().get(0);
                EventBus eventBus = element.getAnnotation(EventBus.class);
                TypeMirror typeMirror = executableElement.getParameters().get(0).asType();

                TypeName parameterName = ClassName.get(typeMirror);

                //生成代码 list.add(new EventBusBean("xxx",a.class))
                methodBuilder.addStatement("$N.add(new $T($S,$T.class,"+eventBus.isMainThread()+"))",
                        mListName,
                        ClassName.get(EventBusBean.class),
                        methodName,
                        parameterName);
            }

            //把list 添加到map中
            methodBuilder.addStatement("$N.put($T.class,$N)",
                    Consts.EVENTBUSMAP_METHOD_MAP,
                    ClassName.get(objectTypeMirror),
                    mListName);

            methodBuilder.addCode("\n");
        }
        //生成代码 return mMap;
        methodBuilder.addStatement("return $N", Consts.EVENTBUSMAP_METHOD_MAP);
        //生成类文件
        //最终生成文件
        // 最终生成的类文件名（类名$$Parameter） 例如：Personal_MainActivity$$Parameter
        String finalClassName = Consts.APT_START_NAME;
        try {
            //生成类文件
            JavaFile.builder(Consts.APT_PACKAGE_NAME,
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(mEventBusMapElement))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build()).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
