package io.matryoshka.microservice.dfs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;

/**
 * 文档管理的mapper
 * @author Administrator
 *
 */
@Mapper
public interface DocManagerMapper {

	// 新建文档,与目录相关联
	public int newDocument(DocManagerBean bean);
	
	// 通过目录名称查找目录的id , 存储到文档相关联的字段中
	public int queryCatalogCid(String cname);
	
	// 查询文件名是否重复
	public int isFileRepeat(@Param("cname") String cname , @Param("fname") String fname);
	
	// 执行文档删除操作
	public int delDocmanage(@Param("fid")int fid, @Param("cid")int cid);
	
	// 通过fid去查询文档表中的文件对象
	public DocManagerBean getDocBean(int fid);
	
	// 查询指定目录下的文档信息
	public List<DocManagerBean> queryDocuments(int cid);
	
	// 移动文件
	public boolean moveFile(@Param("cid")int cid , @Param("fid")int fid);
	
	// 文件重命名
	public boolean renameFile(@Param("fid")int fid, @Param("renameFile") String renameFile);
	
	// 通过cid查询目录中对应的数据
	public CatalogManagerBean queryCatalogByCid(int cid);
	
	
}
