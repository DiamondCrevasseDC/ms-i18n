/**
 *
 */
package com.yonyou.i18n.core;

import com.yonyou.i18n.constants.I18nConstants;
import com.yonyou.i18n.model.MLResElement;
import com.yonyou.i18n.model.MLResSubstitution;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;


/**
 * 抽取中文字符并设置需要替换的字符
 *
 * @author wenfa
 */
public class ExtractChar {

    private static Logger logger = Logger.getLogger(ExtractChar.class);

    private String parseProjectPath = ConfigUtils.getPropertyValue("parseProjectPath");

    private String projectType = ConfigUtils.getPropertyValue("projectType");

    private String multiLangType = ConfigUtils.getPropertyValue("multiLangType");
    private String jQueryResourcePostfix = ConfigUtils.getPropertyValue("jQueryResourcePostfix");
    private String reactResourcePostfix = ConfigUtils.getPropertyValue("reactResourcePostfix");

    private String importJavaMessagesStr = ConfigUtils.getPropertyValue("importJavaMessagesStr");
    private String importJSMessagesStr = ConfigUtils.getPropertyValue("importJSMessagesStr");

    private String replaceJavaString = ConfigUtils.getPropertyValue("replaceJavaString");
    private String replaceHTMLString = ConfigUtils.getPropertyValue("replaceHTMLString");

    private String replaceJSString = ConfigUtils.getPropertyValue("replaceJQueryJSString");
    private String replaceJQueryJSString = ConfigUtils.getPropertyValue("replaceJQueryJSString");
    private String replaceReactJSString = ConfigUtils.getPropertyValue("replaceReactJSString");

    // 全局的目录清单
    static HashSet<String> keyPrefixs = new HashSet<String>();

    MatchChar mc = new MatchChar();


    public ExtractChar() {
        init();
    }

    /**
     * init
     */
    private void init() {
        logger.info("print the whole props info:");
        logger.info(ConfigUtils.props);

        if ("React".equalsIgnoreCase(this.projectType)) {

            this.replaceJSString = this.replaceReactJSString;

//            JsonFileUtil jsonFileUtil = new JsonFileUtil();
//
//            jsonFileUtil.init(this.parseProjectPath + File.separator + "locales", ".json");
//            keyPrefixs = jsonFileUtil.getKeyPrefix();

        } else {

            this.replaceJSString = this.replaceJQueryJSString;

//            ResourceFileUtil resourceFileUtil = new ResourceFileUtil();
//
//            resourceFileUtil.init(this.parseProjectPath + File.separator + "locales", ".properties");
//            keyPrefixs = resourceFileUtil.getKeyPrefix();
        }


    }

    /**
     * 执行抽取
     * <p>
     * 该部分可以采用多线程去进行文件的解析工作，主要抽取出需要替换的中文字符串
     * <p>
     * *******java文件*****
     * 同时将抽取出来的中文字符串以目录结构层次以及流水码进行编码写入map中
     * 暂时按照项目统一保存至message.properties文件，之后按需要再进行资源拆分
     * <p>
     * 之后将map写入properties文件
     * <p>
     * ********html文件******
     */
    public void doExtract(List<PageNode> pageNodes) {

        logger.info("--抽取资源入口--扫描文件个数："+pageNodes.size());

        for (PageNode pageNode : pageNodes) {

            // 针对每个独立的文件，进行中文的解析
            if (pageNode.isFile()) {

                logger.info("--扫描文件个数："+pageNodes.size() + "--当前文件名称：" +pageNode.getName());

                writeMap2Node(pageNode);

                doDataWash(pageNode);
            }
        }

    }

