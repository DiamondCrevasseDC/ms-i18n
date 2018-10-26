package com.yonyou.i18n.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yonyou.i18n.constants.I18nConstants;
import com.yonyou.i18n.model.MLResSubstitution;
import com.yonyou.i18n.model.OrderedProperties;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.ConfigUtils;
import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.StringUtils;
import com.yonyou.i18n.utils.TranslateUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 读取资源文件&&写入资源文件
 * <p>
 * 资源文件在 WEB-INF/i18n 目录下
 *
 * @author wenfa
 */
public class ResourcesFile {

    private static Logger logger = Logger.getLogger(ResourcesFile.class);

    private String parseProjectPath = ConfigUtils.getPropertyValue("parseProjectPath");

    private String projectType = ConfigUtils.getPropertyValue("projectType");

    private String resourcePrefix = ConfigUtils.getPropertyValue("resourcePrefix");

    private String multiLangType = ConfigUtils.getPropertyValue("multiLangType");

    private String jQueryResourcePostfix = ConfigUtils.getPropertyValue("jQueryResourcePostfix");
    private String reactResourcePostfix = ConfigUtils.getPropertyValue("reactResourcePostfix");

    private String resourceFileEncoding = ConfigUtils.getPropertyValue("resourceFileEncoding");

    private String resourceDirectory = ConfigUtils.getPropertyValue("resourceDirectory");

    private Map<String, String> mlrtMap = null;


    public ResourcesFile() {

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        // 确定生成的文件类型
        if (I18nConstants.REACT_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

            mlrtMap = StringUtils.getResourceFileList(resourcePrefix, multiLangType, reactResourcePostfix);
        } else {
            mlrtMap = StringUtils.getResourceFileList(resourcePrefix, multiLangType, jQueryResourcePostfix);
        }


    }

    /**
     * 将抽取出来的资源写入整体资源文件中
     *
     * @param pageNodes 资源
     */
    public void writeResourceFile(List<PageNode> pageNodes) {

        logger.info("开始写入整体资源文件！");

        // 读取配置，确定生成类型的文件
        Iterator<Entry<String, String>> mlrts = this.mlrtMap.entrySet().iterator();

        while (mlrts.hasNext()) {

            Entry<String, String> mlrt = mlrts.next();
            String locales = mlrt.getKey();

            File file = new File(parseProjectPath + File.separator + mlrt.getValue());

            logger.info("++++++整体资源文件路径为： " + file.getAbsolutePath());

            // 针对pageNodes生成资源文件
            if (I18nConstants.JQUERY_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

                writePropertiesFile(file, pageNodes, locales);
            } else if (I18nConstants.REACT_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

                writeJsonFile(file, pageNodes, locales);
            }
        }
    }


    /**
     * 将抽取出来的资源写入单体资源文件中（分目录）
     *
     * @param pageNodes 资源
     */
    public void writeResourceFileByDirectory(List<PageNode> pageNodes) {

        logger.info("开始写入项目路径下的单体资源文件！");

        Iterator<Entry<String, String>> mlrts;

        // 将资源文件放到locales目录下
        // 将资源文件放到分层的目录下
        for (PageNode pageNode : pageNodes) {

            if (pageNode.isFile() && pageNode.getSubstitutions().size() > 0) {

                String resourceSubDirect = parseProjectPath + File.separator + resourceDirectory + File.separator + pageNode.getParent().getResModuleName() + File.separator + pageNode.getResModuleName();
                File fileDirect = new File(resourceSubDirect);
                if (!fileDirect.isDirectory()) fileDirect.mkdirs();

                // 按照类型生成文件
                mlrts = this.mlrtMap.entrySet().iterator();

                while (mlrts.hasNext()) {

                    Entry<String, String> mlrt = mlrts.next();
                    String locales = mlrt.getKey();

                    File file = new File(resourceSubDirect + File.separator + mlrt.getValue());

                    logger.info("------单体资源文件路径为： " + file.getAbsolutePath());

                    // 针对pageNode生成资源文件
                    if (I18nConstants.JQUERY_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

                        writeSinglePropertiesFile(file, pageNode, locales);
                    } else if (I18nConstants.REACT_PROJECT_TYPE.equalsIgnoreCase(this.projectType)) {

                        writeSingleJsonFile(file, pageNode, locales);
                    }
                }
            }
        }
    }


