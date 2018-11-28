package com.yonyou.i18n.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yonyou.i18n.model.OrderedProperties;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 抽取已经国际化的所有中文信息
 * <p>
 * Json资源文件的处理
 *
 * @author wenfan
 */
public class JsonFileUtil {

    private static JsonFileUtil _this = new JsonFileUtil();

    static String resourceFileEncoding = ConfigUtils.getPropertyValue("resourceFileEncoding");

    //	private String legalityReamName = "iuap_zh_CN.properties";
    private String legalityReamName = ".json";

    private ArrayList<File> files = new ArrayList<File>(0);

    private StringBuilder fileContent = new StringBuilder();

//	private Map<String, String> fileDescs = new HashMap<String, String>(0);

    private OrderedProperties prop = new OrderedProperties();

    private Properties corpus = new Properties();

    private String path;

    public JsonFileUtil() {

    }

    /**
     * 属性文件初始化
     *
     * @param path
     * @return void
     */
    public static void init(String path, String legalityReamName) {
        _this.path = path;
        if (legalityReamName != null && !"".equals(legalityReamName))
            _this.legalityReamName = legalityReamName;
        _this.loadFiles();
        _this.initFileContent();
        _this.initFileProps();
        _this.files.clear();
//        _this.initCorpus();
//        _this.macherCorpus();
//    		_this.writeResourceFile();
//    		_this.fileContent = null;
//    		_this.fileDescs = null;
    }

    /**
     * 加载属性文件
     *
     * @return void
     */
    private void loadFiles() {
        _this.getAllFileByFile(new File(_this.path));
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
                if (_this.validateFileName(file)) {
                    _this.files.add(file);
                }
            }
            if (file.isDirectory()) {
                File[] fils = file.listFiles();
                if (null != fils) {
                    for (File tempFile : fils) {
                        _this.getAllFileByFile(tempFile);
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
        if (file.getName().contains(_this.legalityReamName)) {
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
            _this.corpus.load(new InputStreamReader(new FileInputStream(new File(_this.path + File.separator + "corpus-en.properties")), "UTF-8"));
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
        for (File file : _this.files) {
            if (null != file && file.exists()) {
                FileReader fileReader = null;
                BufferedReader bufferedReader = null;
                StringBuffer sb = new StringBuffer();
                try {
                    fileReader = new FileReader(file);
                    bufferedReader = new BufferedReader(fileReader);
                    String temp = bufferedReader.readLine();
                    while (null != temp) {
                        sb.append(temp);
//		            	_this.fileContent.append(temp).append("\n");
                        temp = bufferedReader.readLine();
                    }

                    _this.fileContent.append(sb.toString()).append("\n");

                } catch (Exception e) {
                    LogFactory.getLog(JsonFileUtil.class).error(e);
                } finally {
                    if (null != bufferedReader) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            LogFactory.getLog(JsonFileUtil.class).error(e);
                        }
                    }
                    if (null != fileReader) {
                        try {
                            fileReader.close();
                        } catch (IOException e) {
                            LogFactory.getLog(JsonFileUtil.class).error(e);
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
        for (String key : _this.prop.stringPropertyNames()) {
            if (_this.corpus.containsKey(_this.prop.getProperty(key))) {
                prop.setProperty(key, _this.corpus.getProperty(_this.prop.getProperty(key)));
            } else {
//				prop.setProperty(key, "");
            }
        }
    }

    /**
     * 获取所有资源的ID的前缀，并进行去重
     * 主要用于在新生成资源ID时，生成的id不至于与之前的重复
     *
     * @return
     */
    public static HashSet<String> getKeyPrefix() {

        HashSet<String> keyPrefixs = new HashSet<String>();


        for (String str : _this.fileContent.toString().split("\n")) {
            if (null != str && !"".equals(str.trim())) {
                if (_this.isDescRow(str)) {
                    continue;
                } else if (_this.isValueRow(str)) {

                    JsonObject object = new JsonObject(); //创建Json格式的数据

                    object = new JsonParser().parse(str).getAsJsonObject();

                    Iterator<Map.Entry<String, JsonElement>> obj = object.entrySet().iterator();

                    while (obj.hasNext()) {
                        Map.Entry<String, JsonElement> j = obj.next();

                        if (null != j.getKey() && !"".equals(j.getKey())) {
                            keyPrefixs.add(j.getKey().substring(0, j.getKey().lastIndexOf(".")));
                        }
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

        for (String str : _this.fileContent.toString().split("\n")) {
            if (null != str && !"".equals(str.trim())) {
                if (_this.isDescRow(str)) {
                    continue;
                } else if (_this.isValueRow(str)) {


                    JsonObject object = new JsonObject(); //创建Json格式的数据

                    object = new JsonParser().parse(str).getAsJsonObject();
                    // TODO
                    // 考虑json 的格式是否正确，如果不正确，需要对调整后解析


                    Iterator<Map.Entry<String, JsonElement>> obj = object.entrySet().iterator();
                    while (obj.hasNext()) {
                        Map.Entry<String, JsonElement> j = obj.next();

                        if (null != j.getValue() && !"".equals(delSpecialChar(j.getValue().getAsString()))) {
                            if (prop.containsKey(j.getKey()) && !prop.get(j.getKey()).equals(delSpecialChar(j.getValue().toString()))) {
                                System.out.println(j.getKey());
                            } else {
                                prop.setProperty(j.getKey(), delSpecialChar(j.getValue().getAsString()));
                            }
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
     * 判断该行是否有描述信息或者注释信息
     *
     * @param str 当前该行字符串
     * @return boolean
     */
    private boolean isDescRow(String str) {
        return str.trim().substring(0, 1).equals("#");
    }


    /**
     * 判断该行是否为标准资源
     *
     * @param str 当前该行字符串
     * @return boolean
     */
    private boolean isValueRow(String str) {
        return !str.trim().substring(0, 1).equals("#");
//		return !str.trim().substring(0, 1).equals("#") && str.trim().contains("=") && str.trim().indexOf("=") == str.trim().lastIndexOf("=");
    }


    /**
     * 将抽取出来的资源写入资源文件中
     * 做英文资源文件
     */
    public static void writeResourceFile() {

        File file = new File(_this.path + File.separator + "iuap_all.properties");

        // 为了保证资源的顺序，采用LinkedHashSet存储
//		OrderedProperties prop = new OrderedProperties();

        BufferedWriter output = null;

        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            // 设置属性值
//			Iterator<Entry<String, String>> descs = _this.fileDescs.entrySet().iterator();
//			while(descs.hasNext()){
//				Entry<String, String> desc = descs.next();
//				prop.setProperty(desc.getKey(), desc.getValue());
//			}

            // 保存属性值
            _this.prop.store(output, "create the resource file");

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

        init("/Users/yanyong/workspace/java/yonyou/iweb_apportal/workbench/wbalone/target/workbench/locales", "");

//	    System.out.println(_this.fileDescs);

    }


    /**
     * 得到描述信息
     *
     * @param key
     * @return
     */
    public static String getProps(String key) {
        return _this.prop.get(key).toString();
    }


    /**
     * 返回资源
     *
     * @return
     */
    public OrderedProperties getProps() {
        return _this.prop;
    }

    /**
     * 文件编码
     *
     * @param theString
     * @return String
     */
    private String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

}