    /**
     * 将文件中的获取的中文map信息转换到pageNode对象中
     *
     * @param pageNode
     */
    public void writeMap2Node(PageNode pageNode) {

        File file = pageNode.getFile();

        ArrayList<MLResSubstitution> substitutions = pageNode.getSubstitutions();

        try {
            LinkedHashMap<Integer, LinkedHashMap<Integer, String>> theWholeChineseInfo = null;

            if ("html".equalsIgnoreCase(pageNode.getType())) {
                theWholeChineseInfo = mc.htmlSearch(file);
            } else {
                theWholeChineseInfo = mc.search(file);
            }

            Iterator<Entry<Integer, LinkedHashMap<Integer, String>>> fileInfo = theWholeChineseInfo.entrySet().iterator();

            // 识别文件中存在中文的行，给最后的写入文件使用
            List<Integer> existChiLine = new ArrayList<Integer>();
            boolean isLineExistChinese = false;

            // 基于文件的中文信息
            while (fileInfo.hasNext()) {

                pageNode.setExistChinese(true);// 存在中文信息

                Entry<Integer, LinkedHashMap<Integer, String>> theFileInfo = fileInfo.next();

                int i = theFileInfo.getKey().intValue();
                LinkedHashMap<Integer, String> lineInfo = theFileInfo.getValue();

                Iterator<Entry<Integer, String>> theLineInfo = lineInfo.entrySet().iterator();

                isLineExistChinese = false;
                // 基于行的中文信息
                while (theLineInfo.hasNext()) {
                    isLineExistChinese = true;
                    Entry<Integer, String> filedInfo = theLineInfo.next();
                    int j = filedInfo.getKey().intValue();
                    String v = filedInfo.getValue();

                    // 先判断该值在本文件的多语资源清单中是否存在
                    // 如果存在，则找到该资源，并将i、j录入元素对象中
                    // 如果不存在，则新建对象并写入list
                    boolean isExist = false;
                    for (MLResSubstitution substitution : substitutions) {
                        if (substitution.getValue().equals(v)) {
                            isExist = true;

                            MLResElement ele = new MLResElement();
                            ele.setValue(v);
                            ele.setLine(i);
                            ele.setIndex(j);
                            ele.setLength(v.length());
                            substitution.getElements().add(ele);
                        }
                    }
                    if (!isExist) {
                        MLResSubstitution res = new MLResSubstitution();
                        res.setValue(v);
                        MLResElement ele = new MLResElement();
                        ele.setValue(v);
                        ele.setLine(i);
                        ele.setIndex(j);
                        ele.setLength(v.length());
                        res.getElements().add(ele);
                        pageNode.getSubstitutions().add(res);
                    }
                }

                if (isLineExistChinese) {
                    existChiLine.add(i);
                }
            }

            pageNode.setExistChiLine(existChiLine);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 在今天全局的key设置之前需要进行全局的历史key的前缀的识别， 有两种方式：
     * <p>
     * 1、 直接将数据库（统一存储key的地方）中保存的key的前缀解析后set过来
     * 2、 将数据库中key 的前缀追加到该对象中。
     * <p>
     * 并发的情况会有问题
     *
     * @param keyPrefixs
     */
    public void setKeyPrefixs(HashSet<String> keyPrefixs) {

        this.keyPrefixs = keyPrefixs;

    }

    /**
     * 获取key的前缀
     *
     * @param keyPrefixs
     * @param pageNode
     * @return
     */
    public String getKeyPrefix(HashSet<String> keyPrefixs, PageNode pageNode) {


//		String keyPrefix = pageNode.getType().substring(0, 2) +"." + pageNode.getParent().getResModuleName()+"." + pageNode.getResModuleName()+".";
        StringBuffer sb = new StringBuffer();


        sb.append(pageNode.getType().length() >= 2 ? pageNode.getType().substring(0, 2) : pageNode.getType());
        sb.append(".");
        sb.append(pageNode.getParent().getResModuleName().length() >= 3 ? pageNode.getParent().getResModuleName().substring(0, 3) : pageNode.getParent().getResModuleName());
        sb.append(".");
        sb.append(pageNode.getResModuleName().length() >= 3 ? pageNode.getResModuleName().substring(0, 3) : pageNode.getResModuleName());

        if (keyPrefixs.contains(sb.toString())) {
            int size = 0;
            for (String str : keyPrefixs) {
                if (str.startsWith(sb.toString())) {
                    size++;
                }
            }
            sb.append(size);
        }

        // add to list
        keyPrefixs.add(sb.toString());

        return sb.append(".").toString();

    }

    /**
     * 执行数据的清洗操作--针对单个文件及pageNode && 数据已经被归类了
     * <p>
     * 将pageNode中的数据进行完善：
     * 主要是对资源数据的key值进行定义，对不同类型的文件的资源进行替换时需要考虑不同的写入内容
     */
    public void doDataWash(PageNode pageNode) {

        ArrayList<MLResSubstitution> substitutions = pageNode.getSubstitutions();

        // 针对java文件的处理
        if (I18nConstants.FILE_TYPE_JAVA.equals(pageNode.getType())) {

            pageNode.setAddContent(this.importJavaMessagesStr);

            setKeyAndReplaceStr(pageNode, this.replaceJavaString);

        } else if (I18nConstants.FILE_TYPE_HTML.equals(pageNode.getType())) {

//			pageNode.setAddContent(importHTMLMessagesStr);

            setKeyAndReplaceStr(pageNode, this.replaceHTMLString);

        } else if (I18nConstants.FILE_TYPE_JS.equals(pageNode.getType())) {

            pageNode.setAddContent(this.importJSMessagesStr);

            setKeyAndReplaceStr(pageNode, this.replaceJSString);

        }

    }


    /**
     * 清洗资源配置的key及value值，用于后面的资源替换
     *
     * @param pageNode
     * @param replaceString
     */
    private void setKeyAndReplaceStr(PageNode pageNode, String replaceString) {

        int flowNumber = 1;
        String keyPrefix = getKeyPrefix(keyPrefixs, pageNode);
//			String keyPrefix = pageNode.getType() +"." + pageNode.getParent().getResModuleName()+"." + pageNode.getResModuleName()+".";

        for (MLResSubstitution substitution : pageNode.getSubstitutions()) {

            // key：通过父级节点的模块名称+“.”+本模块名称+“.”+流水生成
            String key = keyPrefix + NumberUtils.getSeqNumByLong(new Long(flowNumber), 4);
            flowNumber++;

            substitution.setKey(key);

            // replaceStr
            // $.i18n.prop(\'{0}\')
//				replaceJQueryJSString=$.i18n.prop(\'{0}\', \'{1}\')
//				replaceReactJSString=intl.get(\'{0}\').defaultMessage(\'{1}\')
//                String replaceStr = this.replaceJSString.replace("{0}", key).replace("{1}", );
            substitution.setReplaceStr(replaceString(replaceString, key, StringUtils.getStrByDeleteBoundary(substitution.getValue())));

            // pageNode
            substitution.setPageNode(pageNode);

        }

    }

    /**
     * @param s
     * @param r
     * @return
     */
    private String replaceString(String s, String... r) {
        try {
            return s.replaceAll("\\{0\\}", r[0]).replaceAll("\\{1\\}", r[1]);
        } catch (Exception e) {
            logger.error("replace string : s:" + s + " and p: " + r);

            try {
                return s.replace("{0}", r[0]).replace("{1}", r[1]);
            } catch (Exception e2) {
                logger.error("replace string : s:" + s + " and p: " + r);
            }
        }
        return "";
    }

} 
