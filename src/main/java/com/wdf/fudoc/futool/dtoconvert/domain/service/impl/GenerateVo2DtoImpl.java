package com.wdf.fudoc.futool.dtoconvert.domain.service.impl;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.wdf.fudoc.common.exception.FuDocException;
import com.wdf.fudoc.futool.dtoconvert.domain.model.GenerateContext;
import com.wdf.fudoc.futool.dtoconvert.domain.model.GetObjConfigDO;
import com.wdf.fudoc.futool.dtoconvert.domain.model.SetObjConfigDO;
import com.wdf.fudoc.futool.dtoconvert.domain.service.AbstractGenerateVo2Dto;
import com.wdf.fudoc.futool.dtoconvert.infrastructure.Utils;

import java.util.*;
import java.util.regex.Pattern;

public class GenerateVo2DtoImpl extends AbstractGenerateVo2Dto {

    private int repair = 0;

    @Override
    protected GenerateContext getGenerateContext(Project project, DataContext dataContext, PsiFile psiFile) {

        // 基础信息
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        assert editor != null;
        Document document = editor.getDocument();
        List<String> importList = Lists.newArrayList();
        if (Objects.nonNull(psiElement)) {
            PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiElement.getContainingFile();
            if (Objects.nonNull(psiJavaFile.getImportList())) {
                PsiImportStatementBase[] allImportStatements = psiJavaFile.getImportList().getAllImportStatements();
                for (PsiImportStatementBase allImportStatement : allImportStatements) {
                    importList.add(allImportStatement.getText());
                }
            }
        }

        // 封装生成对象上下文
        GenerateContext generateContext = new GenerateContext();
        generateContext.setImportList(importList);
        generateContext.setProject(project);
        generateContext.setPsiFile(psiFile);
        generateContext.setDataContext(dataContext);
        generateContext.setEditor(editor);
        generateContext.setPsiElement(psiElement);
        generateContext.setOffset(editor.getCaretModel().getOffset());
        generateContext.setDocument(document);
        generateContext.setLineNumber(document.getLineNumber(generateContext.getOffset()));
        generateContext.setStartOffset(document.getLineStartOffset(generateContext.getLineNumber()));
        generateContext.setEditorText(document.getCharsSequence());

        return generateContext;
    }

    @Override
    protected SetObjConfigDO getSetObjConfigDO(GenerateContext generateContext) {
        repair = 0;
        PsiClass psiClass = null;
        String clazzParamName = null;

        PsiElement psiElement = generateContext.getPsiElement();

        // 鼠标定位到类
        if (psiElement instanceof PsiClass) {
            psiClass = (PsiClass) generateContext.getPsiElement();

            // 通过光标步长递进找到属性名称
            PsiFile psiFile = generateContext.getPsiFile();
            Editor editor = generateContext.getEditor();
            int offsetStep = generateContext.getOffset() + 1;

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());

            while (null == elementAt || elementAt.getText().equals(psiClass.getName()) || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(++offsetStep);
            }

            // 最终拿到属性名称
            clazzParamName = elementAt.getText();
        }

        // 鼠标定位到属性
        if (psiElement instanceof PsiLocalVariable) {
            PsiLocalVariable psiLocalVariable = (PsiLocalVariable) psiElement;

            clazzParamName = psiLocalVariable.getName();

            // 通过光标步长递进找到属性名称
            PsiFile psiFile = generateContext.getPsiFile();
            Editor editor = generateContext.getEditor();
            int offsetStep = generateContext.getOffset() - 1;

            PsiElement elementAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
            while (null == elementAt || elementAt.getText().equals(clazzParamName) || elementAt instanceof PsiWhiteSpace) {
                elementAt = psiFile.findElementAt(--offsetStep);
                if (elementAt instanceof PsiWhiteSpace) {
                    ++repair;
                }
            }

            String clazzName = elementAt.getText();
            List<String> importList = generateContext.getImportList();
            String importPkg = importList.stream().filter(f -> f.endsWith("." + clazzName + ";"))
                    .map(m -> m.replace("import", "").replace(";", "").trim())
                    .findFirst().orElse(clazzName);

            psiClass = JavaPsiFacade.getInstance(generateContext.getProject()).findClass(importPkg, GlobalSearchScope.allScope(generateContext.getProject()));
            if(Objects.isNull(psiClass)){
                throw new FuDocException("无法获取到目标类");
            }

            repair += psiClass.getName().length();
        }

