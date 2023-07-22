package com.wdf.fudoc.request.po;


import com.google.common.collect.Lists;
import com.wdf.fudoc.components.bo.KeyValueTableBO;
import com.wdf.fudoc.components.bo.TreePathBO;
import com.wdf.fudoc.request.constants.enumtype.ViewMode;
import com.wdf.fudoc.request.pojo.ConfigAuthTableBO;
import com.wdf.fudoc.request.pojo.ConfigEnvTableBO;
import com.wdf.fudoc.request.tab.settings.GlobalPreScriptTab;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * [Fu Request]配置持久化对象
 *
 * @author wangdingfu
 * @date 2023-06-07 23:38:12
 */
@Getter
@Setter
public class FuRequestConfigPO {

    /**
     * 【Fu Request】展示模式
     */
    private String viewMode = ViewMode.SINGLE_PINNED.myActionID;

    /**
     * 是否自动从配置文件读取启动端口
     */
    private boolean autoPort = true;

    /**
     * 全局请求头
     */
    private List<GlobalKeyValuePO> globalHeaderList = Lists.newArrayList();

    /**
     * 全局变量
     */
    private List<GlobalKeyValuePO> globalVariableList = Lists.newArrayList();

    /**
     * 前置脚本集合
     */
    private Map<String, GlobalPreScriptPO> preScriptMap = new ConcurrentHashMap<>();


    private List<ConfigEnvTableBO> envConfigList = Lists.newArrayList();

    private List<ConfigAuthTableBO> authConfigList = Lists.newArrayList();

    /**
     * 自定义表格配置
     */
    private Map<String, List<KeyValueTableBO>> customTableConfigMap = new HashMap<>();

    /**
     * cookie集合
     */
    private List<FuCookiePO> cookies = Lists.newArrayList();


    /**
     * 当前选中的用户名
     */
    private String userName;

    /**
     * 当前选中的环境
     */
    private Map<String, String> defaultEnvMap = new HashMap<>();

    public void addDefaultEnv(String moduleName, String defaultEnv) {
        if (StringUtils.isBlank(moduleName) || StringUtils.isBlank(defaultEnv)) {
            return;
        }
        defaultEnvMap.put(moduleName, defaultEnv);
    }

    public String getEnv(String moduleName) {
        if (StringUtils.isBlank(moduleName)) {
            return StringUtils.EMPTY;
        }
        return defaultEnvMap.get(moduleName);
    }


    public List<GlobalPreScriptPO> getPreScriptList(String scope) {
        List<GlobalPreScriptPO> preScriptPOList = Lists.newArrayList();
        preScriptMap.forEach((key, value) -> {
            if (value.getScope().contains(scope)) {
                preScriptPOList.add(value);
            }
        });
        return preScriptPOList;
    }


    public String header(String headerName, List<String> scope) {
        return globalHeaderList.stream().filter(KeyValueTableBO::getSelect).filter(f -> f.getKey().equals(headerName)).filter(f -> contains(scope, f.getScope().getSelectPathList())).map(KeyValueTableBO::getValue).findFirst().orElse(StringUtils.EMPTY);
    }

    public String variable(String variableName, List<String> scope) {
        return globalVariableList.stream().filter(KeyValueTableBO::getSelect).filter(f -> f.getKey().equals(variableName)).filter(f -> contains(scope, f.getScope().getSelectPathList())).map(KeyValueTableBO::getValue).findFirst().orElse(StringUtils.EMPTY);
    }

    public String variable(String variableName) {
        return globalVariableList.stream().filter(KeyValueTableBO::getSelect).filter(f -> f.getKey().equals(variableName)).map(KeyValueTableBO::getValue).findFirst().orElse(StringUtils.EMPTY);
    }


    private boolean contains(List<String> scope1, List<String> scope2) {
        if (CollectionUtils.isEmpty(scope1) || CollectionUtils.isEmpty(scope2)) {
            return false;
        }
        return scope1.stream().anyMatch(scope2::contains);
    }

    public void addHeader(String headerName, String headerValue, List<String> scope) {
        GlobalKeyValuePO globalKeyValuePO = globalHeaderList.stream().filter(f -> f.getKey().equals(headerName)).findFirst().orElse(null);
        if (Objects.isNull(globalKeyValuePO)) {
            globalKeyValuePO = new GlobalKeyValuePO();
            this.globalHeaderList.add(globalKeyValuePO);
        }
        globalKeyValuePO.setScope(new TreePathBO(scope));
        globalKeyValuePO.setKey(headerName);
        globalKeyValuePO.setValue(headerValue);
        globalKeyValuePO.setSelect(true);
    }

    public void addVariable(String variableName, String variableValue, List<String> scope) {
        GlobalKeyValuePO globalKeyValuePO = globalVariableList.stream().filter(f -> f.getKey().equals(variableName)).findFirst().orElse(null);
        if (Objects.isNull(globalKeyValuePO)) {
            globalKeyValuePO = new GlobalKeyValuePO();
            this.globalVariableList.add(globalKeyValuePO);
        }
        globalKeyValuePO.setScope(new TreePathBO(scope));
        globalKeyValuePO.setKey(variableName);
        globalKeyValuePO.setValue(variableValue);
        globalKeyValuePO.setSelect(true);
    }


    public void addCookies(List<FuCookiePO> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }
        List<String> nameList = cookies.stream().map(FuCookiePO::getName).toList();
        //移除重复的cookie
        this.cookies.removeIf(f -> nameList.contains(f.getName()));
        this.cookies.addAll(cookies);
    }
}
