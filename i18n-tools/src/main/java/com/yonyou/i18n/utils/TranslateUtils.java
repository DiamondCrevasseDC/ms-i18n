package com.yonyou.i18n.utils;

import net.sf.chineseutils.ChineseUtils;

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
