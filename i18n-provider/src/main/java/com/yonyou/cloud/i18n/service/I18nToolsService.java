package com.yonyou.cloud.i18n.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 执行对象的rpc调用使用该类
 */
@Service
public class I18nToolsService {

    private static final Logger logger = LoggerFactory.getLogger(I18nToolsService.class);

    @Autowired(required=true)
    private II18nToolsService i18nToolsService;

    /**
     * @param path
     * @return
     * @throws Exception
     */
    public String operation(String path) throws Exception {


        String callback = this.i18nToolsService.operateTools(path);

        logger.info("项目回写路径为：" + callback);

        return callback;
    }

    /**
     * @param path
     * @return
     * @throws Exception
     */
    public String operation(String path, String projectType) throws Exception {


        String callback = this.i18nToolsService.operateTools(path, projectType);

        logger.info("项目回写路径为：" + callback);

        return callback;
    }

}
