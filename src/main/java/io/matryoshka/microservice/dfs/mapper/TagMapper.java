package io.matryoshka.microservice.dfs.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.TagBean;

@Mapper
public interface TagMapper {
 
	/**
	 * 新增tag元素
	 */
	public int addTag(TagBean tagBean);
	/**
	 * 查询tag元素
	 */
	public List<TagBean> queryTags();
	
	/**
	 * 设置tag之前执行删除操作
	 */
	public boolean deleteSetTag(@Param("fid")int fid,@Param("tagid")int tagid);
	/**
	 * 为文档设置tag
	 */
	public int setTag(@Param("fid")int fid,@Param("tagid")int tagid);
	
	/**
	 * 通过tag查询文档数据,过滤查询
	 */
	public List<DocManagerBean> queryDocByTag(@Param("tagids")int[] tagids,@Param("num")int num);
	/**
	 * 修改tag属性(包含名称和颜色)
	 */
	public int updateTagProperty(TagBean tagBean);
	
	/**
	 * 删除tag
	 */
	public int deleteTag(int tagId);
	
	/**
	 * 查询某个文档下的tag
	 */
	public List<TagBean> queryFileTags(int fid);
}
