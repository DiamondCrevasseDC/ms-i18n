package com.yonyou.i18n.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.MLUtil;

/**
 * 
 * @author dingrf
 *
 */
public class UTFProperties extends Hashtable<String, String> {
	
	private static Logger logger = Logger.getLogger(UTFProperties.class);
	
	private String charsetName = null;
	private static final long serialVersionUID = 4112578634029874840L;
	private static final String keyValueSeparators = "=: \t\r\n\f";
	private static final String strictKeyValueSeparators = "=:";
	// private static final String specialSaveChars = "=: \t\r\n\f#!";
	private static final String whiteSpaceChars = " \t\r\n\f";
	// private static final char[] hexDigit = {
	// '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
	// 'E', 'F' };

	public UTFProperties(String charsetName) {
		if (charsetName == null) {
			charsetName = "UTF-8";
		}
		this.charsetName = charsetName;
	}

	public synchronized Object setProperty(String key, String value) {
		return put(key, value);
	}

	public synchronized void load(InputStream inStream) throws IOException {
		load(inStream, this.charsetName);
	}

	public synchronized void load(InputStream inStream, String charset) throws IOException {
		if (inStream == null)
			return;
		if (charset == null)
			charset = this.charsetName;
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream, charset));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				return;
			}
			if (line.length() <= 0) {
				continue;
			}
			int len = line.length();

			int keyStart;
			for (keyStart = 0; keyStart < len; keyStart++) {
				if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1) {
					break;
				}
			}
			if (keyStart == len) {
				continue;
			}
			char firstChar = line.charAt(keyStart);
			if ((firstChar != '#') && (firstChar != '!')) {
				while (continueLine(line)) {
					String nextLine = in.readLine();
					if (nextLine == null)
						nextLine = "";
					String loppedLine = line.substring(0, len - 1);

					int startIndex;
					for (startIndex = 0; startIndex < nextLine.length(); startIndex++)
						if (whiteSpaceChars.indexOf(nextLine.charAt(startIndex)) == -1)
							break;
					nextLine = nextLine.substring(startIndex, nextLine.length());
					line = new String(loppedLine + nextLine);
					len = line.length();
				}

				int separatorIndex;
				for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
					char currentChar = line.charAt(separatorIndex);
					if (currentChar == '\\')
						separatorIndex++;
					else if (keyValueSeparators.indexOf(currentChar) != -1) {
						break;
					}
				}

				int valueIndex;
				for (valueIndex = separatorIndex; valueIndex < len; valueIndex++) {
					if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1) {
						break;
					}
				}
				if ((valueIndex < len) && (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1)) {
					valueIndex++;
				}

				while (valueIndex < len) {
					if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
						break;
					valueIndex++;
				}
				String key = line.substring(keyStart, separatorIndex);
				String value = separatorIndex < len ? line.substring(valueIndex, len) : "";

				value = Helper.windEscapeChars(value);

				if ((this.charsetName.equals("8859_1")) || (this.charsetName.equalsIgnoreCase("ISO-8859-1"))) {
					key = MLUtil.gb2Unicode(key);
					value = MLUtil.gb2Unicode(value);
				}
				if ((key == null) || (value == null)) {
//					Activator.getDefault().logError("警告:加载资源文件时 key = " + key + "     value = " + value);
//					System.out.println("警告:加载资源文件时 key = " + key + "     value = " + value);
					logger.info("警告:加载资源文件时 key = " + key + "     value = " + value);
					continue;
				}
				put(key, value);
			}
		}
	}

	private boolean continueLine(String line) {
		int slashCount = 0;
		int index = line.length() - 1;
		while ((index >= 0) && (line.charAt(index--) == '\\'))
			slashCount++;
		return slashCount % 2 == 1;
	}

	public synchronized void save(OutputStream out) {
		try {
			store(out);
		} catch (IOException localIOException) {
		}
	}

	public synchronized void store(OutputStream out) throws IOException {
		store(out, this.charsetName);
	}

	public synchronized void store(OutputStream out, String charset) throws IOException {
		if (charset == null) {
			charset = this.charsetName;
		}
		BufferedWriter awriter = new BufferedWriter(new OutputStreamWriter(out, charset));

		for (Enumeration<String> e = keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = (String) get(key);

			if (this.charsetName.equals("GBK")) {
				key = MLUtil.uniCode2Gb(key);
				val = MLUtil.uniCode2Gb(val);
			}
			writeln(awriter, key + "=" + val);
		}
		awriter.flush();
	}

	private static void writeln(BufferedWriter bw, String s) throws IOException {
		bw.write(s);
		bw.newLine();
	}

	public String getProperty(String key) {
		Object oval = super.get(key);
		String sval = (oval instanceof String) ? (String) oval : null;

		return sval;
	}

	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return val == null ? defaultValue : val;
	}

	public Enumeration<String> propertyNames() {
		Hashtable<String, String> h = new Hashtable<String, String>();
		enumerate(h);
		return h.keys();
	}

	public void list(PrintStream out) {
		out.println("-- listing properties --");
		Hashtable<String, String> h = new Hashtable<String, String>();
		enumerate(h);
		for (Enumeration<String> e = h.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = (String) h.get(key);
			if (val.length() > 40) {
				val = val.substring(0, 37) + "...";
			}
			out.println(key + "=" + val);
		}
	}

	public void list(PrintWriter out) {
		out.println("-- listing properties --");
		Hashtable<String, String> h = new Hashtable<String, String>();
		enumerate(h);
		for (Enumeration<String> e = h.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String val = (String) h.get(key);
			if (val.length() > 40) {
				val = val.substring(0, 37) + "...";
			}
			out.println(key + "=" + val);
		}
	}

	private synchronized void enumerate(Hashtable<String, String> h) {
		for (Enumeration<String> e = keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			h.put(key, get(key));
		}
	}

}