package com.wdf.apidoc.data;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author wangdingfu
 * @descption: FuDoc数据上下文
 * @date 2022-05-30 23:32:11
 */
public class FuDocDataContent {

    private static final Map<Long, FuDocData> FU_DOC_DATA_MAP = new ConcurrentHashMap<>();


    /**
     * 向全局上下文中 新增|获取 数据内容
     *
     * @param consumer 消费者
     */
    public static void consumerData(Consumer<FuDocData> consumer) {
        if (Objects.isNull(consumer)) {
            return;
        }
        long threadId = Thread.currentThread().getId();
        FuDocData fuDocData = FU_DOC_DATA_MAP.get(threadId);
        if (Objects.isNull(fuDocData)) {
            fuDocData = new FuDocData();
            FU_DOC_DATA_MAP.put(threadId, fuDocData);
        }
        consumer.accept(fuDocData);
    }


    /**
     * 从全局上下文中获取FuDoc数据对象
     *
     * @return FuDoc数据对象
     */
    public static FuDocData getFuDocData() {
        long threadId = Thread.currentThread().getId();
        return FU_DOC_DATA_MAP.get(threadId);
    }


    /**
     * 从全局上下文中获取Project对象
     *
     * @return 当前操作的项目对象
     */
    public static Project getProject() {
        FuDocData fuDocData = getFuDocData();
        if (Objects.nonNull(fuDocData)) {
            return fuDocData.getEvent().getProject();
        }
        return null;
    }


    /**
     * 获取当前事件选中的文本内容
     */
    public static String getSelectedText() {
        //获取当前编辑器对象
        Editor editor = getFuDocData().getEvent().getRequiredData(CommonDataKeys.EDITOR);
        //获取选择的数据模型
        SelectionModel selectionModel = editor.getSelectionModel();
        //获取当前选择的文本
        return selectionModel.getSelectedText();
    }

}
