package com.yonyou.cloud.i18n.service;

import com.wordnik.swagger.annotations.ApiOperation;
import com.yonyou.cloud.i18n.constants.AppI18nConstant;
import com.yonyou.cloud.middleware.rpc.Async;
import com.yonyou.cloud.middleware.rpc.RemoteCall;
import com.yonyou.cloud.mwclient.servmeta.annotation.ApiParam;
import com.yonyou.cloud.mwclient.servmeta.annotation.ApiReturnValue;

/**
 * 国际化工具的接口
 * 主要涉及附件上传、下载，
 * 以及国际化工具的执行
 *
 * @author yy
 */
@RemoteCall(AppI18nConstant.APP_I18N_SERVER)
public interface II18nToolsService {

    /**
     * provider -> server
     * 国际化工具执行
     * delete 异步调用，该部分执行时间较长。
     * add 最终的数据路径需要保存到数据库
     *
     * @param sourcePath /iuap/i18ntools/images/***.zip
     * @return 返回路径
     */
//    @Async
    @ApiOperation("根据路径执行国际化工具")
    public @ApiReturnValue(name = "返回路径", description = "国际化工具执行完成后的回写路径")
    String operateTools(@ApiParam(name = "原始路径", required = true, description = "需要国际化工具处理的项目的完整路径，最好是服务器本地路径", exampleValue = "/iuap/i18ntools/images/***.zip") String sourcePath) throws Exception;


    /**
     * provider -> server
     * 国际化工具执行
     * delete 异步调用，该部分执行时间较长。
     * add 最终的数据路径需要保存到数据库
     *
     * @param sourcePath /iuap/i18ntools/images/***.zip
     * @param projectType
     * @return 返回路径
     */
//    @Async
    @ApiOperation("根据路径执行国际化工具")
    public @ApiReturnValue(name = "返回路径", description = "国际化工具执行完成后的回写路径")
    String operateTools(@ApiParam(name = "原始路径", required = true, description = "需要国际化工具处理的项目的完整路径，最好是服务器本地路径", exampleValue = "/iuap/i18ntools/images/***.zip") String sourcePath,
                        @ApiParam(name = "项目类型", required = true, description = "处理的项目的类型", exampleValue = "UUI React") String projectType) throws Exception;



}



