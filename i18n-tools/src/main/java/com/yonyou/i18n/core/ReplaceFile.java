package com.yonyou.i18n.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yonyou.i18n.constants.I18nConstants;
import org.apache.log4j.Logger;

import com.yonyou.i18n.model.MLResElement;
import com.yonyou.i18n.model.MLResSubstitution;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.ConfigUtils;
import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.StringUtils;
import com.yonyou.i18n.utils.TextUtils;

/**
 * 根据不同的文件进行中文的替换
 *
 * @author wenfa
 */
public class ReplaceFile {

    private static Logger logger = Logger.getLogger(ReplaceFile.class);


    private String projectType = ConfigUtils.getPropertyValue("projectType");

    private String extraHandleJSFile = ConfigUtils.getPropertyValue("extraHandleJSFile");

    // 本应该在读取并 解析文件时添加的信息，目前直接添加
    private String importJSMessagesStr = ConfigUtils.getPropertyValue("importJSMessagesStr");
    private String importModelJSMessagesStr = ConfigUtils.getPropertyValue("importModelJSMessagesStr");
    private String replaceJSAPIString = ConfigUtils.getPropertyValue("replaceJSAPIString");

    public ReplaceFile() {

    }


    /**
     * 直接采用替换 的方式替换，不用读取行，采用String的方式
     * 20180717 update
     *
     * @param pageNodes 资源节点
     */
    public void updateFiles(List<PageNode> pageNodes) {

        for (PageNode pageNode : pageNodes) {
            if (pageNode.isExistChinese() || exitExtraHandleFile(pageNode)) {

                updateFile(pageNode);

            }
        }
    }

    /**
     * 处理特殊的js文件
     *
     * @param pageNode 资源节点
     * @return 是否包含该文件
     */
    private boolean exitExtraHandleFile(PageNode pageNode) {

        if (pageNode.getName() != null) { // && pageNode.getType() != null
            return extraHandleJSFile.contains(pageNode.getName()); // + "." + pageNode.getType()
        } else {
            return false;
        }


    }

