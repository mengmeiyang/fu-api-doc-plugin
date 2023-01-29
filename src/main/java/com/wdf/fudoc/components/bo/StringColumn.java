package com.wdf.fudoc.components.bo;

import com.wdf.fudoc.components.factory.TableCellEditorFactory;
import lombok.Getter;
import lombok.Setter;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author wangdingfu
 * @date 2022-08-16 22:40:37
 */
@Getter
@Setter
public class StringColumn<T> extends Column {

    /**
     * get方法
     */
    private Function<T, String> getFun;
    /**
     * set方法
     */
    private BiConsumer<T, String> setFun;

    private TableCellRenderer tableCellRenderer;


    public StringColumn(String name, Function<T, String> getFun, BiConsumer<T, String> setFun) {
        super(name, TableCellEditorFactory.createTextFieldEditor());
        this.getFun = getFun;
        this.setFun = setFun;
    }

    public StringColumn(String name, Function<T, String> getFun, BiConsumer<T, String> setFun, TableCellEditor editor) {
        super(name, editor);
        this.getFun = getFun;
        this.setFun = setFun;
    }

    public StringColumn(String name, TableCellRenderer cellRenderer, Function<T, String> getFun, BiConsumer<T, String> setFun) {
        super(name, TableCellEditorFactory.createTextFieldEditor());
        this.getFun = getFun;
        this.setFun = setFun;
        this.tableCellRenderer = cellRenderer;
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return this.tableCellRenderer;
    }

    @Override
    public Class<?> getColumnClass() {
        return String.class;
    }
}
