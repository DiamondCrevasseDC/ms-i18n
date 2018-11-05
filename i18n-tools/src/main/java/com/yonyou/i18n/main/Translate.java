package com.yonyou.i18n.main;

import com.yonyou.i18n.core.ResourcesFile;
import com.yonyou.i18n.core.ScanAllFiles;
import com.yonyou.i18n.model.PageNode;
import com.yonyou.i18n.utils.ConfigUtils;
import com.yonyou.i18n.utils.StringUtils;
import com.yonyou.i18n.utils.ZipUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * 针对目前已经存在的中文简体资源文件，将之翻译为繁体，并保存到相应的目录
 *
 * @author wenfan
 */
public class Translate {


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
     * 写入资源文件
     */
    public void resource() throws Exception {

        try {
            ResourcesFile rf = new ResourcesFile();

            rf.init("zh_TW");

            // 分目录写入资源文件
            rf.translateResourceFileByDirectory(pageNodes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

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
        String zipFile = path + ".zip";

        try {

            Translate sb = new Translate();

            sb.init(path, "properties", "properties");

            sb.resource();

            ZipUtils.zip(new File(zipFile), path);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