    /**
     * 直接采用替换的方式替换，不用读取行，采用String的方式
     *
     * @param pageNode 资源节点
     */
    private void updateFile(PageNode pageNode) {

        File file = new File(pageNode.getPath());
        File fileBack = new File(pageNode.getPath() + I18nConstants.FILE_BACKUP_POSTFIX);
        File fileTemp = new File(pageNode.getPath() + I18nConstants.FILE_TEMPORARY_POSTFIX);

        LineNumberReader reader = null;
        BufferedWriter writer = null;
        try {
            // 原始文件编码，确保读写前后的编码一致性
            String encoding = TextUtils.getFileCharset(file);

            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), encoding));

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////按照不同的文件类型进行文件的替换写入----开始///////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            String str = "";

            if (I18nConstants.FILE_TYPE_JAVA.equalsIgnoreCase(pageNode.getType())) {

                str = updateJavaFile(reader, pageNode);

            } else if (I18nConstants.FILE_TYPE_HTML.equalsIgnoreCase(pageNode.getType())) {

                str = updateHtmlFile(reader, pageNode);

            } else if (I18nConstants.FILE_TYPE_JS.equalsIgnoreCase(pageNode.getType())) {

                // 如果是react项目，主要处理js、 java
                if (I18nConstants.REACT_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

                    str = updateReactJSFile(reader, pageNode);

                    // 如果是jQuery项目，主要处理java、html、js
                } else {
                    str = updateJQueryJSFile(reader, pageNode);

                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////按照不同的文件类型进行文件的替换写入----结束///////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////

            // 写成与原文格式一致的文件encoding
            fileBack.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBack), encoding));

            writer.write(str);
            writer.flush();

            reader.close();
            writer.close();
            try {
                file.renameTo(fileTemp);
                fileBack.renameTo(file);
            } catch (Exception e) {
                logger.error("****更改文件名称报错：****" + file.getPath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            closeStream(reader, writer);
        }
    }

    /**
     * 将流输出为字符串
     *
     * @param reader 输入流
     * @return 文件字符串
     * @throws IOException 读写异常
     */
    private String getStreamByReader(LineNumberReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineInfo;

        while ((lineInfo = reader.readLine()) != null) {
            sb.append(lineInfo).append(Helper.getLineDelimiter());
        }
        return sb.toString();
    }

    /**
     * @param reader   输入流
     * @param pageNode 资源信息
     * @return 文件字符串
     * @throws IOException 读写异常
     */
    private String updateJavaFile(LineNumberReader reader, PageNode pageNode) throws IOException {

        String str = getStreamByReader(reader);

        str = updateFileString(str, pageNode.getSubstitutions());

        str = updateFileImport(str, pageNode.getAddContent());

        return str;
    }

    /**
     * @param reader   输入流
     * @param pageNode 资源信息
     * @return 文件字符串
     * @throws IOException 读写异常
     */
    private String updateHtmlFile(LineNumberReader reader, PageNode pageNode) throws IOException {

        String str = getStreamByReader(reader);

        str = updateHTMLFileString(str, pageNode.getSubstitutions());

        return str;

    }


    /**
     * @param reader   输入流
     * @param pageNode 资源信息
     * @return 文件字符串
     * @throws IOException 读写异常
     */
    private String updateJQueryJSFile(LineNumberReader reader, PageNode pageNode) throws IOException {

        String str = getStreamByReader(reader);

        str = updateFileString(str, pageNode.getSubstitutions());

        str = updateFileImport(str, pageNode.getAddContent());

        return str;

    }


    /**
     * 该部分主要用于按照React-Intl国际化框架更新相应的各个类的内容
     * 主要包括model.js、 container.js、index.js等
     *
     * @param reader   输入流
     * @param pageNode 资源信息
     * @return 文件字符串
     * @throws IOException 读写异常
     */
    private String updateReactJSFile(LineNumberReader reader, PageNode pageNode) throws IOException {

        String str;

        if ("container.js".equalsIgnoreCase(pageNode.getName()) || "containers.js".equalsIgnoreCase(pageNode.getName())) {

            str = updateFileForContainerJS(reader);

            str = updateFileImport(str, pageNode.getAddContent());

            str = updateFileImport(str, importJSMessagesStr);


        } else if ("model.js".equalsIgnoreCase(pageNode.getName()) || "models.js".equalsIgnoreCase(pageNode.getName())) {

            str = getStreamByReader(reader);

            str = updateFileString(str, pageNode.getSubstitutions());

            str = updateFileImport(str, pageNode.getAddContent());

            str = updateFileImport(str, importModelJSMessagesStr);

        } else {

            str = getStreamByReader(reader);

            str = updateFileString(str, pageNode.getSubstitutions());

            str = updateFileImport(str, pageNode.getAddContent());

        }

        return str;

    }


    /**
     * 关闭流
     *
     * @param reader 输入流
     * @param writer 写入流
     */
    private void closeStream(LineNumberReader reader, BufferedWriter writer) {

        //关闭文件流对象
        try {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 针对不同类型的文件进行import的插入
     * 主要是定位import位置： 找到第一个import的行，然后写在它前面一行即可
     *
     * @param str       全文本字符串
     * @param importStr 替换的字符串
     * @return 全文本字符串
     */
    private String updateFileImport(String str, String importStr) {

        // 20180814
        // 添加专门针对三一的逻辑处理，其他项目可参考
        // 1、 基本上每个index.js中都包含日期控件，因此都有placeholder属性，
        //     故而需要采用formatMessage的api方式调用，从而导致需要引入injectIntl
        // 2、 在model.js中引入import { FormattedMessage, defineMessages } from 'react-intl';
        //     import React, { Component } from "react"; 同时按照FormattedMessage的组件方式调用
        // 3、在container.js中引入 import { injectIntl, intlShape } from 'react-intl';对所有的组件外包一层injectIntl
        // 4、在index.js中 引入 import { injectIntl, FormattedMessage } from 'react-intl';
        //     对通用的情况采用FormattedMessage的组件方式调用，对日期采用formatMessage的api方式调用

        // BEGIN
        // 先判断是否存在，如果存在则不用写入
        if (str.contains(importStr)) {
            return str;
        }

        // TODO
        // 找到第一个import ，然后写在它前面
        // 如果找不到，java写在package的行后面，js写在第一行
        int i = str.indexOf("import ");
        if (i >= 0) {
            str = str.substring(0, i) + importStr + Helper.getLineDelimiter() + str.substring(i);
        }

        return str;
    }


    /**
     * 针对React国际化的特殊处理
     * <p>
     * 对所有的组件外包一层injectIntl
     *
     * @param reader 全文本字符流
     * @return 解析后的全文本字符串
     */
    private String updateFileForContainerJS(LineNumberReader reader) {

        // 20180814
        // 添加专门针对三一的逻辑处理，其他项目可参考
        // 1、 基本上每个index.js中都包含日期控件，因此都有placeholder属性，
        //     故而需要采用formatMessage的api方式调用，从而导致需要引入injectIntl
        // 3、在container.js中引入 import { injectIntl, intlShape } from 'react-intl';

        // 识别import的组件以及export的组件名称一致
        // 然后将该名称injectIntl，然后在放回去
        StringBuilder sb = new StringBuilder();
        String lineInfo;
        String last;
        String repLast;

        try {
            while ((lineInfo = reader.readLine()) != null) {

                if (lineInfo.startsWith("export const")) {

                    last = StringUtils.extractLastMessage(lineInfo);

                    if (last != null && !"".equals(last)) {

                        if (last.startsWith("(") && last.endsWith(")")) {
                            repLast = "injectIntl" + last;
                        } else {
                            repLast = "injectIntl(" + last + ")";
                        }

                        lineInfo = lineInfo.substring(0, lineInfo.lastIndexOf(last)) + repLast + lineInfo.substring(lineInfo.lastIndexOf(last) + last.length());
                    }
                }
                sb.append(lineInfo).append(Helper.getLineDelimiter());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }


    /**
     * 处理被 {} 包括起来的内容
     *
     * @param str 原始字符串
     * @param rs  字符替换的多语内容
     * @return 将要替换的字符串
     */
    private String getReplaceValueByBrace(String str, MLResSubstitution rs) {

        String replaceValue = rs.getReplaceStr();

        replaceValue = "{" + replaceValue + "}";

        return replaceValue;
    }


    /**
     * 处理被<>***</>包括起来的内容
     *
     * @param str 原始字符串
     * @param rs  字符替换的多语内容
     * @return 将要替换的字符串
     */
    private String getReplaceValueByLTGT(String str, MLResSubstitution rs) {

        String value = rs.getValue();
        String replaceValue = ">" + rs.getReplaceStr() + "<";

        // 针对Label情况做特殊处理
        // 示例： <Label>应用平台</Label>
        // 示例： <Label className='time'>应用平台</Label>
        Matcher matcher;
        String reg4Title = "<Label[\\s\\S]*?>" + (value.substring(0, value.length() - 1)).substring(1) + "</Label>";
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);

            if (matcher.find()) {
                replaceValue = ">{" + rs.getReplaceStr() + "}<";
            }

        } catch (Exception e) {
            logger.info(e);
        }

        // 针对title情况做特殊处理
        // 示例： <title>应用平台</title>
        reg4Title = "<title>" + (value.substring(0, value.length() - 1)).substring(1) + "</title>";
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);

            if (matcher.find()) {
                replaceValue = ">" + rs.getKey() + "<";
            }
        } catch (Exception e) {
            logger.info(e);
        }

        return replaceValue;
    }


    /**
     * 处理被"" '' 包括起来的内容: 主要是基于JS文件的
     * <p>
     * 正常情况下可以直接替换，但是需要处理比较多的特殊情况
     *
     * @param str 原始字符串
     * @param rs  字符替换的多语内容
     * @return 将要替换的字符串
     */
    private String getReplaceValueByQuotation(String str, MLResSubstitution rs) {

        String value = rs.getValue();
        String replaceValue = rs.getReplaceStr();

        // React中的特殊情况
        // 属性值，采用{this.props.intl.formatMessage({id:"js.buy.com2.0047", defaultMessage:"s ~ e"})}值替换
        // replaceJSAPIString
        // placeholder="s~e"
        Matcher matcher;
        String reg4Title = "([^\\s*]+)=" + value;
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);

            while (matcher.find()) {
                String title = matcher.group(1);
                if ("title".equals(title) || "placeholder".equals(title)) {
                    replaceValue = replaceJSAPIString.replace("{0}", rs.getKey()).replace("{1}", StringUtils.getStrByDeleteBoundary(rs.getValue()));
                    replaceValue = "{" + replaceValue + "}";
                    rs.setReplaceStr(replaceValue);
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }

        // placeholder={"s~e"}
        reg4Title = "([^\\s*]+)=\\{" + value + "\\}";
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);

            while (matcher.find()) {
                String title = matcher.group(1);
                if ("title".equals(title) || "placeholder".equals(title)) {
                    replaceValue = replaceJSAPIString.replace("{0}", rs.getKey()).replace("{1}", StringUtils.getStrByDeleteBoundary(rs.getValue()));
                    rs.setReplaceStr(replaceValue);
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }


        // dateInputPlaceholder={["s", "e"]}
        reg4Title = "([^\\s*]+)=\\{\\[[\\s\\S]*?" + value + "[\\s\\S]*?\\]\\}";
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);

            while (matcher.find()) {
                String title = matcher.group(1);
                if ("dateInputPlaceholder".equals(title)) {
                    replaceValue = replaceJSAPIString.replace("{0}", rs.getKey()).replace("{1}", StringUtils.getStrByDeleteBoundary(rs.getValue()));
                    rs.setReplaceStr(replaceValue);
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }


        // TODO 该部分可以追加其他特殊情况
        // TODO 添加处理包含 [] {} 的情况

        return replaceValue;

    }


    /**
     * 处理被"" '' 包括起来的内容: 主要是基于HTML文件的
     * <p>
     * 正常情况下可以直接替换，但是需要处理比较多的特殊情况
     *
     * @param str 原始字符串
     * @param rs  字符替换的多语内容
     * @return 将要替换的字符串
     */
    private String getReplaceValueByHtmlQuotation(String str, MLResSubstitution rs) {

        String value = rs.getValue();
        String replaceValue = rs.getReplaceStr();

        // 20180728 update
        // 处理思路： 目前有引号的基本就是作为某个属性的值而存在，需要判断是否是title属性以及placeholder属性，否则采用替换的方式
        // 针对title的处理，需要判断是否是title，同时对title的内容直接采用key值替换
        // 针对placeholder的处理，判断是placeholder属性，直接采用key值替换
        Matcher matcher;

        // title="按钮编码"
        // 属性值，采用key值替换
        String reg4Title = "([^\\s*]+)=" + value;
        try {
            matcher = Pattern.compile(reg4Title).matcher(str);
            while (matcher.find()) {
                String title = matcher.group(1);
                if ("title".equals(title) || "placeholder".equals(title)) {
                    replaceValue = "\"" + rs.getKey() + "\"";
                    rs.setReplaceStr(replaceValue);
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }

        // "title":"按钮编码"
        // u-meta中定义的数据，此时将所有的\"\'都去掉即可
        // "\"([^\\s*]+)\":"
        String reg4Title1 = "\"([a-zA-Z]+)\":" + value;
        try {
            matcher = Pattern.compile(reg4Title1).matcher(str);
            while (matcher.find()) {
                String title = matcher.group(1);
                if ("title".equals(title) || "placeholder".equals(title)) {
                    replaceValue = "\"" + replaceValue.replaceAll("\"", "") + "\"";
                    rs.setReplaceStr(replaceValue);
                }
            }
        } catch (Exception e) {
            logger.info(e);
        }


        // TODO 该部分可以追加其他特殊情况
        // TODO 添加处理包含 [] {} 的情况

        return replaceValue;

    }


    /**
     * 处理纯文本的情况：先将该部分的内容全量保存起来，然后统一处理
     * <p>
     * 在前面进行解析时，将纯文本的内容使用 $$  包括起来
     *
     * @param rs  字符替换的多语内容
     * @param map 用来保存特殊的纯文本的情况
     */
    private void setReplaceValueByPureTxt(MLResSubstitution rs, Map<String, ArrayList<String>> map) {

        ArrayList<String> als = new ArrayList<>();
        als.add(rs.getKey());
        als.add(rs.getReplaceStr());

        map.put((rs.getValue().substring(0, rs.getValue().length() - 1)).substring(1), als);

    }

    /**
     * 统一处理纯文本的情况：先将该部分的内容全量保存起来，然后统一处理
     *
     * @param str 全文本字符串
     * @param map 用来保存特殊的纯文本的情况
     * @return 全文本字符串
     */
    private String operateStrByPureTxt(String str, Map<String, ArrayList<String>> map) {

        if (map.size() > 0) {

            LinkedHashMap<String, ArrayList<String>> ext = getOrderByKey(map);

            Iterator<Entry<String, ArrayList<String>>> extMap = ext.entrySet().iterator();

            while (extMap.hasNext()) {
                Entry<String, ArrayList<String>> e = extMap.next();

                String k = e.getKey();
                String v = e.getValue().get(1);

                // 针对title情况做特殊处理
                // 示例： <title>应用平台</title>
                String reg4Title2 = "<title>" + e.getKey() + "</title>";
                try {
                    Matcher matcher = Pattern.compile(reg4Title2).matcher(str);

                    if (matcher.find()) {
                        v = e.getValue().get(0);
                    }
                } catch (Exception e1) {
                    logger.info(e1);
                }

                // 多语字符替换
                str = replaceAllString(str, k, v);
            }
        }

        return str;

    }


    /**
     * 针对全文进行中文的替换
     * 不用像以前那样读取行，判断行号了，直接采用String流整体替换后输出
     * <p>
     * 需要依据原始扫描信息而来，因为原始信息做过内容的去重等过滤操作
     *
     * @param str 全文本字符流
     * @param rss 资源信息
     * @return 替换后的字符串
     */
    private String updateFileString(String str, ArrayList<MLResSubstitution> rss) {

        // MLResSubstitution里的内容包含界定符号
        // 整体替换时基于“”、’‘ 的界定可以直接替换，基于{}、<>的界定需要在替换的对象中添加界定符号以保证替换后的完整性。
        // 另外针对html中“”、’‘ 的处理需要更多的判断
        String value;
        String replaceValue;

        Map<String, ArrayList<String>> map = new HashMap<>();// 用来保存特殊的纯文本的情况

        for (MLResSubstitution rs : rss) {

            // 某些情况下全量替换会报错
            value = rs.getValue();
            replaceValue = rs.getReplaceStr();

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////多语字符串的识别处理----开始////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            if (value.startsWith("{")) {

                replaceValue = getReplaceValueByBrace(str, rs);

            } else if (value.startsWith(">")) {

                replaceValue = getReplaceValueByLTGT(str, rs);

            } else if (value.startsWith("\"") || value.startsWith("\'")) {

                replaceValue = getReplaceValueByQuotation(str, rs);

            } else if (value.startsWith("$") && value.endsWith("$")) {

                // 添加对纯文本的解析
                setReplaceValueByPureTxt(rs, map);
            }

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////多语字符串的识别处理----结束////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////

            // 主要的替换方法-start：
            // 进行特定字符的全量替换
            // 由于字符前后都带有界定符号，因此对单个文件基本可以确定唯一性
            str = replaceAllString(str, value, replaceValue);
            // 主要的替换方法-end

        }

        return str;
    }


    /**
     * 针对全文进行中文的替换
     * 不用像以前那样读取行，判断行号了，直接采用String流整体替换后输出
     * <p>
     * 需要依据原始扫描信息而来，因为原始信息做过内容的去重等过滤操作
     * <p>
     * TODO
     * 20180728 update
     * 针对html的内容做的更多的判断以及处理
     * 针对title的处理，需要判断是否是title，同时对title的内容直接采用key值替换
     * 针对placeholder的处理，判断是placeholder属性，直接采用key值替换
     * 处理思路： 目前有引号的基本就是作为某个属性的值而存在，需要判断是否是title属性以及placeholder属性
     * 其次就是作为独立的纯文本存在的属性
     *
     * @param str 全文本字符串
     * @param rss 多语资源
     * @return 全文本字符串
     */
    private String updateHTMLFileString(String str, ArrayList<MLResSubstitution> rss) {


        String value;
        String replaceValue;
        Map<String, ArrayList<String>> map = new HashMap<>();// 用来保存特殊的纯文本的情况

        // MLResSubstitution里的内容包含界定符号
        // 整体替换时基于“”、’‘ 的界定可以直接替换，基于{}、<>的界定需要在替换的对象中添加界定符号以保证替换后的完整性。
        // 另外针对html中“”、’‘ 的处理需要更多的判断
        for (MLResSubstitution rs : rss) {

            // 某些情况下全量替换会报错
            value = rs.getValue();
            replaceValue = rs.getReplaceStr();

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////多语字符串的识别处理----开始////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            if (value.startsWith("{")) {

                replaceValue = getReplaceValueByBrace(str, rs);

            } else if (value.startsWith(">")) {

                replaceValue = getReplaceValueByLTGT(str, rs);

            } else if (value.startsWith("\"") || value.startsWith("\'")) {

                replaceValue = getReplaceValueByHtmlQuotation(str, rs);

            } else if (value.startsWith("$") && value.endsWith("$")) {

                // 添加对纯文本的解析
                setReplaceValueByPureTxt(rs, map);

            }

            ////////////////////////////////////////////////////////////////////////////////////////
            ///////////////////////////////多语字符串的识别处理----结束////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////

            // 主要的替换方法-start：
            // 进行特定字符的全量替换
            // 由于字符前后都带有界定符号，因此对单个文件基本可以确定唯一性
            str = replaceAllString(str, value, replaceValue);
            // 主要的替换方法-end
        }

        // 添加对纯文本的解析
        // 该部分主要是针对html而言，其他类型的文件基本无此问题
        str = operateStrByPureTxt(str, map);

        return str;
    }


    /**
     * @param str          全文本字符串
     * @param value        被替换的字符串
     * @param replaceValue 将要替换的字符串
     * @return 全文本字符串
     */
    private String replaceAllString(String str, String value, String replaceValue) {

        try {
            str = str.replaceAll(value, replaceValue);
        } catch (Exception e) {

            logger.info("error：" + (str.length() > 200 ? "WHOLE-STRING:" + str.substring(0, 200) : str) + "，replace：" + replaceValue);

            try {
                str = str.replace(value, replaceValue);
            } catch (Exception e1) {
                logger.info("error：" + (str.length() > 200 ? "WHOLE-STRING:" + str.substring(0, 200) : str) + "，replace：" + replaceValue);
            }
        }

        return str;
    }

    /**
     * 针对map的按照key值得长度的排序
     *
     * @param map map
     * @return 排序后的map
     */
    @Deprecated
    public LinkedHashMap<String, String> getOrder(Map<String, String> map) {

        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());

        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o2.getKey().length() - o1.getKey().length();
            }
        });

        // 输出
        LinkedHashMap<String, String> newMap = new LinkedHashMap<String, String>();

        for (Map.Entry<String, String> entity : infoIds) {
            newMap.put(entity.getKey(), entity.getValue());
        }

        return newMap;
    }


    /**
     * 针对map的按照key值得长度的排序
     *
     * @param map map
     * @return 排序后的map
     */
    private LinkedHashMap<String, ArrayList<String>> getOrderByKey(Map<String, ArrayList<String>> map) {

        List<Map.Entry<String, ArrayList<String>>> infoIds = new ArrayList<>(map.entrySet());

        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, ArrayList<String>>>() {
            public int compare(Map.Entry<String, ArrayList<String>> o1, Map.Entry<String, ArrayList<String>> o2) {
                return o2.getKey().length() - o1.getKey().length();
            }
        });

        // 输出
        LinkedHashMap<String, ArrayList<String>> newMap = new LinkedHashMap<String, ArrayList<String>>();

        for (Map.Entry<String, ArrayList<String>> entity : infoIds) {
            newMap.put(entity.getKey(), entity.getValue());
        }

        return newMap;
    }


    /**
     * 按照长度进行排序
     * <p>
     * 长的字符串放在最前
     *
     * @param ext ext
     */
    @Deprecated
    public void sortStringArray(List<String> ext) {
        String temp;
        for (int i = 0; i < ext.size(); i++) {
            for (int j = ext.size() - 1; j > i; j--) {
                if (ext.get(i).length() < ext.get(j).length()) {
                    temp = ext.get(i);
                    ext.set(i, ext.get(j));
                    ext.set(j, temp);
                }
            }
        }
    }

    /**
     * 通过行号来精确定位，精确替换
     *
     * @param pageNodes
     */
    @Deprecated
    public void updateFiles1(List<PageNode> pageNodes) {

        for (PageNode pageNode : pageNodes) {

            if (!pageNode.isExistChinese()) continue;

            File file = new File(pageNode.getPath());

            File fileBack = new File(pageNode.getPath() + ".bak");

            File fileTemp = new File(pageNode.getPath() + ".temp");

            FileReader in = null;
            LineNumberReader reader;

            FileWriter fw = null;
            try {
                in = new FileReader(file);

                reader = new LineNumberReader(in);

                fileBack.createNewFile();

                fw = new FileWriter(fileBack);

                String s = reader.readLine();
                int line = reader.getLineNumber();
                while (s != null) {

                    // 针对存在中文的地方进行替换
                    if (pageNode.getExistChiLine().contains(line)) {
                        s = updateLine(s, line, pageNode.getSubstitutions());
                    }

                    fw.write(s);
                    fw.write(Helper.getLineDelimiter());

                    s = reader.readLine();
                    line = reader.getLineNumber();
                }

                //刷新缓冲区
                fw.flush();
                reader.close();
                in.close();
                fw.close();

                try {
                    file.renameTo(fileTemp);
                    fileBack.renameTo(file);
                } catch (Exception e) {
                    logger.error("****更改文件名称报错：****" + file.getPath());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                //关闭文件流对象
                try {
                    if (in != null)
                        in.close();
                    if (fw != null)
                        fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 针对一行进行中文的替换
     * <p>
     * 需要依据原始扫描信息而来，因为原始信息做过内容的去重等过滤操作
     *
     * @param str
     * @param line
     * @param rss
     * @return
     */
    @Deprecated
    public String updateLine(String str, int line, ArrayList<MLResSubstitution> rss) {


        for (MLResSubstitution rs : rss) {

            ArrayList<MLResElement> eles = rs.getElements();

            // 存在即替换
            boolean isExist = false;
            for (MLResElement ele : eles) {

                if (ele.getLine() == line) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                // 某些情况下全量替换会报错
                try {
                    str = str.replaceAll(rs.getValue(), rs.getReplaceStr());
                } catch (Exception e) {
//					logger.error("替换报错：" + str + "，识别：" + rs.getValue());

                    logger.info("error：" + (str.length() > 200 ? "WHOLE-STRING:" + str.substring(0, 200) : str) + "，replace：" + rs.getValue());

                    str = str.replace(rs.getValue(), rs.getReplaceStr());
                }
            }
        }

        return str;
    }

    /**
     * 写入固定的依赖行数据
     * <p>
     * 1、 需要定位出具体在哪儿进行写入--需要具体的条件
     * 2、 写入的依赖数据
     */
    @Deprecated
    public void writeDepend() {

        // 该部分采用标准的字符匹配，而且带上前后的界定符号，因此不必定位了。
    }


    /**
     * 定位出具体在哪儿进行写入--需要具体的条件
     */
    @Deprecated
    public void locationDepend() {

    }

    @Deprecated
    public String replaceContentByPartten(String content, String partten, String target) {
        Matcher matcher = Pattern.compile(partten, Pattern.CASE_INSENSITIVE).matcher(content);
        if (matcher.find()) {
            StringBuffer temp = new StringBuffer();
            matcher.appendReplacement(temp, target);
            matcher.appendTail(temp);
            return temp.toString();
        } else {
            return content;
        }
    }

    public static void main(String[] args) {

    }

}
