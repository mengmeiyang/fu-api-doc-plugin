package com.wdf.fudoc.util;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.wdf.fudoc.apidoc.constant.AnnotationConstants;
import com.wdf.fudoc.apidoc.constant.enumtype.JavaClassType;

import java.util.List;
import java.util.Objects;

/**
 * @author wangdingfu
 * @Descption Fu Doc工具类
 * @date 2022-06-08 20:16:46
 */
public class FuDocUtils {

    /**
     * 校验一个方法是否需要解析
     *
     * @param psiClass  类对象
     * @param psiMethod 方法对象
     * @return true 需要解析
     */
    public static boolean isNeedParse(PsiClass psiClass, PsiMethod psiMethod) {
        JavaClassType javaClassType = JavaClassType.get(psiClass);
        if (JavaClassType.isNone(javaClassType)) {
            return false;
        }
        return isValidMethod(javaClassType, psiMethod);
    }


    /**
     * 校验java类是否为Controller
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isController(PsiClass psiClass) {
        return existsAnnotation(psiClass, AnnotationConstants.CONTROLLER, AnnotationConstants.REST_CONTROLLER);
    }

    /**
     * 校验java类是否为Dubbo接口
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isDubbo(PsiClass psiClass) {
        return psiClass.isInterface();
    }

    /**
     * 校验java类是否为Feign接口
     *
     * @param psiClass java类
     * @return true 是一个Controller
     */
    public static boolean isFeign(PsiClass psiClass) {
        return existsAnnotation(psiClass, AnnotationConstants.FEIGN_CLIENT);
    }


    private static boolean existsAnnotation(PsiClass psiClass, String... annotationNames) {
        if (Objects.isNull(psiClass) || Objects.isNull(annotationNames) || annotationNames.length <= 0) {
            return false;
        }
        return ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () ->
                AnnotationUtil.isAnnotated(psiClass, Sets.newHashSet(annotationNames), 0));
    }


    /**
     * 是否为有效的方法(即需要被解析的方法)
     *
     * @param javaClassType 该方法所处的java类 类型
     * @param psiMethod     方法对象
     * @return true 是有效的方法 需要被解析
     */
    public static boolean isValidMethod(JavaClassType javaClassType, PsiMethod psiMethod) {
        switch (javaClassType) {
            case CONTROLLER:
                return isControllerMethod(psiMethod);
            case INTERFACE:
            case FEIGN:
                //TODO 过滤default方法
                return true;
            case NONE:
            default:
                return false;
        }
    }


    /**
     * 是否为Controller有效的请求方法
     *
     * @param psiMethod 方法对象
     * @return true 是
     */
    public static boolean isControllerMethod(PsiMethod psiMethod) {
        if (Objects.nonNull(psiMethod)) {
            PsiAnnotation[] annotations = psiMethod.getAnnotations();
            if (annotations.length > 0) {
                for (String mapping : AnnotationConstants.MAPPING) {
                    for (PsiAnnotation annotation : annotations) {
                        if (mapping.equals(annotation.getQualifiedName())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取项目中module的唯一标识
     *
     * @param module 项目中的module
     * @return module的绝对路径
     */
    public static String getModuleId(Module module) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if (contentRoots.length > 0) {
            return contentRoots[0].getPath();
        }
        return module.getName();
    }


}
