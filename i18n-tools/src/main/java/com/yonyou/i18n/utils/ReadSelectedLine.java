/**
 *
 */
package com.yonyou.i18n.utils;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.apache.log4j.Logger;


/**
 * 处理在文件的中文资源替换时的精确替换
 * <p>
 * TODO
 *
 * @author wenfan
 */
public class ReadSelectedLine {

    private static Logger logger = Logger.getLogger(ReadSelectedLine.class);

    /**
     * 读取文件指定行。
     */
    public static void main(String[] args) throws IOException {
        // 指定读取的行号 
        int lineNumber = 126;
        // 读取文件 
        File sourceFile = new
                File("D:\\workspace\\iuap_poc\\zhongxing-poc\\iuap_pap_quickstart\\src\\main\\java\\com\\yonyou\\iuap\\example\\orders\\web\\controller\\OrdersController.java");
        // 读取指定的行 
        readAppointedLineNumber(sourceFile, lineNumber);
        // 获取文件的内容的总行数 
        System.out.println(getTotalLines(sourceFile));
    }

    // 读取文件指定行。 
    static void readAppointedLineNumber(File sourceFile, int lineNumber) throws IOException {
        FileReader in = new FileReader(sourceFile);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        reader.setLineNumber(126);
        if (lineNumber < 0 || lineNumber > getTotalLines(sourceFile)) {
            logger.info("不在文件的行数范围之内。");
        }

        while (s != null) {
            logger.info("当前行号为:" + reader.getLineNumber());
            s = reader.readLine();
            logger.info(s);

        }
        reader.close();
        in.close();
    }

    // 文件内容的总行数。
    static int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        String s = reader.readLine();
        int lines = 0;
        while (s != null) {
            lines++;
            s = reader.readLine();
        }
        reader.close();
        in.close();
        return lines;
    }
}