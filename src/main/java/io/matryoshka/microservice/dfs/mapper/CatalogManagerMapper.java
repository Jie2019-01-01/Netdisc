package io.matryoshka.microservice.dfs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import io.matryoshka.microservice.dfs.bean.CatalogDTO;
import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.UserRoleCatalogBean;

/**
 * 目录管理的mapper
 *
 */
@Mapper
public interface CatalogManagerMapper {

	// 条件查询
	public List<CatalogManagerBean> queryByCondition(CatalogManagerBean bean);
	// 通过cid查询目录中对应的数据
	public CatalogManagerBean queryCatalogByCid(int cid);
	// 通过目录id查询文件
	public List<DocManagerBean> queryFilesByCid(int cid);
	
	// 创建目录的方法,将新建的目录写入数据库
	public int newDirectory(CatalogManagerBean bean);
	
	// 移动目录
	public int moveCatalog(CatalogManagerBean mainCbean);
	public int updateSubCatalog(CatalogManagerBean childCbean);
	
	// 通过目录名查询对应的这条数据,目录名是唯一值
	public CatalogManagerBean queryByCatalogName(String catalogName);

	// 根据目录名称id该目录以及下面子目录在数据库中的数据
	public boolean delCatalogByCids(List<Integer> cidsList);
	public boolean delDocByCids(List<Integer> cidsList);
	
	//目录重命名
	public boolean catalogRename(
			@Param("cid") int cid, 
			@Param("newName") String newName
//			@Param("renameAfterCpath") String renameAfterCpath
			);
	
	//删除权限
	public void deleteAuthorization(UserRoleCatalogBean urcBean);
	//用户针对指定目录进行授权
	public int userAuthorizationForDir(UserRoleCatalogBean urcBean);
	//通过userid查询该用户下对每个目录(包括公有)有什么角色
	public List<UserRoleCatalogBean> queryCatalogAndRoleByUserid(int userid);
	//根据目录的类型,查询哪些用户拥有该目录何种权限
	public int queryDirPermissions(UserRoleCatalogBean urcBean);
	
	//全局搜索(模糊查询文档和目录)
	public List<CatalogManagerBean> queryCatalogLike(String keyword);
	public List<DocManagerBean> queryDocLike(String keyword);
	
	//修改文件夹颜色
	public int modifyFolderColor(@Param("cid")int cid,@Param("color")String color);
	
	//查询目录表所有数据,封装DTO返回
	public List<CatalogDTO> queryCatalogs();
}
