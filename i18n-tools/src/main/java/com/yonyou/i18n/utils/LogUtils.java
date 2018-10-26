package com.yonyou.i18n.utils;

import java.util.Date;

/**
 * 打印日志信息
 *
 * @author wenfan
 */
public class LogUtils {

    public static String n = "\n";

    public static String getException(Exception e) {

        if (e == null) {
            return "异常为空";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(new Date());
        sb.append(" 异常描述:" + n);
        sb.append(e.toString() + " at " + n);
        StackTraceElement[] st = e.getStackTrace();
        if (st != null) {
            for (int i = 0; i < st.length; i++) {
                sb.append(st[i]);
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String getException(String msg, Exception e) {

        if (e == null) {
            return "异常为空";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(new Date());
        sb.append(" 异常描述:" + n);
        sb.append(" 业务消息:" + msg + n);
        sb.append(e.toString() + " at " + n);
        StackTraceElement[] st = e.getStackTrace();
        if (st != null) {
            for (int i = 0; i < st.length; i++) {
                sb.append(st[i]);
                sb.append(n);
            }
        }
        return sb.toString();
    }

    public static String printMessage(String msg) {

        StringBuilder sb = new StringBuilder();
        sb.append("---");
        sb.append(msg);
        sb.append("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        int printlength = Integer.valueOf(ConfigUtils.getPropertyValue("logPrintLength"));
        if (printlength < 0 || printlength > 200) {
            printlength = 150;
        }
        return sb.toString().substring(0, printlength);
    }

}