    /**
     * 将pageNode的资源以locales 的形式保持至文件中
     *
     * @param file      资源文件
     * @param pageNodes 资源数据来源
     * @param locales   语种信息
     */
    private void writePropertiesFile(File file, List<PageNode> pageNodes, String locales) {

        BufferedWriter output = null;

        try {
            // 为了保证资源的顺序，采用LinkedHashSet存储
            OrderedProperties prop = new OrderedProperties();

            if (file.exists())
                prop.load(new InputStreamReader(new FileInputStream(file), resourceFileEncoding));

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), resourceFileEncoding));

            // 设置属性值
            for (PageNode pageNode : pageNodes) {
                ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();

                for (MLResSubstitution rs : rss) {
                    // 在写入资源文件时，去掉前后的界定符号
                    String v = rs.getValue();
                    if (v.length() <= 2) continue;

                    prop.setProperty(rs.getKey(), TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
                }
            }

            // 保存属性值
            prop.store(output, "create the resource file");

            output.flush();
            output.close();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将pageNode的资源以locales 的形式保持至文件中
     *
     * @param file     资源文件
     * @param pageNode 资源数据来源
     * @param locales  语种信息
     */
    private void writeSinglePropertiesFile(File file, PageNode pageNode, String locales) {

        BufferedWriter output = null;

        try {
            // 为了保证资源的顺序，采用LinkedHashSet存储
            // 该部分需要在output之前进行读取保存
            OrderedProperties props = new OrderedProperties();

            if (file.exists())
                props.load(new InputStreamReader(new FileInputStream(file), resourceFileEncoding));

            // 该部分会将源文件清空，然后再写入
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), resourceFileEncoding));

            ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();

            for (MLResSubstitution rs : rss) {
                // 在写入资源文件时，去掉前后的界定符号
                String v = rs.getValue();
                if (v.length() <= 2) continue;

                props.setProperty(rs.getKey(), TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));

            }

            // 保存属性值
            props.store(output, "create the resource file");

            output.flush();
            output.close();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将pageNode的资源以locales 的形式保持至文件中
     *
     * @param file      资源文件
     * @param pageNodes 资源数据来源
     * @param locales   语种信息
     */
    private void writeJsonFile(File file, List<PageNode> pageNodes, String locales) {

        BufferedWriter output = null;

        try {

            JsonObject object = new JsonObject(); //创建Json格式的数据
            if (file.exists()) {
                object = new JsonParser().parse(new InputStreamReader(new FileInputStream(file), resourceFileEncoding)).getAsJsonObject();
            }

            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), resourceFileEncoding));

            // 设置属性值
            for (PageNode pageNode : pageNodes) {
                ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();
                for (MLResSubstitution rs : rss) {
                    // 在写入资源文件时，去掉前后的界定符号
                    String v = rs.getValue();
                    if (v.length() <= 2) continue;

                    object.addProperty(rs.getKey(), Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)) + locales);
                }
            }

            // 保存属性值
            output.write(object.toString());

            output.flush();
            output.close();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将pageNode的资源以locales 的形式保持至文件中
     *
     * @param file     资源文件
     * @param pageNode 资源数据来源
     * @param locales  语种信息
     */
    private void writeSingleJsonFile(File file, PageNode pageNode, String locales) {

        BufferedWriter output = null;

        try {

            JsonObject object = new JsonObject(); //创建Json格式的数据

            if (file.exists()) {
                object = new JsonParser().parse(new InputStreamReader(new FileInputStream(file), resourceFileEncoding)).getAsJsonObject();
            }

            // 该部分会将源文件清空，然后再写入
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), resourceFileEncoding));

            // 设置属性值
            ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();
            for (MLResSubstitution rs : rss) {
                // 在写入资源文件时，去掉前后的界定符号
                String v = rs.getValue();
                if (v.length() <= 2) continue;

                object.addProperty(rs.getKey(), Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)) + locales);

            }

            // 保存属性值
            output.write(object.toString());

            output.flush();
            output.close();

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {


    }

}


