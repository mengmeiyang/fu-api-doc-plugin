package com.wdf.fudoc.start;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.wdf.fudoc.components.FuHtmlComponent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangdingfu
 * @date 2022-12-03 17:50:12
 */
public class FuDocUpdateDialog implements StartupActivity {

    private static String html = "<html><head><meta charset=\"utf-8\" /></head><body><div style=\"width: 750px; margin: auto;\"><h1 style=\"text-align:center;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:15.959999999999999px;font-weight:bold;display:table;margin:2em auto 1em;padding:0 1em;border-bottom:2px solid rgba(15, 76, 129, 1);color:#3f3f3f;margin-top: 0\">FuDoc</h1><blockquote style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:14px;font-style:normal;border-left:none;padding:1em;border-radius:8px;color:rgba(0,0,0,0.5);background:#f7f7f7;margin:2em 8px\"><p style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:1em;letter-spacing:0.1em;color:rgb(80, 80, 80);display:block\">一个提升你工作效率的接口文档生成插件</p></blockquote><h1 style=\"text-align:center;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:15.959999999999999px;font-weight:bold;display:table;margin:2em auto 1em;padding:0 1em;border-bottom:2px solid rgba(15, 76, 129, 1);color:#3f3f3f\">概述</h1><blockquote style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:14px;font-style:normal;border-left:none;padding:1em;border-radius:8px;color:rgba(0,0,0,0.5);background:#f7f7f7;margin:2em 8px\"><p style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:1em;letter-spacing:0.1em;color:rgb(80, 80, 80);display:block\"><code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Fu Doc</code>可以一键帮你生成接口文档. 相比于<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Swagger</code> <code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Api Fox</code> <code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Yapi</code>等. <code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Fu Doc</code>更能帮你提升工作效率.\n" +
            "使用<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Fu Doc</code>不需要添加任何依赖, 不需要额外去配置一堆参数. 只需要按下快捷键就能生成你平时需要花费大量时间编写的接口文档\n" +
            "你只需要按照<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">JAVA DOC</code>规范写好注释即可</p></blockquote><p style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:14px;margin:1.5em 8px;letter-spacing:0.1em;color:#3f3f3f\">查看<span style=\"text-align:left;line-height:1.75;color:#576b95\">快速开始</span>了解详情。</p><h2 style=\"text-align:center;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:15.400000000000002px;font-weight:bold;display:table;margin:4em auto 2em;padding:0 0.2em;background:rgba(15, 76, 129, 1);color:#fff\">特性</h2><ul style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:14px;margin-left:0;padding-left:1em;list-style:circle;color:#3f3f3f\"><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>对开发项目完全零侵入</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>依托与IDEA插件市场 安装方便 使用简单 无任何学习成本</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>兼容<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">@Valid</code>校验注解</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持读取自定义注解内容渲染到接口文档</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持在接口文档模板中直接通过<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">${fudoc.tagName}</code>的方式获取注释内容</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持自定义编辑接口文档模板</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持枚举类生成<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">key-value</code>格式和<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">table</code>格式的字典说明</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持在字段上标识<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">@see</code>注解引用枚举 生成该字段的枚举字段描述说明信息</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Dubbo</code> <code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Feign</code>等接口生成接口文档</li><li style=\"text-align:left;line-height:1.75;text-indent:-1em;display:block;margin:0.2em 8px;color:#3f3f3f\"><span>• </span>支持对<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">JAVA</code>对象的属性生成markdown格式的表格信息展示</li></ul><h2 style=\"text-align:center;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:15.400000000000002px;font-weight:bold;display:table;margin:4em auto 2em;padding:0 0.2em;background:rgba(15, 76, 129, 1);color:#fff\">示例</h2><p style=\"text-align:left;line-height:1.75;font-family:-apple-system-font,BlinkMacSystemFont, Helvetica Neue, PingFang SC, Hiragino Sans GB , Microsoft YaHei UI , Microsoft YaHei ,Arial,sans-serif;font-size:14px;margin:1.5em 8px;letter-spacing:0.1em;color:#3f3f3f\">可以查看 <span style=\"text-align:left;line-height:1.75;color:#576b95\">示例</span> 来了解更多使用<code style=\"text-align:left;line-height:1.75;font-size:90%;white-space:pre;color:#d14;background:rgba(27,31,35,.05);padding:3px 5px;border-radius:4px\">Fu Doc</code>的案例。</p></div></body></html>";

    @Override
    public void runActivity(@NotNull Project project) {
        new FuHtmlComponent(project, "测试html消息", html).showAndGet();
    }
}
