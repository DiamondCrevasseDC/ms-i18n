package com.yonyou.cloud.i18n.service;

import com.yonyou.i18n.core.ExtractChar;
import com.yonyou.i18n.main.StepBy;
import com.yonyou.i18n.utils.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;

/**
 * 服务层： 提供国际化工具的API调用， 同时将抽取的资源写入到数据库
 *
 * @author wenfan
 */
@Service
public class I18nToolsService implements II18nToolsService {

    private static final Logger logger = LoggerFactory.getLogger(I18nToolsService.class);

    @Autowired(required = true)
    public ITranslateToolsService iTranslateToolsService;

    /**
     * 执行工具逻辑
     *
     * @param sourcePath /iuap/i18ntools/images/***.zip
     * @return
     * @throws Exception
     */
    @Override
    public String operateTools(String sourcePath) throws Exception {

        /********************执行上传文件的解压缩*************************/
        logger.info("识别文件：" + sourcePath);

        String path = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_" + System.currentTimeMillis();

        String zipFile = path + ".zip";

        path = path + "/";

        logger.info("解压缩路径：" + path);

        ZipUtils.unZipForFilePath(sourcePath, path);

        /*********************执行国际化工具的主体方法************************/
        StepBy sb = new StepBy();

        sb.init(path);

        sb.extract();

        sb.resource();

        sb.replace();

        /*********************执行文件的压缩供下载使用************************/
        ZipUtils.zip(new File(zipFile), path);

        logger.info("执行完成后压缩路径：" + zipFile);

        /*********************资源保存完成后添加对数据库的写入操作************************/
        try {
            // 按照类型生成文件
//            Iterator<Map.Entry<String, String>> mlrts = sb.getMlrts();
//
////            List<Translate> list = new ArrayList<Translate>();
//            while (mlrts.hasNext()) {
//                Map.Entry<String, String> mlrt = mlrts.next();
//                String locales = mlrt.getKey();
//
//                if (locales == null || "".equals(locales) || "zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
//                    iTranslateToolsService.saveTranslate(sb.getPageNodesProperties(locales));
//                } else {
//                    iTranslateToolsService.updateTranslate(sb.getPageNodesProperties(locales), locales);
//                }
//
////                PropertiesUtils.pageNodes2Entity(list, sb.getPageNodes(), locales);
//            }


            iTranslateToolsService.saveTranslate(sb.getPageNodesProperties(), sb.getMlrts());


//            iTranslateToolsService.saveTranslate(list);

        } catch (Exception e) {
            // DO Nothing
        }

        /*********************返回并写入数据库************************/
        return zipFile;
    }

    /**
     * 添加对项目类型的支持：
     * 主要处理UUI  React的项目
     *
     * @param sourcePath  /iuap/i18ntools/images/***.zip
     * @param projectType
     * @return
     * @throws Exception
     */
    @Override
    public String operateTools(String sourcePath, String projectType) throws Exception {

        /********************采用静态对象保存所有key的前缀*************************/
        // 原则上一个运行的服务只需要执行一次
        new ExtractChar().setKeyPrefixs(iTranslateToolsService.getCode());

        /********************执行上传文件的解压缩*************************/
        logger.info("识别文件：" + sourcePath);

        String path = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_" + System.currentTimeMillis();

        String zipFile = path + ".zip";

        path = path + "/";

        logger.info("解压缩路径：" + path);

        ZipUtils.unZipForFilePath(sourcePath, path);

        try {

            /*********************执行国际化工具的主体方法************************/
            StepBy sb = new StepBy();

            sb.init(path, projectType);

            sb.extract();

            sb.resource();

            sb.replace();

            /*********************执行文件的压缩供下载使用************************/
            ZipUtils.zip(new File(zipFile), path);

            logger.info("执行完成后压缩路径：" + zipFile);

            /*********************资源保存完成后添加对数据库的写入操作************************/

            iTranslateToolsService.saveTranslate(sb.getPageNodesProperties(), sb.getMlrts());

        } catch (Exception e) {


            // 异常在该部分统一处理

            logger.error(e.getMessage());

            throw e;

        }

        /*********************返回并写入数据库************************/
        return zipFile;

    }
}
