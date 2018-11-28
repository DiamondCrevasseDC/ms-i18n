package com.yonyou.i18n.main;

import com.yonyou.i18n.core.ScanAllFiles;
import com.yonyou.i18n.model.OrderedProperties;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.ConfigUtils;
import com.yonyou.i18n.utils.JsonFileUtil;
import com.yonyou.i18n.utils.ResourceFileUtil;
import com.yonyou.i18n.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

/**
 * 根据上传的设计properties、json文件进行解析，生成java property对象并保持数据库
 *
 * @author wenfan
 */
public class TranslateEnglish {


    private static Logger logger = Logger.getLogger(StepBy.class);

    // 所有的数据都是通过该对象进行传递的
    private List<PageNode> pageNodes = null;


    /**
     * 初始化项目目录
     * <p>
     * 加载所有文件
     */
    public void init(String path, String projectType, String scanFileType) throws Exception {

        try {
            this.pageNodes = (new ScanAllFiles(path, projectType, scanFileType)).loadNodes();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 针对不同的文件类型，从资源文件中获取资源，并归置到property对象中
     * <p>
     * 针对properties资源，采用resourceFileUtil进行解析
     * <p>
     * 针对json资源，采用jsonFileUtil进行解析
     * <p>
     * 解析的过程中，如果存在语料库的信息则写入语料库的信息，如果没有则不写。
     */
    public Properties getOrderedProperties(String path) throws Exception {

        OrderedProperties op = new OrderedProperties();

        try {

            // 设置属性值
            // resourcefileutil
            ResourceFileUtil resourceFileUtil = new ResourceFileUtil();
            resourceFileUtil.init(path, "zh_CN.properties");

            op.add(resourceFileUtil.getPropsFromFiles());

            // jsonfileutil
            JsonFileUtil jsonFileUtil = new JsonFileUtil();
            jsonFileUtil.init(path, "zh_CN.json");
            op.add(jsonFileUtil.getPropsFromFiles());

        } catch (Exception e) {

            logger.info(e);

            throw e;

        }

        return op;

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


    public static void main(String[] args) {

        String path = "/Users/yanyong/Downloads/prop";
//        String zipFile = path + ".zip";

        try {


            TranslateEnglish sb = new TranslateEnglish();

            sb.init(path, "English", "properties,json");

            Properties properties = sb.getOrderedProperties(path);

            System.out.println(properties);

//            sb.init(path, "English", "properties,json");

//            sb.resource();

//            ZipUtils.zip(new File(zipFile), path);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
