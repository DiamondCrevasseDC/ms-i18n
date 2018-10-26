package com.yonyou.cloud.translate.service;

import com.yonyou.cloud.i18n.service.I18nToolsService;
import com.yonyou.cloud.i18n.service.ITranslateToolsService;
import com.yonyou.cloud.translate.entity.Translate;
import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.StringUtils;
import com.yonyou.i18n.utils.TranslateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * spring-boot工程示例，服务具体实现，统计功能
 *
 * @author liujmc
 * @version 5.0.0-RELEASE
 */
@Service
public class TranslateToolsService implements ITranslateToolsService {

    private static final Logger logger = LoggerFactory.getLogger(I18nToolsService.class);

    private TranslateService translateService;

    @Autowired
    public void setTranslateService(TranslateService translateService) {
        this.translateService = translateService;
    }


//    @Override
//    public Boolean saveTranslate(Properties properties) throws Exception {
//
//        logger.info("开始执行资源写入");
//
//        List<Translate> listData = new ArrayList<Translate>();
//        Translate translate = null;
//        for (String key : properties.stringPropertyNames()) {
//
//            translate = new Translate();
//            translate.setPropertyCode(key);
//            translate.setChinese(properties.getProperty(key));
//
//            listData.add(translate);
//        }
//
//        this.translateService.saveBatch(listData);
//
//        logger.info("执行资源写入结束");
//
//        return true;
//    }


    //    @Override
    public List<Translate> getList(Properties properties) throws Exception {

        return this.translateService.findByCode(properties);

    }

    @Override
    public Boolean updateTranslate(Properties properties, String locales) throws Exception {

        logger.info("开始执行资源更新！");

        List<Translate> listData = this.getList(properties);

        for (Translate translate : listData) {

            String key = translate.getPropertyCode();

            if (locales == null || "".equals(locales) || "zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
                translate.setChinese(properties.getProperty(key));
            } else if ("zh_TW".equalsIgnoreCase(locales) || "tw".equalsIgnoreCase(locales)) {
                translate.setTraditional(properties.getProperty(key));
            } else if ("en_US".equalsIgnoreCase(locales) || "en_UK".equalsIgnoreCase(locales) || "en".equalsIgnoreCase(locales)) {
                translate.setEnglish(properties.getProperty(key));
            } else if ("fr_FR".equalsIgnoreCase(locales) || "fr".equalsIgnoreCase(locales)) {
                translate.setFrench(properties.getProperty(key));
            } else {
                translate.setReserve1(properties.getProperty(key));
            }

            translate.setChinese(properties.getProperty(key));

            listData.add(translate);
        }

        this.translateService.saveBatch(listData);

        logger.info("执行资源写入更新结束！");

        return true;

    }

    @Override
    public Boolean saveTranslate(Properties properties, Map<String, String> map) throws Exception {

        logger.info("开始执行资源的多语种解析并存入数据库！");

        Iterator<Map.Entry<String, String>> mlrts = null;
        List<Translate> listData = new ArrayList<Translate>();
        Translate translate = null;


        for (String key : properties.stringPropertyNames()) {

            translate = new Translate();
            translate.setPropertyCode(key);
//            translate.setChinese(properties.getProperty(key));

            mlrts = map.entrySet().iterator();
            while (mlrts.hasNext()) {
                Map.Entry<String, String> mlrt = mlrts.next();
                String locales = mlrt.getKey();

                if (locales == null || "".equals(locales) || "zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
                    translate.setChinese(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                } else if ("zh_TW".equalsIgnoreCase(locales) || "tw".equalsIgnoreCase(locales)) {
                    translate.setTraditional(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                } else if ("en_US".equalsIgnoreCase(locales) || "en_UK".equalsIgnoreCase(locales) || "en".equalsIgnoreCase(locales)) {
                    translate.setEnglish(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                } else if ("fr_FR".equalsIgnoreCase(locales) || "fr".equalsIgnoreCase(locales)) {
                    translate.setFrench(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                } else if (translate.getReserve1() == null || "".equals(translate.getReserve1())) {
                    translate.setReserve1(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                } else {
                    translate.setReserve2(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(properties.getProperty(key))), locales));
                }
            }

            listData.add(translate);
        }

        this.translateService.saveBatch(listData);

        logger.info("执行资源写入数据库完成！");

        return true;

    }

    @Override
    public HashSet<String> getCode() throws Exception {

        HashSet<String> keyPrefix = new HashSet<String>();

        List<Translate> listData = this.translateService.findAll();

        for (Translate translate : listData) {

            String key = translate.getPropertyCode();

            if (key.indexOf(".") >= 0) {
                key = key.substring(0, key.lastIndexOf("."));

                keyPrefix.add(key);
            }
        }

        return keyPrefix;
    }

//    @Override
//    public Boolean saveTranslate(List<Translate> list) throws Exception {
//
//        logger.info("开始执行资源写入");
//
//        this.translateService.saveBatch(list);
//
//        logger.info("执行资源写入结束");
//
//        return true;
//
//    }
}
