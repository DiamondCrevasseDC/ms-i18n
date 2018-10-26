package com.yonyou.i18n.model;

import java.util.ArrayList;

import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.ISNConvert;


/**
 * 替换的中文的各项信息
 * 
 * 包括key、 value、 replace info、 elements info
 * 
 * @author wenfa
 *
 */
public class MLResSubstitution {
	
	private ArrayList<MLResElement> elements = new ArrayList<MLResElement>();
	
	private String langModule = "";
	
	// 中文字符
	private String value = null;
	private String englishValue = null;
	private String twValue = null;
	
	// 存放到资源文件的key值：路径+流水保证全局唯一
	private String key = "";
	private String extKey = "";
	private String extLangModule = "";
	
	// 先采用messages，取值配置文件
	private String prefix = "messages";
	private String paramStr = "";
	private int state = 0;
	
	// 需要替换中文的对象，根据文件类型等判断
	private String replaceStr;

	/** PageNode */
	private PageNode pageNode;
	

	public MLResSubstitution(MLResElement element, String value) {
//		this.element = element;
		this.value = value;
		this.englishValue = value;
		this.twValue = ISNConvert.gbToBig5(value);
	}

	public MLResSubstitution() {
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		return this.value;
	}

	public String getKey() {
		return this.key;
	}

	public String getKeyWithPrefix() {
		return this.prefix + this.key;
	}

	public String getRealKey() {
		if (this.state == 0)
			return getKeyWithPrefix();
		if (this.state == 1)
			return getExtKey();

		return "";
	}

	public int getState() {
		return this.state;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setState(int state) {
		if ((Helper.isEmptyString(this.extKey)) && (state == 1))
			state = 2;

		this.state = state;
	}


	public String getLangModule() {
		return this.langModule;
	}

	public String getRealLangModule() {
		if (this.state == 0)
			return getLangModule();
		if (this.state == 1)
			return this.extLangModule;

		return "";
	}

	public void setLangModule(String langModule) {
		this.langModule = langModule;
	}

	public String getExtKey() {
		return this.extKey;
	}

	public void setExtKey(String extKey) {
		this.extKey = extKey;
	}

	public String getExtLangModule() {
		return this.extLangModule;
	}

	public void setExtLangModule(String extLangModule) {
		this.extLangModule = extLangModule;
	}

	public String getEnglishValue() {
		if ((this.state == 2) || (this.state == 1))
			return "";

		return this.englishValue;
	}

	public void setEnglishValue(String englishValue) {
		this.englishValue = englishValue;
	}

	public String getTwValue() {
		if ((this.state == 2) || (this.state == 1))
			return "";

		return this.twValue;
	}

	public void setTwValue(String twValue) {
		this.twValue = twValue;
	}

	public PageNode getPageNode() {
		return pageNode;
	}

	public void setPageNode(PageNode pageNode) {
		this.pageNode = pageNode;
	}


	public String getPrefix(){
		return this.prefix;
	}
	public void setPrefix(String prefix){
		this.prefix = prefix;
	}

	public String getParamStr() {
		return paramStr;
	}

	public void setParamStr(String paramStr) {
		this.paramStr = paramStr;
	}

	public ArrayList<MLResElement> getElements() {
		return elements;
	}

	public void setElements(ArrayList<MLResElement> elements) {
		this.elements = elements;
	}

	public String getReplaceStr() {
		return replaceStr;
	}

	public void setReplaceStr(String replaceStr) {
		this.replaceStr = replaceStr;
	}
	
//	public MLResElement[] getElementss() {
//		return elements;
//	}
//
//	public void setElements(MLResElement[] elements) {
//		this.elements = elements;
//	}
	
}