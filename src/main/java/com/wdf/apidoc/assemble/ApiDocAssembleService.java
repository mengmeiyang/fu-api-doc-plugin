package com.wdf.apidoc.assemble;

import com.wdf.apidoc.pojo.bo.AssembleBO;
import com.wdf.apidoc.pojo.data.FuApiDocData;
import com.wdf.apidoc.pojo.desc.ClassInfoDesc;
import com.wdf.apidoc.pojo.desc.MethodInfoDesc;

import java.util.List;

/**
 * @author wangdingfu
 * @descption: ApiDoc对象组装
 * @date 2022-05-08 22:19:29
 */
public interface ApiDocAssembleService {


    /**
     * 组装FuApiDocData对象(用于渲染接口文档模板)
     *
     * @param assembleBO 组装参数
     * @return 生成FuApiDoc的数据对象
     */
    FuApiDocData assemble(AssembleBO assembleBO);
}
