package io.matryoshka.microservice.dfs.bean;
/**
 * 建立用户,目录以及角色的关联关系的类
 * @author Administrator
 */
public class UserRoleCatalogBean {

	int userid;//用户id
	int roleid;//角色id
	int cid;//目录id
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
}
