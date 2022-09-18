package com.wdf.fudoc.request.tab;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.impl.IdeGlassPaneImpl;
import com.intellij.ui.JBColor;
import com.intellij.ui.tabs.TabInfo;
import com.wdf.fudoc.apidoc.constant.enumtype.RequestType;
import com.wdf.fudoc.common.FuTab;
import com.wdf.fudoc.components.FuTabComponent;
import com.wdf.fudoc.request.InitRequestData;
import com.wdf.fudoc.request.pojo.FuHttpRequestData;
import com.wdf.fudoc.request.pojo.FuRequestData;
import com.wdf.fudoc.test.factory.FuTabBuilder;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * http请求部分内容
 *
 * @author wangdingfu
 * @date 2022-09-17 18:05:36
 */
@Getter
public class RequestTabView implements FuTab, InitRequestData {

    private final Project project;

    private final JRootPane rootPane;

    private final JPanel mainPanel;

    /**
     * 请求类型
     */
    private final JComboBox<String> requestTypeComponent;

    /**
     * 请求地址
     */
    private final JTextField requestUrlComponent;

    /**
     * 发送按钮
     */
    private final JButton sendBtn;

    /**
     * 请求头tab页
     */
    private final HttpHeaderTab httpHeaderTab;
    /**
     * GET请求参数tab页
     */
    private final HttpGetParamsTab httpGetParamsTab;
    /**
     * POST请求参数tab页
     */
    private final HttpRequestBodyTab httpRequestBodyTab;

    /**
     * api接口url
     */
    private String apiUrl;

    public RequestTabView(Project project) {
        this.project = project;
        this.mainPanel = new JPanel(new BorderLayout());
        this.requestTypeComponent = new ComboBox<>(RequestType.getItems());
        this.requestUrlComponent = new JTextField();
        this.sendBtn = new JButton("Send");
        this.httpHeaderTab = new HttpHeaderTab();
        this.httpGetParamsTab = new HttpGetParamsTab(this);
        this.httpRequestBodyTab = new HttpRequestBodyTab();
        this.rootPane = new JRootPane();
        initRootPane();
        initUI();
        this.sendBtn.addActionListener(e -> {
            //发送请求
        });
    }


    private void initUI() {
        //send区域
        this.mainPanel.add(initSendPanel(), BorderLayout.NORTH);
        //请求参数区域
        this.mainPanel.add(FuTabBuilder.getInstance()
                .addTab(this.httpHeaderTab.getTabInfo())
                .addTab(this.httpGetParamsTab.getTabInfo())
                .addTab(this.httpRequestBodyTab.getTabInfo())
                .build(), BorderLayout.CENTER);
    }

    private JPanel initSendPanel() {
        JPanel sendPane = new JPanel(new BorderLayout());
        //请求类型
        this.requestTypeComponent.setBackground(new JBColor(new Color(74, 136, 199), new Color(74, 136, 199)));
        sendPane.add(this.requestTypeComponent, BorderLayout.WEST);
        //请求url
        sendPane.add(this.requestUrlComponent, BorderLayout.CENTER);
        //send按钮
        sendPane.add(this.sendBtn, BorderLayout.EAST);
        return sendPane;
    }


    @Override
    public TabInfo getTabInfo() {
        return FuTabComponent.getInstance("Request", null, this.rootPane).builder();
    }


    @Override
    public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
        rootPane.setDefaultButton(sendBtn);
    }


    public String getRequestUrl() {
        return this.requestUrlComponent.getText();
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrlComponent.setText(requestUrl);
    }



    public void initRootPane() {
        final IdeGlassPaneImpl glass = new IdeGlassPaneImpl(rootPane);
        rootPane.setGlassPane(glass);
        glass.setVisible(true);
        rootPane.setContentPane(this.mainPanel);
        rootPane.setDefaultButton(this.getSendBtn());
    }

    /**
     * 初始化请求数据
     *
     * @param httpRequestData 发起http请求的数据
     */
    @Override
    public void initData(FuHttpRequestData httpRequestData) {
        FuRequestData request = httpRequestData.getRequest();
        this.requestTypeComponent.setSelectedItem(request.getRequestType().getRequestType());
        this.apiUrl = request.getRequestUrl();
        this.httpHeaderTab.initData(httpRequestData);
        this.httpGetParamsTab.initData(httpRequestData);
        this.httpRequestBodyTab.initData(httpRequestData);
    }
}
