package com.wdf.fudoc.apidoc.sync.data;

import com.google.common.collect.Lists;
import com.wdf.fudoc.apidoc.config.state.FuDocSyncProjectSetting;
import com.wdf.fudoc.apidoc.constant.enumtype.ApiDocSystem;
import com.wdf.fudoc.apidoc.data.SyncApiConfigData;
import com.wdf.fudoc.apidoc.sync.dto.ApiCategoryDTO;
import com.wdf.fudoc.apidoc.sync.dto.ApiProjectDTO;
import com.wdf.fudoc.apidoc.sync.dto.SyncApiResultDTO;
import com.wdf.fudoc.util.JsonUtil;
import com.wdf.fudoc.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wangdingfu
 * @date 2023-01-08 20:29:16
 */
public class ShowDocConfigData extends BaseSyncConfigData{


    @Override
    public List<ApiProjectDTO> getProjectConfigList(String moduleName) {
        SyncApiConfigData state = FuDocSyncProjectSetting.getInstance().getState();
        if (Objects.isNull(state)) {
            return Lists.newArrayList();
        }
        return state.getShowDocConfigList().stream().filter(f -> Objects.isNull(f.getScope()) || f.getScope().isScope(moduleName))
                .map(this::buildApiProjectDTO).collect(Collectors.toList());
    }

    @Override
    public void syncApiProjectList(String moduleName, List<ApiProjectDTO> apiProjectDTOList) {
        SyncApiConfigData state = FuDocSyncProjectSetting.getInstance().getState();
        if (Objects.isNull(state)) {
            state = new SyncApiConfigData();
            FuDocSyncProjectSetting.getInstance().loadState(state);
        }
        List<ShowDocProjectTableData> showDocConfigList = state.getShowDocConfigList();
        showDocConfigList.removeIf(f -> Objects.isNull(f.getScope()) || f.getScope().isScope(moduleName));
        showDocConfigList.addAll(ObjectUtils.listToList(apiProjectDTOList, this::buildConfigData));
        FuDocSyncProjectSetting.getInstance().loadState(state);
    }

    @Override
    public boolean isExistsConfig() {
        return false;
    }

    @Override
    public void clearData(boolean isAll) {

    }

    @Override
    public ApiDocSystem getApiSystem() {
        return ApiDocSystem.SHOW_DOC;
    }

    @Override
    public String getApiDocUrl(SyncApiResultDTO syncApiResultDTO) {
        return StringUtils.EMPTY;
    }


    private ApiProjectDTO buildApiProjectDTO(ShowDocProjectTableData tableData) {
        ApiProjectDTO apiProjectDTO = new ApiProjectDTO();
        apiProjectDTO.setProjectId(tableData.getApiKey());
        apiProjectDTO.setProjectToken(tableData.getApiToken());
        apiProjectDTO.setProjectName(tableData.getProjectName());
        apiProjectDTO.setApiCategoryList(JsonUtil.toList(tableData.getCategories(), ApiCategoryDTO.class));
        apiProjectDTO.setScope(tableData.getScope());
        return apiProjectDTO;
    }

    private ShowDocProjectTableData buildConfigData(ApiProjectDTO projectDTO) {
        ShowDocProjectTableData configData = new ShowDocProjectTableData();
        configData.setApiKey(projectDTO.getProjectId());
        configData.setApiToken(projectDTO.getProjectToken());
        configData.setProjectName(projectDTO.getProjectName());
        configData.setCategories(JsonUtil.toJson(projectDTO.getApiCategoryList()));
        configData.setScope(projectDTO.getScope());
        return configData;
    }
}
