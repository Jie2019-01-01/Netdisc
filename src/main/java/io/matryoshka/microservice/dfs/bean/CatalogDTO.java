package io.matryoshka.microservice.dfs.bean;

import java.util.List;

import javax.xml.transform.Source;

public class CatalogDTO {

	int userid;//用户id
	int cid; // 目录id
	String cname; // 目录名称
	String createdate; // 创建目录时间
	int supDirectory;// 上级目录的标识
	Integer rank; // 目录级别
	int ishide; // 是否隐藏 0. 不隐藏 , 1. 隐藏
	String path; // minio存储中的位置
	String color;//目录颜色
	String range;//可见范围
	List<CatalogDTO> zcatalogChild;// 子节点的数据
	List<Resource> resources;//资源
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public List<Resource> getResources() {
		return resources;
	}
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCreatedate() {
		String createdate = this.createdate.replace(".0", "");
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public int getSupDirectory() {
		return supDirectory;
	}
	public void setSupDirectory(int supDirectory) {
		this.supDirectory = supDirectory;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public int getIshide() {
		return ishide;
	}
	public void setIshide(int ishide) {
		this.ishide = ishide;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public List<CatalogDTO> getZcatalogChild() {
		return zcatalogChild;
	}
	public void setZcatalogChild(List<CatalogDTO> zcatalogChild) {
		this.zcatalogChild = zcatalogChild;
	}
}
