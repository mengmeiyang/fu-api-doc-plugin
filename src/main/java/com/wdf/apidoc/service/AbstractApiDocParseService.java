package com.wdf.apidoc.service;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.wdf.apidoc.helper.AnnotationParseHelper;
import com.wdf.apidoc.helper.DocCommentParseHelper;
import com.wdf.apidoc.pojo.context.ApiDocContext;
import com.wdf.apidoc.pojo.data.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wangdingfu
 * @Descption API接口参数解析器抽象类
 * @Date 2022-04-21 20:52:09
 */
public abstract class AbstractApiDocParseService implements ApiDocParseService {

    /**
     * 解析指定方法的请求参数
     *
     * @param apiDocContext     全局上下文
     * @param psiMethod         指定的方法
     * @param apiDocCommentData 方法的注释内容
     * @return 参数解析后的数据(参数的属性)对象
     */
    protected abstract List<ApiDocObjectData> requestParse(ApiDocContext apiDocContext, PsiMethod psiMethod, ApiDocCommentData apiDocCommentData);

    /**
     * 解析指定方法的返回参数
     *
     * @param apiDocContext     全局上下文
     * @param psiMethod         指定的方法
     * @param apiDocCommentData 方法的注释内容
     * @return 参数解析后的数据(参数的属性)对象
     */
    protected abstract ApiDocObjectData responseParse(ApiDocContext apiDocContext, PsiMethod psiMethod, ApiDocCommentData apiDocCommentData);


    /**
     * 解析java类(controller/dubbo接口/feign接口)
     *
     * @param apiDocContext 全局上下文
     * @param psiClass      指定类的PsiClass
     * @param methodList    指定方法集合(为空则当前类所有方法都解析)
     * @return 解析指定java类后的属性数据对象
     */
    @Override
    public ApiDocData parse(ApiDocContext apiDocContext, PsiClass psiClass, List<String> methodList) {
        ApiDocData apiDocData = new ApiDocData();
        if (Objects.nonNull(apiDocContext) && Objects.nonNull(psiClass)) {
            List<ApiDocMethodData> apiDocMethodDataList = Lists.newArrayList();
            //设置类上的注解
            apiDocData.setAnnotationDataMap(annotationParse(apiDocContext, psiClass));
            apiDocData.setApiDocMethodDataList(apiDocMethodDataList);
            for (PsiMethod method : psiClass.getMethods()) {
                if (CollectionUtils.isNotEmpty(methodList) && !methodList.contains(method.getName())) {
                    //过滤没有指定的方法
                    continue;
                }
                ApiDocCommentData apiDocCommentData = DocCommentParseHelper.parseComment(method.getDocComment());
                ApiDocMethodData apiDocMethodData = new ApiDocMethodData();
                //设置方法上注解
                apiDocMethodData.setAnnotationDataMap(annotationParse(apiDocContext, method));
                //设置请求参数
                apiDocMethodData.setRequestList(requestParse(apiDocContext, method, apiDocCommentData));
                //设置响应参数
                apiDocMethodData.setResponse(responseParse(apiDocContext, method, apiDocCommentData));
                apiDocMethodDataList.add(apiDocMethodData);
            }
        }
        return apiDocData;
    }


    /**
     * 注解解析
     *
     * @param apiDocContext        全局上下文
     * @param psiModifierListOwner psi
     * @return key: 注解名  value:解析后的注解对象
     */
    private Map<String, AnnotationData> annotationParse(ApiDocContext apiDocContext, PsiModifierListOwner psiModifierListOwner) {
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (Objects.nonNull(modifierList)) {
            //解析注解
            return AnnotationParseHelper.parse(apiDocContext, modifierList.getAnnotations());
        }
        return new HashMap<>();
    }
}
