//package com.yonyou.cloud.i18n.utils;
//
//import com.yonyou.cloud.translate.entity.TranslateTraditional;
//import com.yonyou.i18n.model.MLResSubstitution;
//import com.yonyou.i18n.model.PageNode;
//import com.yonyou.i18n.utils.Helper;
//import com.yonyou.i18n.utils.StringUtils;
//import com.yonyou.i18n.utils.TranslateUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//public class PropertiesUtils {
//
//    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
//
//
//    public List<TranslateTraditional> prop2Entity(Properties properties) throws Exception {
//
//        List<TranslateTraditional> listData = new ArrayList<TranslateTraditional>();
//        TranslateTraditional translate = null;
//
//        for (String key : properties.stringPropertyNames()) {
//
//            translate = new TranslateTraditional();
//            translate.setPropertyCode(key);
//            translate.setChinese(properties.getProperty(key));
//
//            listData.add(translate);
//        }
//
//        return listData;
//    }
//
//
//    public static void pageNodes2Entity(List<TranslateTraditional> listData, List<PageNode> pageNodes, String locales) throws Exception {
//
//        logger.info("开始执行资源到对象的封装！");
//
//
//        TranslateTraditional translate = null;
//
//        // 新建对象
//        for (PageNode pageNode : pageNodes) {
//            ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();
//
//            for (MLResSubstitution rs : rss) {
//                // 在写入资源文件时，去掉前后的界定符号
//
//                translate = new TranslateTraditional();
//
//                String v = rs.getValue();
//                if (v.length() <= 2) continue;
//
//                translate.setPropertyCode(rs.getKey());
//
//                if (locales == null || "".equals(locales) || "zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
//                    translate.setChinese(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                } else if ("zh_TW".equalsIgnoreCase(locales) || "tw".equalsIgnoreCase(locales)) {
//                    translate.setTraditional(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                } else if ("en_US".equalsIgnoreCase(locales) || "en_UK".equalsIgnoreCase(locales) || "en".equalsIgnoreCase(locales)) {
//                    translate.setEnglish(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                } else if ("fr_FR".equalsIgnoreCase(locales) || "fr".equalsIgnoreCase(locales)) {
//                    translate.setFrench(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                } else if (translate.getReserve1() == null || "".equals(translate.getReserve1())) {
//                    translate.setReserve1(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                } else {
//                    translate.setReserve2(TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
//                }
//
//                listData.add(translate);
//            }
//        }
//
//        logger.info("执行资源到对象的封装完成，准备保存数据库！");
//    }
//
//
//}
