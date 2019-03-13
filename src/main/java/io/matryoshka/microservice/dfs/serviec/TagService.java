package io.matryoshka.microservice.dfs.serviec;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.TagBean;
import io.matryoshka.microservice.dfs.mapper.TagMapper;

@Service
public class TagService {

	@Autowired
	private TagMapper tagMapper;
	
	/**
	 * 新增tag标签
	 * @param tagBean 新增的tag参数
	 * @return
	 */
	public int addTag(TagBean tagBean){
		int rows = tagMapper.addTag(tagBean);
		return rows;
	}
	/**
	 * 查询tag标签
	 * @param tagBean 新增的tag参数
	 * @return
	 */
	public List<TagBean> queryTags(){
		List<TagBean> tags = tagMapper.queryTags();
		return tags;
	}
	
	/**
	 * 文档设置tag
	 * @param fid 文档id
	 * @param tagid 单个tagid
	 * @return
	 */
	@Transactional
	public int setTag(int fid,int tagid){
		//为防止相同文档设置tag重复,在给文档设置tag时将rel_tag_doc表中的该条数据删除
		tagMapper.deleteSetTag(fid, tagid);
		int rows = tagMapper.setTag(fid, tagid);
		return rows;
	}
	
	/**
	 * 通过tag查询文档数据,过滤查询
	 * @param tagids 通过tag过滤的tagid,可以是多个 
	 * @param num tagid参数的个数
	 * @return
	 */
	public List<DocManagerBean> queryDocByTag(int[] tagids,int num){
		List<DocManagerBean> docList = tagMapper.queryDocByTag(tagids, num);
		return docList;
	}
	
	/**
	 * 修改tag属性(包含名称和颜色)
	 */
	public int updateTagProperty(TagBean tagBean){
		int rows = tagMapper.updateTagProperty(tagBean);
		return rows;
	}
	
	/**
	 * 删除tag
	 * @param tagId
	 * @return
	 */
	public int deleteTag(int tagId){
		int rows = tagMapper.deleteTag(tagId);
		return rows;
	}
	
	/**
	 * 查询文档下包含的tag
	 * @param fid
	 * @return
	 */
	public List<TagBean> queryFileTags(int fid){
		List<TagBean> tagBeans = tagMapper.queryFileTags(fid);
		return tagBeans;
	}
}
