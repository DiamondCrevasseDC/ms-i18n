package com.yonyou.i18n.utils;

import com.yonyou.i18n.model.OrderedProperties;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * 抽取已经国际化的所有中文信息，主要用于资源的整理、翻译等
 * <p>
 * 方案有两种：
 * 一、 直接按照资源文件的方式进行读取
 * 二、 按照文件流的方式读取字符，然后解析。
 * <p>
 * properties文件的处理
 *
 * @author wenfan
 */
public class ResourceFileUtil {

//    private ResourceFileUtil _this = new ResourceFileUtil();

    private String legalityReamName = "iuap_zh_CN.properties";

    private String resourceFileEncoding = ConfigUtils.getPropertyValue("resourceFileEncoding");

//	private String legalityReamName = ".json";

    private ArrayList<File> files = new ArrayList<File>(0);

    private StringBuilder fileContent = new StringBuilder("");

//	private Map<String, String> fileDescs = new HashMap<String, String>(0);

    private OrderedProperties prop = new OrderedProperties();

    private Properties corpus = new Properties();

    private String path;

    public ResourceFileUtil() {

    }

    public OrderedProperties getPropsFromFile(File file) {

        // 为了保证资源的顺序，采用LinkedHashSet存储
        OrderedProperties prop = new OrderedProperties();

        try {
            if (file.exists())
                prop.load(new InputStreamReader(new FileInputStream(file), resourceFileEncoding));
        } catch (Exception e) {

        }

        return prop;

    }

    public OrderedProperties getPropsFromFiles() {

        // 为了保证资源的顺序，采用LinkedHashSet存储
        OrderedProperties prop = new OrderedProperties();

        for (File file : this.files) {
            if (null != file && file.exists()) {

                prop.add(this.getPropsFromFile(file));
            }
        }

        return prop;

    }


    public void setEnglishProps(Properties properties) {

        // 为了保证资源的顺序，采用LinkedHashSet存储
        OrderedProperties prop;

        for (File file : this.files) {
            if (null != file && file.exists()) {

                prop = this.getPropsFromFile(file);

                //  update the resource
                for(String key : prop.stringPropertyNames()){

                    if(properties.get(key) != null && !"".equals(properties.get(key))){
                        prop.put(key, properties.get(key));
                    }
                }

                File fileBack = new File(file.getAbsolutePath().replace("zh_CN", "en_US"));

                writePropsFile(fileBack, prop);
            }
        }

    }


