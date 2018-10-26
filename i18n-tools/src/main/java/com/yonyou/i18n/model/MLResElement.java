package com.yonyou.i18n.model;


/**
 * 中文元素在文件中的位置，如果是完全一致的话就采用统一的key值
 * 
 * @author wenfa
 *
 */
public class MLResElement {
	  private String value;
	  private int line;
	  private int index;
	  private int length;

	  public static String createTagText(int index) {
	    return "/*$MLRes-" + index + "$*/";
	  }

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}



}