        Pattern setMtd = Pattern.compile(setRegex);
        // 获取类的set方法并存放起来
        List<String> paramList = new ArrayList<>();
        Map<String, String> paramMtdMap = new HashMap<>();

        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiClass);
        for (PsiClass psi : psiClassLinkList) {
            List<String> methodsList = getMethods(psi, setRegex, "set");
            for (String methodName : methodsList) {
                // 替换属性
                String param = setMtd.matcher(methodName).replaceAll("$1").toLowerCase();
                // 保存获取的属性信息
                paramMtdMap.put(param, methodName);
                paramList.add(param);
            }
        }

        return new SetObjConfigDO(clazzParamName, paramList, paramMtdMap);
    }

    @Override
    protected GetObjConfigDO getObjConfigDOByClipboardText(GenerateContext generateContext) {
        // 获取剪切板信息 【实际使用可补充一些必要的参数判断】
        String systemClipboardText = Utils.getSystemClipboardText().trim();

        // 按照默认规则提取信息，例如：UserDto userDto
        String[] split = systemClipboardText.split("\\s");

        if (split.length < 2) {
            return new GetObjConfigDO(null, null, new HashMap<>());
        }

        String clazzName = split[0].trim();
        String clazzParam = split[1].trim();
        List<String> importList = generateContext.getImportList();
        String importPkg = importList.stream().filter(f -> f.endsWith("." + clazzName + ";"))
                .map(m -> m.replace("import", "").replace(";", "").trim())
                .findFirst().orElse(clazzName);
        // 获取类
        PsiClass psiClass = JavaPsiFacade.getInstance(generateContext.getProject()).findClass(importPkg, GlobalSearchScope.allScope(generateContext.getProject()));
        if(Objects.isNull(psiClass)){
            throw new FuDocException("无法获取到目标类");
        }
        List<PsiClass> psiClassLinkList = getPsiClassLinkList(psiClass);

        Map<String, String> paramMtdMap = new HashMap<>();
        Pattern getM = Pattern.compile(getRegex);

        for (PsiClass psi : psiClassLinkList) {
            List<String> methodsList = getMethods(psi, getRegex, "get");
            for (String methodName : methodsList) {
                String param = getM.matcher(methodName).replaceAll("$1").toLowerCase();
                paramMtdMap.put(param, methodName);
            }
        }

        return new GetObjConfigDO(clazzName, clazzParam, paramMtdMap);
    }

    @Override
    protected void weavingSetGetCode(GenerateContext generateContext, SetObjConfigDO setObjConfigDO, GetObjConfigDO getObjConfigDO) {
        Application application = ApplicationManager.getApplication();

        // 获取空格位置长度
        int distance = Utils.getWordStartOffset(generateContext.getEditorText(), generateContext.getOffset()) - generateContext.getStartOffset() - repair;

        application.runWriteAction(() -> {
            StringBuilder blankSpace = new StringBuilder();
            for (int i = 0; i < distance; i++) {
                blankSpace.append(" ");
            }

            int lineNumberCurrent = generateContext.getDocument().getLineNumber(generateContext.getOffset()) + 1;

            List<String> setMtdList = setObjConfigDO.getParamList();
            for (String param : setMtdList) {
                int lineStartOffset = generateContext.getDocument().getLineStartOffset(lineNumberCurrent++);

                WriteCommandAction.runWriteCommandAction(generateContext.getProject(), () -> {
                    generateContext.getDocument().insertString(lineStartOffset, blankSpace + setObjConfigDO.getClazzParamName() + "." + setObjConfigDO.getParamMtdMap().get(param) + "(" + (null == getObjConfigDO.getParamMtdMap().get(param) ? "" : getObjConfigDO.getClazzParam() + "." + getObjConfigDO.getParamMtdMap().get(param) + "()") + ");\n");
                    generateContext.getEditor().getCaretModel().moveToOffset(lineStartOffset + 2);
                    generateContext.getEditor().getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
                });

            }

        });

    }

}
