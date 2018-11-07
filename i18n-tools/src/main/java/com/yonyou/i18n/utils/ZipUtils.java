package com.yonyou.i18n.utils;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 针对压缩及解压缩文件的操作
 *
 * @author wenfan
 */
public class ZipUtils {

    private static Logger log = Logger.getLogger(ZipUtils.class);

    static final int BUFFER = 1024;
    private static String comment = "";

    public ZipUtils() {
    }

    public void setComment(String comment) {
        comment = comment;
    }

    /**
     * 压缩文件
     *
     * @param zipFile   压缩后的文件
     * @param inputFile 待压缩的文件路径
     * @throws Exception
     */
    public static void zip(File zipFile, String inputFile) throws Exception {

        ZipOutputStream out = null;

        try {
            File f = new File(inputFile);
            if (!f.exists()) {
                log.error("压缩文件不存在!" + inputFile);
                throw new Exception("压缩文件不存在!" + inputFile);
            }

            out = new ZipOutputStream(new FileOutputStream(zipFile));

//            out.setEncoding("UTF-8");
            out.setEncoding(System.getProperty("sun.jnu.encoding"));

            zip(out, f, "");

            out.close();
            out = null;
        } catch (Exception e) {
            log.error("压缩文件不存在!" + inputFile, e);
        } finally {
            if (out != null) {
                out.close();
            }

        }

    }

    public static void zipAppendFile(String zipFile, String appFile) throws Exception {
        File zipF = new File(zipFile);
        if (!zipF.exists()) {
            throw new Exception("压缩文件不存在!" + zipFile);
        } else {
            String tempUnzipfile = zipF.getParentFile().getAbsolutePath() + File.separator + "temp" + File.separator;
            File ms = new File(tempUnzipfile);
            if (!ms.exists()) {
                ms.mkdirs();
            }

            unZip(zipFile, tempUnzipfile);
            File appFiles = new File(appFile);
            if (!appFiles.exists()) {
                throw new Exception("要添加压缩文件不存在!" + appFiles.getAbsolutePath());
            } else {
                FilesUtils.moveFolder(appFile, tempUnzipfile);
                zip(zipF, tempUnzipfile);

                try {
                    System.gc();
                } catch (Exception var7) {
                    ;
                }

                FileUtil.delFolder(tempUnzipfile);
            }
        }
    }