    private void writePropsFile(File file, OrderedProperties prop) {

        BufferedWriter output = null;

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), resourceFileEncoding));

            // 保存属性值
            prop.store(output, "create the english resource file");

            output.flush();
            output.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 属性文件初始化：主要是依据路径加载所有的符合类型的文件
     *
     * @param path
     * @return void
     */
    public void init(String path, String legalityReamName) {
        this.path = path;
        if (legalityReamName != null && !"".equals(legalityReamName))
            this.legalityReamName = legalityReamName;
        this.loadFiles();
//        this.initFileContent();
//        this.initFileProps();
//        this.files.clear();
//        this.initCorpus();
//        this.macherCorpus();
//    		this.writeResourceFile();
//        this.fileContent = null;
//    		this.fileDescs = null;
    }

    /**
     * 加载属性文件
     *
     * @return void
     */
    private void loadFiles() {
        this.getAllFileByFile(new File(this.path));
    }

    /**
     * 递归获取所有File对象,包含子文件夹文件
     *
     * @param file 当前File
     * @return void
     * @throws Exception
     */
    private void getAllFileByFile(File file) {
        if (null != file) {
            if (file.isFile()) {
                if (this.validateFileName(file)) {
                    this.files.add(file);
                }
            }
            if (file.isDirectory()) {
                File[] fils = file.listFiles();
                if (null != fils) {
                    for (File tempFile : fils) {
                        this.getAllFileByFile(tempFile);
                    }
                }
            }
        }
    }

    /**
     * 校验文件名
     *
     * @param file 当前File
     * @return boolean
     * @throws Exception
     */
    private boolean validateFileName(File file) {
        if (file.getName().contains(this.legalityReamName)) {
            return true;
        }
        return false;
    }

    /**
     * 初始化语料库
     *
     * @return void
     */
    private void initCorpus() {
        try {
            this.corpus.load(new InputStreamReader(new FileInputStream(new File(this.path + File.separator + "corpus-en.properties")), "UTF-8"));
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * 初始化属性文件内容
     *
     * @return void
     */
    private void initFileContent() {
        for (File file : this.files) {
            if (null != file && file.exists()) {
                FileReader fileReader = null;
                BufferedReader bufferedReader = null;
                try {
                    fileReader = new FileReader(file);
                    bufferedReader = new BufferedReader(fileReader);
                    String temp = bufferedReader.readLine();
                    while (null != temp) {
                        this.fileContent.append(temp).append("\n");
                        temp = bufferedReader.readLine();
                    }
                } catch (Exception e) {
                    LogFactory.getLog(ResourceFileUtil.class).error(e);
                } finally {
                    if (null != bufferedReader) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            LogFactory.getLog(ResourceFileUtil.class).error(e);
                        }
                    }
                    if (null != fileReader) {
                        try {
                            fileReader.close();
                        } catch (IOException e) {
                            LogFactory.getLog(ResourceFileUtil.class).error(e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 匹配语料库
     */
    private void macherCorpus() {
        for (String key : this.prop.stringPropertyNames()) {
            if (this.corpus.containsKey(this.prop.getProperty(key))) {
                prop.setProperty(key, this.corpus.getProperty(this.prop.getProperty(key)));
            } else {
                prop.setProperty(key, "");
            }
        }
    }

    /**
     * 获取所有资源的ID的前缀，并进行去重
     * 主要用于在新生成资源ID时，生成的id不至于与之前的重复
     *
     * @return
     */
    public HashSet<String> getKeyPrefix() {

        HashSet<String> keyPrefixs = new HashSet<String>();

        if (this.fileContent == null)
            return keyPrefixs;

        for (String str : this.fileContent.toString().split("\n")) {
            if (null != str && !"".equals(str.trim())) {
                if (this.isDescRow(str)) {
                    continue;
                } else if (this.isValueRow(str)) {
                    String[] value = str.split("=");

                    if (value.length == 2 && null != value[0] && !"".equals(value[0])) {
                        keyPrefixs.add(value[0].substring(0, value[0].lastIndexOf(".")));
                    }
                }
            }
        }

        return keyPrefixs;
    }

    /**
     * 获取所有的资源，剔除重复的资源id
     *
     * @return void
     */
    private void initFileProps() {

        if (this.fileContent == null || "".equals(this.fileContent.toString())) {
            return;
        }
        for (String str : delSpecialChar(this.fileContent.toString()).split("\n")) {
            if (null != str && !"".equals(str.trim())) {
                if (this.isDescRow(str)) {
                    continue;
                } else if (this.isValueRow(str)) {
                    String[] value = str.split("=");
                    if (value.length == 2 && null != value[1] && !"".equals(value[1])) {
                        if (prop.containsKey(value[0]) && !prop.get(value[0]).equals(value[1])) {
                            System.out.println(value[0]);
                        } else {
                            prop.setProperty(value[0], value[1]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除\n、~、等特殊字符
     *
     * @param str
     * @return
     */
    private String delSpecialChar(String str) {
        return str.replaceAll(Matcher.quoteReplacement("\\n"), "")
                .replaceAll("~", "")
                .replaceAll("&nbsp;", "")
                .replaceAll(" ", "")
                .replaceAll(Matcher.quoteReplacement("\\"), "")
                .replaceAll(Matcher.quoteReplacement(":"), "")
                .replaceAll(Matcher.quoteReplacement("!"), "")
                .replaceAll(Matcher.quoteReplacement("："), "")
                .replaceAll(Matcher.quoteReplacement("！"), "");
    }

    /**
     * 判断该行是否有描述信息
     *
     * @param str 当前该行字符串
     * @return boolean
     */
    private boolean isDescRow(String str) {
        return str.trim().substring(0, 1).equals("#");
    }


    /**
     * 判断该行是否有注释信息
     *
     * @param str 当前该行字符串
     * @return boolean
     */
    private boolean isValueRow(String str) {
        return !str.trim().substring(0, 1).equals("#") && str.trim().contains("=") && str.trim().indexOf("=") == str.trim().lastIndexOf("=");
    }


    /**
     * 将抽取出来的资源写入资源文件中
     * 做英文资源文件
     */
    public void writeResourceFile() {

        File file = new File(this.path + File.separator + "iuap_all.properties");

        // 为了保证资源的顺序，采用LinkedHashSet存储
//		OrderedProperties prop = new OrderedProperties();

        BufferedWriter output = null;

        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            // 设置属性值
//			Iterator<Entry<String, String>> descs = this.fileDescs.entrySet().iterator();
//			while(descs.hasNext()){
//				Entry<String, String> desc = descs.next();
//				prop.setProperty(desc.getKey(), desc.getValue());
//			}

            // 保存属性值
            this.prop.store(output, "create the resource file");

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

//	    init("D:\\workspace\\iuap_apportal\\iweb_apportal\\workbench\\wbalone\\target\\workbench\\locales");

//	    init("/Users/yanyong/workspace/java/yonyou/iweb_apportal/workbench/wbalone/target/workbench/locales", "");

//        init("/Users/yanyong/Desktop/controller/", "");

//	    System.out.println(this.fileDescs);

    }


    /**
     * 得到描述信息
     *
     * @param key
     * @return
     */
    public String getProps(String key) {
        return this.prop.get(key).toString();
    }


    /**
     * 返回资源
     *
     * @return
     */
    public OrderedProperties getProps() {
        return this.prop;
    }


}