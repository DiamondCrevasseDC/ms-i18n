package com.yonyou.cloud.i18n.service;

import com.wordnik.swagger.annotations.ApiOperation;
import com.yonyou.cloud.middleware.rpc.Async;
import com.yonyou.cloud.middleware.rpc.RemoteCall;
import com.yonyou.cloud.i18n.constants.AppI18nConstant;
import com.yonyou.cloud.mwclient.servmeta.annotation.ApiParam;
import com.yonyou.cloud.mwclient.servmeta.annotation.ApiReturnValue;

/**
 * 国际化工具的接口
 * 主要涉及附件上传、下载，
 * 以及国际化工具的执行
 *
 * @author yy
 */
@RemoteCall(AppI18nConstant.APP_I18N_PROVIDER)
public interface II18nService {


    /**
     * client -> provider
     * 国际化工具执行
     *
     * @param id 482c5b633aea4a46aa0cd1fd0a62cc27
     * @return 成功或失败
     */
    @ApiOperation("根据路径执行国际化工具")
    public @ApiReturnValue(name = "执行状态", description = "国际化工具执行状态")
    Boolean operation(@ApiParam(name = "执行的行ID", required = true, description = "需要国际化工具处理的项目所在行的主键", exampleValue = "482c5b633aea4a46aa0cd1fd0a62cc27") String id) throws Exception;

}


