package com.yonyou.i18n.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * 
 * @author wenfa
 *
 */
public class ConfigUtils {

	private static Logger logger = Logger.getLogger(ConfigUtils.class);

	public static Properties props;

	static {

		try {
			InputStream in = ConfigUtils.class.getResourceAsStream("/config.properties");
			
			InputStreamReader read = new InputStreamReader(in, "GBK");// 考虑到编码格式
			
			props = new Properties();

			props.load(read);
			
			read.close();
			in.close();
		} catch (IOException e) {
			logger.error(LogUtils.printMessage("读取配置文件异常！"));
			logger.error(LogUtils.getException(e));
		}
	}

	public static String isExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			throw new RuntimeException(file.getPath() + "文件夹或文件不存在");
		}
		return file.getPath();
	}

	public static String getSystemPath(String rootName) {
		String path = ConfigUtils.class.getResource("/" + rootName).getPath();
		return path;
	}

	public static String getPropertyValue(String key) {
		String value = "";

		value = props.getProperty(key);
		return value;
	}

}
