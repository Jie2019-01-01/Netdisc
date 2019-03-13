package io.matryoshka.microservice.dfs.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 目录管理的bean对象 
 * @author Administrator
 *
 */
public class CatalogManagerBean {

	
	int cid; // 目录id
	String cname; // 目录名称
	String createuser; // 创建人
	String createdate; // 创建目录时间
	Integer flag; // 删除标记
	int supDirectory;// 上级目录的标识
	Integer rank; // 目录级别
	String mapperName; // 映射目录名
	int ishide; // 是否隐藏 0. 不隐藏 , 1. 隐藏
	String path; // minio存储中的位置
	String cpath;// 网盘中的目录位置
	String color;//目录颜色
	String range;//可见范围
	List<CatalogManagerBean> zcatalogChild;// 子节点的数据
	
	
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCreateuser() {
		return createuser;
	}
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	public String getCreatedate() {
		String createdate = this.createdate.replace(".0", "");
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public Integer getFlag() {
		if(flag==null){
			flag=0;
		}
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Integer getRank() {
		if(rank==null){
			rank=0;
		}
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public int getSupDirectory() {
		return supDirectory;
	}
	public void setSupDirectory(int supDirectory) {
		this.supDirectory = supDirectory;
	}
	public List<CatalogManagerBean> getZcatalogChild() {
		return zcatalogChild;
	}
	public void setZcatalogChild(List<CatalogManagerBean> zcatalogChild) {
		this.zcatalogChild = zcatalogChild;
	}
	public String getMapperName() {
		return mapperName;
	}
	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
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
	public String getCpath() {
		return cpath;
	}
	public void setCpath(String cpath) {
		this.cpath = cpath;
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
	
}
