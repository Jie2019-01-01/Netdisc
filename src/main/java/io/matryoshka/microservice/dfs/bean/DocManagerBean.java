package io.matryoshka.microservice.dfs.bean;
/**
 * 文档管理的bean对象 
 * @author Administrator
 *
 */
public class DocManagerBean {

	Integer fid;  // 文档id
	String fname; // 文档名
	String createuser; // 创建人
	String createdate; // 创建时间
	Integer cid; // 当前文档所在的目录id
	Integer flag; // 删除标记: 0.不删除 ; 1.删除
	String type; // 文件的类型
	String size; // 表示文件的大小

	public Integer getFid() {
		return fid;
	}
	public void setFid(Integer fid) {
		this.fid = fid;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
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
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
