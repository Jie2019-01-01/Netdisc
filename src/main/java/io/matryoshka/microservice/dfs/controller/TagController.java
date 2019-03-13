package io.matryoshka.microservice.dfs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.TagBean;
import io.matryoshka.microservice.dfs.serviec.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

/**
 * 文档管理中的tag,通过tag元素实现过滤
 * @author Administrator
 *
 */
@Controller
@Api
public class TagController {

	@Autowired
	private TagService tagService;
	/**
	 * 新增tag元素
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "addTag", method={RequestMethod.POST})
	@ApiOperation(value = "新建tag标签")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="tagName",value="tag名称",required=true,dataType="string",paramType="query"),
			@ApiImplicitParam(name="color",value="目录颜色",required=true,dataType="string",paramType="query"),
	})
	public JSONObject addTag(String tagName,String color){
		JSONObject map = new JSONObject();
		
		TagBean tagBean = new TagBean();
		tagBean.setColor(color);		tagBean.setTagName(tagName);
		int rows = tagService.addTag(tagBean);
		if(rows>0){
			map.put("message", "tag标签上传成功");
		}else{
			map.put("message", "tag标签上传失败");
		}
		return map;
	}
	
	/**
	 * 查询tag元素
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "queryTags", method={RequestMethod.GET})
	@ApiOperation(value = "查询tag标签")
	public JSONObject queryTags(){
		JSONObject map = new JSONObject();
		List<TagBean> tags = tagService.queryTags();
		map.put("message", "tag标签查询成功");
		map.put("data", tags);
		return map;
	}
	
	/**
	 * 为文档设置tag
	 * @param fid 文档的id
	 * @param tagids 设置的tagid,可以有多个
	 */
	@ResponseBody
	@RequestMapping(value="setTag",method=RequestMethod.POST)
	@ApiOperation(value = "为文档设置tag")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="fid",value="文档的id",dataType="int",paramType="query"),
			@ApiImplicitParam(name="tagids",value="设置的tagid,可以有多个",allowMultiple=true,dataType="int",paramType="query")
				}
			)
	public JSONObject setTag(int fid , int[] tagids){
		JSONObject map = new JSONObject();
		if(tagids!=null){
			for (int i = 0; i < tagids.length; i++) {
				int tagid = tagids[i];
				int rows = tagService.setTag(fid, tagid);
				if(rows<=0){
					map.put("message", "文档tag设置失败");
					return map;
				}
			}
		}else{
			map.put("message", "设置的tag不能为空");
			return map;
		}
		map.put("message", "文档tag设置成功");
		return map;
	}
	
	/**
	 * 通过tag查询文档数据,过滤查询
	 * @param tagids 通过tag过滤的tagid,可以是多个
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryDocByTag",method=RequestMethod.GET)
	@ApiOperation(value = "通过tag查询文档数据,过滤查询")
	@ApiImplicitParam(name="tagids",value="设置的tagid,可以有多个",allowMultiple=true,dataType="int",paramType="query")
	public JSONObject queryDocByTag(int[] tagids){
		JSONObject map = new JSONObject();
		int num = tagids.length;
		List<DocManagerBean> docList = tagService.queryDocByTag(tagids, num);
		map.put("data", docList);
		return map;
	}
	
	/**
	 * 修改tag属性(包含名称和颜色)
	 */
	@ResponseBody
	@RequestMapping(value="updateTagProperty",method=RequestMethod.PUT)
	@ApiOperation(value = "修改tag属性(包含名称和颜色)")
	@ApiImplicitParams(value={
		@ApiImplicitParam(name="tagId",value="修改的tagid",required=true,dataType="int",paramType="query"),
		@ApiImplicitParam(name="tagName",value="改名后的tagName,不修改该属性可以不写",dataType="string",paramType="query"),
		@ApiImplicitParam(name="color",value="tag的颜色,不修改该属性可以不写",dataType="string",paramType="query")
	})
	public JSONObject updateTagName(int tagId,String tagName,String color){
		JSONObject map = new JSONObject();
		TagBean tagBean = new TagBean();
		tagBean.setTagId(tagId);	tagBean.setTagName(tagName);	tagBean.setColor(color);
		int rows = tagService.updateTagProperty(tagBean);
		if(rows==1){
			map.put("message", "tag修改成功");
		}else{
			map.put("message", "tag修改失败");
		}
		return map;
	}
	
	/**
	 * 删除tag
	 * @param tagId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="deleteTag",method=RequestMethod.DELETE)
	@ApiOperation(value = "删除指定的tag标签")
	@ApiImplicitParam(name="tagId",dataType="int",required=true,paramType="query")
	public JSONObject deleteTag(int tagId){
		JSONObject map = new JSONObject();
		int rows = tagService.deleteTag(tagId);
		if(rows==1){
			map.put("message", "tag删除成功");
		}else{
			map.put("message", "tag删除失败");
		}
		return map;
	}
	
	/**
	 * 查询文档tag
	 * @param fid 文档id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryFileTags",method=RequestMethod.GET)
	@ApiOperation(value = "查询文档tag")
	@ApiImplicitParam(name="fid",dataType="int",required=true,paramType="query")
	public JSONObject queryFileTags(int fid){
		JSONObject map = new JSONObject();
		List<TagBean> tagBeans = tagService.queryFileTags(fid);
		map.put("data", tagBeans);
		return map;
	}
}
