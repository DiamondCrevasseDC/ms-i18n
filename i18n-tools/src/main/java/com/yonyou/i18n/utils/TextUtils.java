package com.yonyou.i18n.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * 通过类库获取文件的编码格式
 * <p>
 * 文件编码格式处理类，配合ISNConvert以及chardet进行处理
 *
 * @author wenfan
 */
public class TextUtils {

    private static Logger logger = Logger.getLogger(TextUtils.class);

    private static String defaultFileEncoding = ConfigUtils.getPropertyValue("defaultFileEncoding");

    /**
     * @param filePath
     * @return
     * @Description: 获取文件的字符编码格式
     */
    public static String getFileCharset(String filePath) {

        File file = new File(filePath);
        if (file.isFile() && file.exists()) { // 判断文件是否存在
            return getFileCharset(file);
        } else {
            logger.error(LogUtils.printMessage("找不到指定的文件" + filePath));

            return defaultFileEncoding;
        }


    }


    /**
     * @param file
     * @return
     * @Description: 获取文件的字符编码格式
     */
    public static String getFileCharset(File file) {

        String encoding = defaultFileEncoding;
        BufferedInputStream imp = null;

        // 设置字符探测器
        // 如果探测到字符集编码，则返回该编码值
        nsICharsetDetectionObserver cdo = new Observer() {
        };

        /**
         * 初始化nsDetector() 1. Japanese 2. Chinese 3. Simplified Chinese 4.
         * Traditional Chinese 5. Korean 6. Dont know (默认)
         */
        try {
            nsDetector det = new nsDetector(3);
            det.Init(cdo);

            imp = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[1024];
            boolean done = false; // 是否已经确定某种字符集
            boolean isAscii = true;// 假定当前的串是ASCII编码
            int len = -1;
            boolean found = false;

            while ((len = imp.read(buf, 0, buf.length)) != -1) {
                // 检查是不是全是ascii字符，当有一个字符不是ASC编码时，则所有的数据即不是ASCII编码了。
                if (isAscii)
                    isAscii = det.isAscii(buf, len);
                // 如果不是ascii字符，则调用DoIt方法.
                if (!isAscii && !done)
                    done = det.DoIt(buf, len, false);// 如果不是ASCII，又还没确定编码集，则继续检测。
            }
            det.DataEnd();// 最后要调用此方法，Notify被调用

            encoding = cdo.toString();

            if (isAscii) {
                encoding = "ASCII";
                found = true;
            }
            if ((encoding == null || "".equals(encoding)) && !found) {
                // 如果没找到，则找到最可能的那些字符集
                String prob[] = det.getProbableCharsets();
                if (prob.length > 0) {
                    encoding = prob[0];
                }
            }

            // SET THE DEFAULT FARSET
            if (encoding == null || "".equals(encoding)) {
                encoding = defaultFileEncoding;
            }

            imp.close();
            imp = null;
        } catch (Exception e) {
            try {
                if (imp != null) {
                    imp.close();
                }
            } catch (Exception e1) {
            }
        }

        return encoding;

    }


    /**
     * @param args
     * @Description:
     */
    public static void main(String[] args) {

        String filePath = "D:\\test.txt";

        System.out.println("ENCODING = " + getFileCharset(filePath));

    }
}
