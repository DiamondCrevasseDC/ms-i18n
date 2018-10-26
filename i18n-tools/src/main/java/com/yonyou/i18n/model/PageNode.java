package com.yonyou.i18n.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 扫描完整目录后的文件节点，
 * 
 * TODO 叫FileNode更合适
 * 
 * @author dingrf
 *
 */
public class PageNode {

	/**ID值*/
	private String id;
	
	/**显示名称*/
	private String name;
	
	/**父节点*/
	private PageNode parent;
	
	/**子节点List*/
	private List<PageNode> childrens;
	
	/**路径*/
	private String path;
	
	/**是否为文件*/
	private boolean isFile;
	
	/**是否为项目*/
	private boolean isProject;
	
	/**根*/
	private String root;
	
	/** key值前缀*/
	private String prefix;
	
	/**IFile*/
	private File file;
	
	/**文件发生改变*/
	private boolean changed = false;
	
	/** 资源文件模块名称 */
	private String resModuleName;
	
//	/** 资源文件模块名称*/
//	private String bcpName;
	
	/** 文件类型*/
	private String type;
	
	/** 是否存在中文 **/
	private boolean isExistChinese;
	
	/** 如果存在，则记录行号  **/
	private List<Integer> existChiLine;
	
	/**
	 * 文件的多语资源清单
	 */
//	private MLResSubstitution[] substitutions;
	private ArrayList<MLResSubstitution> substitutions = new ArrayList<MLResSubstitution>();
	
	/**
	 * 需要向文件中写入的内容，比如java需要import class， js需要import component
	 * 
	 * TODO
	 * 暂时采用字符串,后续改为map对象，需要定位、解析、写入等操作
	 */
	private String addContent;
	
	/**
	 * 
	 * @param f
	 * @param resModule
	 * @param root
	 */
	public PageNode(File f,String resModule,String root){
		this.setId(f.getName());
		this.setName(f.getName());
		this.setPath(f.getAbsolutePath());
		this.setFile(f.isFile());
		this.setFile(f);
		this.setResModuleName(resModule);
		this.setRoot(root);
	}
	
//	public PageNode(File f,String resModule){
//		this.setId(f.getName());
//		this.setName(f.getName());
//		this.setFile(f.isFile());
//		this.setPath(f.getAbsolutePath());
//		this.setFile(f);
//		this.setResModuleName(resModule);
//	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public PageNode getParent() {
		return parent;
	}

	public void setParent(PageNode parent) {
		this.parent = parent;
	}

	public List<PageNode> getChildrens() {
		if (childrens==null){
			childrens = new ArrayList<PageNode>();
		}
		return childrens;
	}

	public void setChildrens(List<PageNode> childrens) {
		this.childrens = childrens;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	
	public boolean isProject() {
		return isProject;
	}

	public void setProject(boolean isProject) {
		this.isProject = isProject;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
//		this.prefix = root + "-";
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public String getResModuleName() {
		return resModuleName;
	}

	public void setResModuleName(String resModuleName) {
		this.resModuleName = resModuleName;
	}

//	public String getBcpName() {
//		return bcpName;
//	}
//
//	public void setBcpName(String bcpName) {
//		this.bcpName = bcpName;
//	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<MLResSubstitution> getSubstitutions() {
		return substitutions;
	}

	public void setSubstitutions(ArrayList<MLResSubstitution> substitutions) {
		this.substitutions = substitutions;
	}

	public String getAddContent() {
		return addContent;
	}

	public void setAddContent(String addContent) {
		this.addContent = addContent;
	}

	public boolean isExistChinese() {
		return isExistChinese;
	}

	public void setExistChinese(boolean isExistChinese) {
		this.isExistChinese = isExistChinese;
	}

	public List<Integer> getExistChiLine() {
		return existChiLine;
	}

	public void setExistChiLine(List<Integer> existChiLine) {
		this.existChiLine = existChiLine;
	}

//	public MLResSubstitution[] getSubstitutions() {
//		return substitutions;
//	}
//
//	public void setSubstitutions(MLResSubstitution[] substitutions) {
//		this.substitutions = substitutions;
//	}
	

}
