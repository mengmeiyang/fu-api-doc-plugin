package com.wdf.fudoc.request.tab.settings;

import com.google.common.collect.Lists;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.ui.JBUI;
import com.wdf.fudoc.common.FuDataTab;
import com.wdf.fudoc.components.FuTabComponent;
import com.wdf.fudoc.components.FuTableComponent;
import com.wdf.fudoc.components.factory.FuTableColumnFactory;
import com.wdf.fudoc.request.po.FuRequestConfigPO;
import com.wdf.fudoc.request.pojo.ConfigAuthTableBO;
import com.wdf.fudoc.request.pojo.ConfigEnvTableBO;
import icons.FuDocIcons;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.List;

/**
 * 全局配置维护
 *
 * @author wangdingfu
 * @date 2022-12-07 21:48:14
 */
public class GlobalConfigTab implements FuDataTab<FuRequestConfigPO> {

    private final VerticalBox rootBox;

    private final FuTableComponent<ConfigEnvTableBO> envTable;
    private final FuTableComponent<ConfigAuthTableBO> authTable;

    public GlobalConfigTab() {
        this.envTable = FuTableComponent.create("env", FuTableColumnFactory.envConfig(), ConfigEnvTableBO.class);
        this.authTable = FuTableComponent.create("auth", FuTableColumnFactory.authConfig(), ConfigAuthTableBO.class);
        this.rootBox = new VerticalBox();
        JPanel envPanel = this.envTable.createPanel();
        JPanel authPanel = this.authTable.createPanel();
        TitledBorder envBorder = BorderFactory.createTitledBorder("环境配置");
        envBorder.setBorder(JBUI.Borders.empty());
        envPanel.setBorder(envBorder);
        TitledBorder authBorder = BorderFactory.createTitledBorder("用户授权配置");
        authBorder.setBorder(JBUI.Borders.empty());
        authPanel.setBorder(authBorder);
        this.rootBox.add(envPanel);
        this.rootBox.add(authPanel);
    }

    @Override
    public TabInfo getTabInfo() {
        return FuTabComponent.getInstance("全局配置", FuDocIcons.FU_SETTINGS, this.rootBox).builder();
    }

    @Override
    public void initData(FuRequestConfigPO data) {
        List<ConfigEnvTableBO> envConfigList = data.getEnvConfigList();
        List<ConfigAuthTableBO> authConfigList = data.getAuthConfigList();
        if (CollectionUtils.isNotEmpty(envConfigList)) {
            this.envTable.setDataList(Lists.newArrayList(envConfigList));
        }
        if (CollectionUtils.isNotEmpty(authConfigList)) {
            this.authTable.setDataList(Lists.newArrayList(authConfigList));
        }
    }

    @Override
    public void saveData(FuRequestConfigPO data) {
        data.setEnvConfigList(Lists.newArrayList(this.envTable.getDataList()));
        data.setAuthConfigList(Lists.newArrayList(this.authTable.getDataList()));
    }
}
