/**
 *
 */
package com.yonyou.i18n.main;

import com.yonyou.i18n.core.ExtractChar;
import com.yonyou.i18n.core.ReplaceFile;
import com.yonyou.i18n.core.ResourcesFile;
import com.yonyou.i18n.core.ScanAllFiles;
import com.yonyou.i18n.model.MLResSubstitution;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 整体的实现步骤是：
 * <p>
 * 1、 对文件中的中文进行抽取
 * 2、 对中文进行替换
 * 3、 同时需要往文件中写入固定的依赖行
 *
 * @author wenfa
 */
public class StepBy {

    private static Logger logger = Logger.getLogger(StepBy.class);

    // 所有的数据都是通过该对象进行传递的
    private List<PageNode> pageNodes = null;


    /**
     * 初始化项目目录
     * <p>
     * 加载所有文件
     */
    public void init(String path) throws Exception {

        try {
            this.pageNodes = (new ScanAllFiles(path)).loadNodes();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 初始化项目目录
     * <p>
     * 加载所有文件
     */
    public void init(String path, String projectType) throws Exception {

        try {
            this.pageNodes = (new ScanAllFiles(path, projectType)).loadNodes();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 通过字符集范围进行抽取
     *
     * @param
     */
    public void extract() throws Exception {

        try {
            new ExtractChar().doExtract(this.pageNodes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 写入资源文件
     */
    public void resource() throws Exception {

        try {
            ResourcesFile rf = new ResourcesFile();

            // 写入整体资源文件
            rf.writeResourceFile(this.pageNodes);

            // 分目录写入资源文件
            rf.writeResourceFileByDirectory(this.pageNodes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 直接替换
     *
     * @param
     */
    public void replace() throws Exception {

        try {
            new ReplaceFile().updateFiles(this.pageNodes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }


    /**
     * 获取写入的语种类别
     *
     * @return
     */
    @Deprecated
    public Map<String, String> getMlrts1() {

        return StringUtils.getResourceFileList(ConfigUtils.getPropertyValue("resourcePrefix"), ConfigUtils.getPropertyValue("testMultiLangResourceType"));
    }


    /**
     * 获取写入的语种类别
     *
     * @return multi lang resource type
     */
    public List<String> getMlrts() {
        return StringUtils.getResourceFileList(ConfigUtils.getPropertyValue("multiLangType"));
    }


    /**
     * 获取运行时的抽取中文信息
     *
     * @return
     */
    public List<PageNode> getPageNodes() {
        return this.pageNodes;
    }

    /**
     * 获取运行时的抽取中文信息
     *
     * @return
     */
    public Properties getPageNodesProperties() {

        Properties prop = new Properties();

        // 设置属性值
        for (PageNode pageNode : this.pageNodes) {
            ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();

            for (MLResSubstitution rs : rss) {
                // 在写入资源文件时，去掉前后的界定符号
                String v = rs.getValue();
                if (v.length() <= 2) continue;

                prop.setProperty(rs.getKey(), Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)));
            }
        }

        return prop;
    }

    /**
     * 获取运行时的抽取中文信息（根据语种的选择信息）
     *
     * @param locales
     * @return
     */
    public Properties getPageNodesProperties(String locales) {

        Properties prop = new Properties();

        // 设置属性值
        for (PageNode pageNode : this.pageNodes) {
            ArrayList<MLResSubstitution> rss = pageNode.getSubstitutions();

            for (MLResSubstitution rs : rss) {
                // 在写入资源文件时，去掉前后的界定符号
                String v = rs.getValue();
                if (v.length() <= 2) continue;

                prop.setProperty(rs.getKey(), TranslateUtils.transByLocales(Helper.unwindEscapeChars(StringUtils.getStrByDeleteBoundary(v)), locales));
            }
        }

        return prop;
    }


    public static void main(String[] args) {


//        logger.info("识别文件：" + sourcePath);
//        String path = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_" + System.currentTimeMillis();
//        String zipFile = path + ".zip";
//        path = path + "/";
//        logger.info("解压缩路径：" + path);

        String sourcePath = "/Users/yanyong/temp/iuap-pap-baseservice-develop/java.zip";
        String path = "/Users/yanyong/temp/iuap-pap-baseservice-develop";// + "_" + System.currentTimeMillis();

        path = "/Users/yanyong/Downloads/print";
        String zipFile = path + ".zip";

        try {
//            ZipUtils.unZipForFilePath(sourcePath, path);
//        logger.info("执行完成后压缩路径：" + zipFile);

            StepBy sb = new StepBy();

            sb.init(path, "Jquery");

            sb.extract();

            sb.resource();

            sb.replace();

            ZipUtils.zip(new File(zipFile), path);

//		ZipUtils.zip(new File(zipFile), path);

        } catch (Exception e) {
            e.printStackTrace();
        }

//		logger.info("执行完成后压缩路径：" + zipFile);
    }

}
