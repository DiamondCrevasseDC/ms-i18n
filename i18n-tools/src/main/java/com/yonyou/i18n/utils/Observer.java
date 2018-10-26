package com.yonyou.i18n.utils;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * 字符编码工具类
 * <p>
 * 自动识别文件的编码格式，目前适配主流的编码类型，主要使用chardet进行识别
 *
 * @author wenfan
 */
public class Observer implements nsICharsetDetectionObserver {

    String charset = "";

    /**
     * 该部分在nsDetector中被调用，用于输出字符编码
     */
    public void Notify(String charset) {
        this.charset = charset;
    }

    /**
     * 通过该方法获取字符编码
     */
    public String toString() {
        return charset;
    }

}