    /**
     * 压缩文件
     *
     * @param out  压缩后的文件
     * @param f    待压缩的文件路径
     * @param base
     * @throws Exception
     */
    private static void zip(ZipOutputStream out, File f, String base) throws Exception {
        int i;

        // 文件路径需要遍历
        if (f.isDirectory()) {

            File[] fl = f.listFiles();

            // 第一层目录为空，不用加/
            base = base.length() == 0 ? "" : base + "/";

            out.putNextEntry(new ZipEntry(base));


            for (i = 0; i < fl.length; ++i) {
                zip(out, fl[i], base + fl[i].getName());
            }

        } else {
            out.putNextEntry(new ZipEntry(base));

            FileInputStream in = null;
            try {

                in = new FileInputStream(f);
                byte[] buf = new byte[BUFFER];

                while ((i = in.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }

                in.close();
                in = null;

            } catch (Exception e) {
                // do nothing
            } finally {

                if (in != null) {
                    in.close();
                }

            }

        }

    }

    public static void unZip(String fileName, String filePath) throws Exception {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(fileName, "GBK");
            Enumeration emu = zipFile.getEntries();

            while (true) {
                while (emu.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) emu.nextElement();
                    if (entry.isDirectory()) {
                        (new File(filePath + entry.getName())).mkdirs();
                    } else {
                        bis = new BufferedInputStream(zipFile.getInputStream(entry));
                        File file = new File(filePath + entry.getName());
                        File parent = file.getParentFile();
                        if (parent != null && !parent.exists()) {
                            parent.mkdirs();
                        }

                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos, 1024);
                        byte[] buf = new byte[1024];
                        boolean var11 = false;

                        int len;
                        while ((len = bis.read(buf, 0, 1024)) != -1) {
                            fos.write(buf, 0, len);
                        }
                    }
                }

                return;
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            if (bos != null) {
                bos.flush();
                bos.close();
                bis.close();
                zipFile.close();
            }

        }

    }

    public static void zip(String src, String dest, List filter) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest));
        File srcFile = new File(src);
        zip(out, srcFile, "", filter);
        out.close();
    }

    public static void zip(ZipOutputStream out, File srcFile, String base, List filter) throws Exception {
        if (!srcFile.exists()) {
            throw new Exception("压缩目录不存在!");
        } else {
            if (srcFile.isDirectory()) {
                File[] files = srcFile.listFiles();
                base = base.length() == 0 ? "" : base + "/";
                if (isExist(base, filter)) {
                    out.putNextEntry(new ZipEntry(base));
                }

                for (int i = 0; i < files.length; ++i) {
                    zip(out, files[i], base + files[i].getName(), filter);
                }
            } else if (isExist(base, filter)) {
                base = base.length() == 0 ? srcFile.getName() : base;
                ZipEntry zipEntry = new ZipEntry(base);
                zipEntry.setComment(comment);
                out.putNextEntry(zipEntry);
                FileInputStream in = new FileInputStream(srcFile);
                byte[] b = new byte[1024];

                int length;
                while ((length = in.read(b, 0, 1024)) != -1) {
                    out.write(b, 0, length);
                }

                in.close();
            }

        }
    }

    public static boolean isExist(String base, List list) {
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                if (base.indexOf((String) list.get(i)) >= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void unZip(String srcFile, String dest, boolean deleteFile) throws Exception {
        File file = new File(srcFile);
        if (!file.exists()) {
            throw new Exception("解压文件不存在!");
        } else {
            ZipFile zipFile = new ZipFile(file);
            Enumeration e = zipFile.getEntries();

            while (true) {
                while (e.hasMoreElements()) {
                    ZipEntry zipEntry = (ZipEntry) e.nextElement();
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File f = new File(dest + name);
                        f.mkdirs();
                    } else {
                        File f = new File(dest + zipEntry.getName());
                        f.getParentFile().mkdirs();
                        f.createNewFile();
                        InputStream is = zipFile.getInputStream(zipEntry);
                        FileOutputStream fos = new FileOutputStream(f);
                        byte[] b = new byte[1024];

                        int length;
                        while ((length = is.read(b, 0, 1024)) != -1) {
                            fos.write(b, 0, length);
                        }

                        is.close();
                        fos.close();
                    }
                }

                if (zipFile != null) {
                    zipFile.close();
                }

                if (deleteFile) {
                    file.deleteOnExit();
                }

                return;
            }
        }
    }

    public static String getZipComment(String srcFile) {
        String comment = "";

        try {
            ZipFile zipFile = new ZipFile(srcFile);
            Enumeration e = zipFile.getEntries();

            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                comment = ze.getComment();
                if (comment != null && !comment.equals("") && !comment.equals("null")) {
                    break;
                }
            }

            zipFile.close();
        } catch (Exception var5) {
            System.out.println("获取zip文件注释信息失败:" + var5.getMessage());
        }

        return comment;
    }

    /**
     * @param fileName
     * @param filePath
     * @throws Exception
     */
    public static void unZipForFilePath(String fileName, String filePath) throws Exception {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(fileName, "UTF-8");
            Enumeration emu = zipFile.getEntries();

            while (true) {
                ZipEntry entry;
                do {
                    if (!emu.hasMoreElements()) {
                        return;
                    }

                    entry = (ZipEntry) emu.nextElement();
                } while (entry.isDirectory());

                StringBuffer sv = new StringBuffer();
                if (entry.getName().indexOf("/") != -1) {
                    String[] str = entry.getName().split("/");

                    for (int i = 0; i < str.length; ++i) {
                        if (i != str.length - 1) {
                            sv.append(str[i] + File.separator);
                        } else {
                            sv.append(str[i]);
                        }
                    }
                } else {
                    sv = new StringBuffer(entry.getName());
                }

                bis = new BufferedInputStream(zipFile.getInputStream(entry));
                File file = new File(filePath + File.separator + sv.toString());
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, 1024);
                byte[] buf = new byte[1024];
                boolean var12 = false;

                int len;
                while ((len = bis.read(buf, 0, 1024)) != -1) {
                    fos.write(buf, 0, len);
                }
            }
        } catch (Exception var16) {
            var16.printStackTrace();
        } finally {
            if (bos != null) {
                bos.flush();
                bos.close();
                bis.close();
                zipFile.close();
            }

        }

    }

    public static void zipFiles(File zipFile, String inputFile, String[] fileNames) throws Exception {
        ZipOutputStream out = null;

        try {
            File f = new File(inputFile);
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            out.setEncoding("GBK");
            out.setLevel(0);
            zipByFileNames(out, f, fileNames);
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            out.close();
        }

    }

    private static void zipByFileNames(ZipOutputStream out, File f, String[] fileNames) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();

            for (int i = 0; i < fl.length; ++i) {
                File file = fl[i];

                for (int j = 0; j < fileNames.length; ++j) {
                    if (fileNames[j].equals(file.getName())) {
                        out.putNextEntry(new ZipEntry(file.getName()));
                        FileInputStream in = new FileInputStream(file);
                        byte[] buf = new byte[1024];

                        int len;
                        while ((len = in.read(buf, 0, 1024)) != -1) {
                            out.write(buf, 0, len);
                        }

                        in.close();
                    }
                }
            }
        }

    }

    public static void deleteByFileNames(String path, String[] fileNames) {
        File files = new File(path);
        File[] flieList = files.listFiles();

        for (int i = 0; i < flieList.length; ++i) {
            String fileName = flieList[i].getName();
            String filePath = flieList[i].getPath();

            for (int j = 0; j < fileNames.length; ++j) {
                if (fileName.equals(fileNames[j])) {
                    FileUtil.delete(filePath);
                }
            }
        }

    }


    public static boolean fileIsExist(String path, String fileName) {
        boolean isExist = false;
        String[] fileList = FileUtil.listFiles(path);
        if (fileList != null && fileList.length > 0) {
            for (int i = 0; i < fileList.length; ++i) {
                if (fileName.equals(fileList[i])) {
                    isExist = true;
                }
            }
        }

        return isExist;
    }

    public static boolean whetherTheFileExistsInZipFile(String zipFilePath, String fileName) throws IOException {
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(zipFilePath, "GBK");
            Enumeration emu = zipFile.getEntries();

            while (emu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) emu.nextElement();
                if (entry.getName().indexOf(fileName) > -1) {
                    return true;
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            zipFile.close();
        }

        return false;
    }

    public static void main(String[] args) {
//        List<String> filter = new ArrayList();
//        filter.add("3RDPARTY");
//        filter.add("BANNER.GIF");

        try {

//            String path = "/Users/yanyong/temp/iuap-pap-baseservice-develop";// + "_" + System.currentTimeMillis();

            String path = "/Users/yanyong/Downloads/wbalone";
            String zipFile = path + ".zip";

            ZipUtils.zip(new File(zipFile), path);


//            zipAppendFile("D:\\upload\\project\\402886b335ebb0470135ebbaea7d0014\\purFile\\公共资源交易中心.GZBS", "d:\\temp\\2.doc");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
