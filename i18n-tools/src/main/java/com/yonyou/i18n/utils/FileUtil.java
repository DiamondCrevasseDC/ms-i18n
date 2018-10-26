package com.yonyou.i18n.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.List;

/**
 * 文件的操作
 *
 * @author  wenfan
 */
public class FileUtil {
    private static final Log log = LogFactory.getLog(FileUtil.class);

    public FileUtil() {
    }

    public static final String[] listFiles(String path) {
        return list(path, false);
    }

    public static final String[] listDirectorys(String path) {
        return list(path, true);
    }

    private static final String[] list(String path, boolean isDirectory) {
        File directory = getDirectory(path);
        if (directory == null) {
            return null;
        } else {
            int count = 0;
            File[] files = directory.listFiles();
            String[] temp = new String[files.length];

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory() == isDirectory) {
                    temp[count] = files[i].getName();
                    ++count;
                }
            }

            return cloneSubarray(temp, 0, count);
        }
    }

    public static String[] cloneSubarray(String[] a, int from, int to) {
        int n = to - from;
        String[] result = new String[n];
        System.arraycopy(a, from, result, 0, n);
        return result;
    }

    public static final String[] list(String path) {
        File file = getDirectory(path);
        if (file != null) {
            String[] list = file.list();
            return list;
        } else {
            return null;
        }
    }

    public static final File getDirectory(String path) {
        File file = new File(path);
        return file.canRead() && file.isDirectory() ? file : null;
    }

    public static void create(String path, String content) {
        try {
            rename(path, path + ".bak");
            RandomAccessFile newfile = new RandomAccessFile(path, "rw");
            newfile.writeBytes(content);
            newfile.close();
        } catch (Exception var3) {
            var3.printStackTrace();
            log.error("FileUtil.create error");
        }

    }

    public static final boolean rename(String orgPathName, String tarPathName) {
        boolean result = false;

        try {
            File file = new File(orgPathName);
            file.renameTo(new File(tarPathName));
            result = true;
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return result;
    }

    public static final boolean delete(String path) {
        try {
            File file = new File(path);
            file.delete();
            return true;
        } catch (Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }

    public static final boolean copy(String source, String target) {
        boolean flag = false;
        byte[] abyte0 = new byte[4096];

        try {
            File mvSourceFile = new File(source);
            if (mvSourceFile.exists()) {
                FileOutputStream fileoutputstream = new FileOutputStream(target);
                FileInputStream fileinputstream = new FileInputStream(source);

                int i;
                while ((i = fileinputstream.read(abyte0)) != -1) {
                    fileoutputstream.write(abyte0, 0, i);
                }

                fileinputstream.close();
                fileoutputstream.close();
            }

            mvSourceFile = null;
            flag = true;
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return flag;
    }

    public static boolean mkdirs(String path) {
        try {
            File dir = new File(path);
            dir.mkdirs();
            return true;
        } catch (Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }

    public static String read(InputStream inputStream) throws IOException {
        InputStreamReader ins = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader br = new BufferedReader(ins);
        String text = "";
        StringBuffer sb = new StringBuffer();

        while ((text = br.readLine()) != null) {
            sb.append(text + "\n");
        }

        br.close();
        return sb.toString();
    }

    public static void getListFiles(String path, List<String> fileList, String suffix, boolean isdepth) {
        File file = new File(path);
        listFile(file, fileList, suffix, isdepth);
    }

    public static List<String> listFile(File f, List<String> fileList, String suffix, boolean isdepth) {
        int begIndex;
        if (f.isDirectory() && isdepth) {
            File[] t = f.listFiles();

            for (begIndex = 0; begIndex < t.length; ++begIndex) {
                listFile(t[begIndex], fileList, suffix, isdepth);
            }
        } else {
            String filePath = f.getAbsolutePath();
            if (suffix != null) {
                begIndex = filePath.lastIndexOf(".");
                String tempsuffix = "";
                if (begIndex != -1) {
                    tempsuffix = filePath.substring(begIndex + 1, filePath.length());
                }

                if (tempsuffix.equals(suffix)) {
                    fileList.add(filePath);
                }
            } else {
                fileList.add(filePath);
            }
        }

        return fileList;
    }

    public static boolean fileExist(String fileName) {
        File objFile = new File(fileName);
        return objFile.exists();
    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                String[] tempList = file.list();
                File temp = null;

                for (int i = 0; i < tempList.length; ++i) {
                    if (path.endsWith(File.separator)) {
                        temp = new File(path + tempList[i]);
                    } else {
                        temp = new File(path + File.separator + tempList[i]);
                    }

                    if (temp.isFile()) {
                        temp.delete();
                    }

                    if (temp.isDirectory()) {
                        delAllFile(path + "/" + tempList[i]);
                        delFolder(path + "/" + tempList[i]);
                    }
                }

            }
        }
    }

    public static void delFolder(String folderPath) {
        delAllFile(folderPath);
        String filePath = folderPath.toString();
        File myFilePath = new File(filePath);
        myFilePath.delete();
    }

    public static void copyFolder(String oldPath, String newPath) throws IOException {
        (new File(newPath)).mkdirs();
        File a = new File(oldPath);
        String[] file = a.list();
        File temp = null;

        for (int i = 0; i < file.length; ++i) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file[i]);
            } else {
                temp = new File(oldPath + File.separator + file[i]);
            }

            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName().toString());
                byte[] b = new byte[5120];

                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }

                output.flush();
                output.close();
                input.close();
            }

            if (temp.isDirectory()) {
                copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }

    }

    public static void main(String[] args) {
    }

    public static boolean writerFile(String filePathName, String content, String unicode) {
        boolean flag = false;
        OutputStreamWriter osw = null;

        try {
            if (filePathName != null && !"".equals(filePathName)) {
                osw = new OutputStreamWriter(new FileOutputStream(filePathName), unicode);
            }
        } catch (FileNotFoundException var19) {
            flag = false;
            var19.printStackTrace();
        } catch (UnsupportedEncodingException var20) {
            flag = false;
            var20.printStackTrace();
        }

        if (osw != null) {
            BufferedWriter bw = new BufferedWriter(osw);

            try {
                if (content != null && !"".equals(content)) {
                    bw.write(content);
                    flag = true;
                }
            } catch (IOException var17) {
                flag = false;
                var17.printStackTrace();
            } finally {
                try {
                    bw.close();
                    osw.close();
                } catch (IOException var16) {
                    flag = false;
                    var16.printStackTrace();
                }

            }
        }

        return flag;
    }
}
