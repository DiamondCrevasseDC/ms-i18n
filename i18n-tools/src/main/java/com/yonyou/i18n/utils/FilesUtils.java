package com.yonyou.i18n.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.yonyou.i18n.model.PageNode;

/**
 * 扫描文件目录下的所有适当类型的文件
 *
 * @author wenfan
 */
public class FilesUtils {

    private static Logger logger = Logger.getLogger(FilesUtils.class);


    public static List<PageNode> dirFileList(String dirPath, String scanFileType) throws Exception {

        File dirFile = new File(dirPath);
        File[] fileList = dirFile.listFiles();

        if (fileList == null || fileList.length == 0) {
            logger.error(LogUtils.printMessage("扫描文件失败，文件路径为：" + dirPath));
            throw new Exception("扫描文件失败!");
        }

//		String[] scanFileTypes = scanFileType.split(",");

        if (fileList == null || fileList.length == 0) {
            logger.error(LogUtils.printMessage("扫描文件失败，文件路径为：" + dirPath));
            throw new Exception("扫描文件失败!");
        }

        List<PageNode> pageNodes = new ArrayList<PageNode>();

        for (int i = 0; i < fileList.length; i++) {


            String fileName = fileList[i].getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (scanFileType.indexOf(suffix) >= 0) {

            }
        }
        return pageNodes;
    }


    public static void fileWriter(String fileName, TreeSet<String> clist) throws IOException {
        //创建一个FileWriter对象
        FileWriter fw = new FileWriter(fileName);
        //遍历clist集合写入到fileName中
        for (String str : clist) {
            fw.write(str);
            fw.write("\n");
        }
        //刷新缓冲区
        fw.flush();
        //关闭文件流对象
        fw.close();
    }

    public static TreeSet<String> readFileByLines(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        //创建一个集合
        TreeSet<String> nums = new TreeSet<String>();
        //按行读取文件内容，并存放到集合
        while ((tempString = reader.readLine()) != null) {
            nums.add(tempString);
        }
        reader.close();
        //返回集合变量
        return nums;
    }

    public static void moveFolder(String src, String dest) {
        File srcFolder = new File(src);
        File destFolder = new File(dest);
        File newFile = new File(destFolder.getAbsoluteFile() + File.separator + srcFolder.getName());
        srcFolder.renameTo(newFile);
    }

    public static void main(String[] args) {

    }


}