package com.yonyou.i18n.utils;

import net.sf.chineseutils.ChineseUtils;

/**
 * 处理多语间的翻译
 * <p>
 * 目前主要支持中文简体以及繁体之间的翻译
 * <p>
 * TODO
 * 后续将中文与英文之间的通过语料库或者DB的资源进行自动匹配翻译
 *
 * @author wenfan
 */
public class TranslateUtils {

    public static String transByLocales(String str, String locales) {

        if (locales == null || "".equals(locales)) {
            return str;
        } else if ("zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
            return str;
        } else if ("zh_TW".equalsIgnoreCase(locales) || "tw".equalsIgnoreCase(locales)) {
            return ChineseUtils.simpToTrad(str, true);
        } else if ("en_US".equalsIgnoreCase(locales) || "en".equalsIgnoreCase(locales)) {
            // TODO
            // 添加对英语翻译的词条库的检索
            return str + "EN";
        } else {
            return str + locales.toUpperCase();
        }

    }

    public static String transByLocales(String str, boolean glossaryMapping, String locales) {

        if (locales == null || "".equals(locales)) {
            return str;
        } else if ("zh_CN".equalsIgnoreCase(locales) || "cn".equalsIgnoreCase(locales)) {
            return str;
        } else if ("zh_TW".equalsIgnoreCase(locales) || "tw".equalsIgnoreCase(locales)) {
            return ChineseUtils.simpToTrad(str, glossaryMapping);
        } else if ("en_US".equalsIgnoreCase(locales) || "en".equalsIgnoreCase(locales)) {
            return str + "EN";
        } else {
            return str + locales.toUpperCase();
        }

    }
}
