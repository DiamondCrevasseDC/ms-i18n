package com.yonyou.i18n.utils;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * 字符编码工具类
 *
 * @author wenfa
 *
 */
public class Observer implements nsICharsetDetectionObserver{
		
		String charset = "";
		
		/**
		 * 该部分在nsDetector中被调用，用于输出字符编码
		 */
		public void Notify(String charset){
			this.charset = charset;
		}
		
		/**
		 * 通过该方法获取字符编码
		 */
		public String toString(){
			return charset;
		}

}
