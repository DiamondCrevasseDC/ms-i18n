package com.yonyou.i18n.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * 字符串处理工具类
 *
 * @author wenfan
 */
public class StringUtils {

    private static Logger logger = Logger.getLogger(StringUtils.class);

    public static void main(String[] args) {


    }

    /**
     * 提取最后一个括号中内容，忽略括号中的括号
     *
     * @param msg
     * @return
     */
    public static String extractLastMessage(String msg) {
        String last = "";
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '(') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == ')') {
                endFlag++;
                if (endFlag == startFlag) {
                    last = msg.substring(start + 1, i);
                }
            }
        }
        return last;
    }

    /**
     * 提取括号中内容，忽略括号中的括号
     *
     * @param msg
     * @return
     */
    public static List<String> extractMessage(String msg) {
        List<String> list = new ArrayList<String>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '(') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == ')') {
                endFlag++;
                if (endFlag == startFlag) {
                    list.add(msg.substring(start + 1, i));
                }
            }
        }
        return list;
    }


    /**
     * 解析最后生成的文件：properties、json格式等
     *
     * @param resourcePrefix
     * @param multiLangType
     * @return
     */
    public static Map<String, String> getResourceFileList(String resourcePrefix, String multiLangType, String resourcePostfix) {

        Map<String, String> fileMap = new HashMap<String, String>();

        if (multiLangType != null && !"".equals(multiLangType)) {

            String[] mlrts = multiLangType.split(",");

            for (String mlrt : mlrts) {
                if (mlrt != null && !"".equals(mlrt.trim())) {

                    fileMap.put(mlrt, resourcePrefix + "_" + mlrt + "." + resourcePostfix);

                }
            }
        }

        return fileMap;
    }


    /**
     * 解析最后生成的文件：properties、json格式等
     *
     * @param resourcePrefix
     * @param testMultiLangResourceType
     * @return
     */
    public static Map<String, String> getResourceFileList(String resourcePrefix, String testMultiLangResourceType) {

        Map<String, String> fileMap = new HashMap<String, String>();

        if (testMultiLangResourceType != null && !"".equals(testMultiLangResourceType)) {

            String[] mlrts = testMultiLangResourceType.split(",");

            for (String mlrt : mlrts) {
                if (mlrt != null && !"".equals(mlrt.trim())) {
                    String[] lts = mlrt.split("\\|");

                    if (lts.length == 2) {
                        fileMap.put(lts[0], resourcePrefix + "_" + lts[0] + "." + lts[1]);
                    } else if (lts.length == 1) {
                        if ("properties".equals(lts[0]) || "json".equals(lts[0])) {
                            fileMap.put("zh", resourcePrefix + "." + lts[0]);
                        }
                    }
                }
            }
        }

        return fileMap;
    }

    /**
     * 删除前后的边界符号以及多余的空格
     *
     * @param v
     * @return
     */
    public static String getStrByDeleteBoundary(String v) {

        if (v.length() <= 2)
            return v;

        v = v.substring(1);
        v = v.substring(0, v.length() - 1);

        return v.trim();
    }

    /**
     * 通过正则判断中文在一行中出现的情况，不管重复与否都需要记录
     *
     * @param lineInfo
     * @param regexMatchString
     * @return
     */
    public static LinkedHashMap<Integer, String> getLineChineseList(String lineInfo, String regexMatchString) {

        LinkedHashMap<Integer, String> chinese = new LinkedHashMap<Integer, String>();

        Pattern pattern = Pattern.compile(regexMatchString);
        Matcher matcher = pattern.matcher(lineInfo);

        while (matcher.find()) {
//			System.out.println(lineInfo.substring(matcher.start(), matcher.start()+matcher.group().length()));
            logger.info("--colume:--" + matcher.start() + "--name:--" + matcher.group(0));
            chinese.put(new Integer(matcher.start()), matcher.group(0));
        }

        return chinese;

    }


    /**
     * 中文正则后的精确匹配处理，特别考虑一行中重复出现的中文描述
     * 示例：sb.append(“测试“）
     * 示例：sb.append(“--测试：下载Excel失败！--”).append("test").append('测试').append("匹配").append('测试123').append('测试')
     *
     * @param line    完整行信息
     * @param chinese 中文list   其中size>=1
     * @param info    整合后的最终信息
     * @return
     */
    public static int getFinalLineChineseList(String line, LinkedHashMap<Integer, String> chinese, LinkedHashMap<Integer, String> info) {

        if (chinese.isEmpty()) return 0;
        // 具体的处理逻辑是：
        // 如果只有一个，那作为独立的字符串，
        // 如果多于两个，先把第一个与第二个之间进行判断是否存在界定符，
        // 	如果存在，则第一个为独立字符串，保持，remove（0）再去递归找第二个与第三个，
        // 	如果不存在，则将两者与之间的内容相连后作为第一个字符串，remove（1）再进行比较
        // 因为可能有重复出现的中文，所以必须采用position标记
        if (chinese.size() == 1) {

            Entry<Integer, String> first = chinese.entrySet().iterator().next();

            info.put(first.getKey(), first.getValue());

            chinese.remove(first.getKey());

        } else {

            Iterator<Entry<Integer, String>> iter = chinese.entrySet().iterator();

            Entry<Integer, String> first = iter.next();
            Entry<Integer, String> second = iter.next();

            String middle = line.substring(first.getKey() + first.getValue().length(), second.getKey());

            // 如果包含界定符号，则说明这两个是独立的字符串
            // 将原map第一个删除，添加到最终的map
            // 20180717 update for html
            if (middle != null && (middle.contains("\'") || middle.contains("\"") || middle.contains("<") || middle.contains(">"))) {
                info.put(first.getKey(), first.getValue());
                chinese.remove(first.getKey());
            } else {
                // 如果不包含，说明这两个是一个字符串的部分，需要完全合并
                first.getKey();
                String v = line.substring(first.getKey(), second.getKey() + second.getValue().length());

                first.setValue(v);
                chinese.remove(second.getKey());
            }
        }

        getFinalLineChineseList(line, chinese, info);

        return 0;

    }


    /**
     * 对抽取的中文字符串的最后解析：
     * 主要是因为这些字符串的前后可能存在其他的非中文字符的字符与之组成一个完整的语句
     * <p>
     * 处理逻辑：字符串前后的“”为界定
     * <p>
     * TODO
     * 目前主要针对双引号、单引号做了识别，但是从显示的数据来看，大括号也包含。
     */
    public static LinkedHashMap<Integer, String> finalParse(String line, LinkedHashMap<Integer, String> info) {

        LinkedHashMap<Integer, String> lineChineseInfo = new LinkedHashMap<Integer, String>();

        Iterator<Entry<Integer, String>> iter = info.entrySet().iterator();

        String bigBrackets = "";
        String cuspBrackets = "";
        String doubleQuotation = "";
        String singleQuotation = "";
        while (iter.hasNext()) {
            Entry<Integer, String> first = iter.next();

            int key = first.getKey().intValue();
            String value = first.getValue();

            String front = line.substring(0, key);
            String back = line.substring(key + value.length());

            // 该部分的界定符号必须重复出现
            // 界定符号包括“” ’‘
            // 20170717 update 添加对html中<>的界定，如果<div>中文</div>的情况
            // update 界定符号成对出现 {},><,"",''
            List<String> results = new ArrayList<String>();
            if (front.indexOf("{") >= 0 && back.indexOf("}") >= 0) {
                bigBrackets = line.substring(front.lastIndexOf("{"), key + value.length() + back.indexOf("}") + 1);
                if (bigBrackets != null && !"".equals(bigBrackets)) results.add(bigBrackets);
            }
            if (front.indexOf(">") >= 0 && back.indexOf("<") >= 0) {
                cuspBrackets = line.substring(front.lastIndexOf(">"), key + value.length() + back.indexOf("<") + 1);
                if (cuspBrackets != null && !"".equals(cuspBrackets)) results.add(cuspBrackets);
            }
            if (front.indexOf("\"") >= 0 && back.indexOf("\"") >= 0) {
                doubleQuotation = line.substring(front.lastIndexOf("\""), key + value.length() + back.indexOf("\"") + 1);
                if (doubleQuotation != null && !"".equals(doubleQuotation)) results.add(doubleQuotation);
            }
            if (front.indexOf("\'") >= 0 && back.indexOf("\'") >= 0) {
                singleQuotation = line.substring(front.lastIndexOf("\'"), key + value.length() + back.indexOf("\'") + 1);
                if (singleQuotation != null && !"".equals(singleQuotation)) results.add(singleQuotation);
            }

            // 将几个值中为空的去除掉，然后找出长度最小的那个
            if (results.isEmpty()) {
                logger.info("无法定位该值：" + value);

                // 基本可以确定该情况下的中文为独立的文本，需要放到最后进行替换，
                // 而且需要按照字符长度进行排序替换，防止字符包含关系时出现替换混乱的情况
                // 添加$界定标识

                lineChineseInfo.put(new Integer(line.indexOf(value)), "$" + value + "$");

                continue;
            }
            String w = results.get(0);
            for (String r : results) {
                if (r.length() < w.length()) w = r;
            }
//			if(doubleQuotation.equals("")){
//				w = singleQuotation;
//			}else{
//				if(singleQuotation.equals("")){
//					w = doubleQuotation;
//				}else{
//					w = doubleQuotation.length() < singleQuotation.length() ? doubleQuotation : singleQuotation;
//				}
//			}

            lineChineseInfo.put(new Integer(line.substring(0, (line.length() < key + w.length() + 1 ? line.length() : key + w.length() + 1)).lastIndexOf(w)), w);
        }

        return lineChineseInfo;


    }

    /**
     * 中文正则后的精确匹配处理，特别考虑一行中重复出现的中文描述
     * 示例：sb.append(“测试“）
     * 示例：sb.append(“--测试：下载Excel失败！--”).append("test").append('测试').append("匹配").append('测试123').append('测试')
     *
     * @param line    完整行信息
     * @param chinese 中文list   其中size>=1
     * @param info    整合后的最终信息
     * @return
     */
    @Deprecated
    public static int getSubNum(String line, List<String> chinese, List<String> info) {

        // 具体的处理逻辑是：
        // 如果只有一个，那作为独立的字符串，
        // 如果多于两个，先把第一个与第二个之间进行判断是否存在界定符，
        // 	如果存在，则第一个为独立字符串，保持，remove（0）再去递归找第二个与第三个，
        // 	如果不存在，则将两者与之间的内容相连后作为第一个字符串，remove（1）再进行比较
        // 因为可能有重复出现的中文，所以必须采用position标记
        if (chinese.size() == 1) {
            // 判断是否存在界定符号，要前后成对出现并截取
            String front = line.substring(0, line.indexOf(chinese.get(0)));
            String back = line.substring(line.indexOf(chinese.get(0)) + chinese.get(0).length() + 1);
            if (front.contains("\"") && back.contains("\"")) {
// TODO
//				info.add(line.substring(front.lastIndexOf("\""),line.));

            }
        } else {


        }

        return 0;

    }


    /**
     * 字符串b在a中出现的次数
     *
     * @param a
     * @param b
     * @return
     */
    public static int getSubNum(String a, String b) {
        int num = 0;
        String str = a;
        int index = a.indexOf(b);
        while (index != -1) {
            num++;
            str = str.substring(index + b.length() - 1);
            index = str.indexOf(b);
        }
        return num;
    }


}
