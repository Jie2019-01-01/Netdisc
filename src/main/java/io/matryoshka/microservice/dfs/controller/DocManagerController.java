package io.matryoshka.microservice.dfs.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.serviec.DocManagerService;
import io.matryoshka.microservice.dfs.tools.GetParameter;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;

/**
 * 文档管理的handler
 * @author Administrator
 *
 */
@Controller
@Api("文档的后台管理接口")
@RequestMapping(path="/")
public class DocManagerController {

	@Autowired
	private DocManagerService service;
	
	
	/**
	 * 文档上传
	 * @param cid 指定的目录id
	 * @return 上传的信息
	 */
	@ResponseBody
	@RequestMapping(value = "uploadFile", method={RequestMethod.POST})
	@ApiOperation(value = "单个文件上传")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",defaultValue="1",value="指定的目录id",required=true,dataType="int",paramType="query"),
				}
			)
	public JSONObject uploadFile(int cid ,
			@ApiParam(value="上传的文件流",required=true) MultipartFile fileName
			){
		JSONObject map = new JSONObject();

		String fname = fileName.getOriginalFilename(); // 上传的文件名
		// 新增到数据库中的文件赋值
		DocManagerBean bean = new DocManagerBean();
		bean.setFname(fname);     bean.setCreateuser("kuuyee");
		bean.setCid(cid);         bean.setFlag(0);
		String type = fname.substring(fname.lastIndexOf("."));
		long size = fileName.getSize();
		bean.setType(type); 	  bean.setSize(GetParameter.getPrintSize(size));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		bean.setCreatedate(df.format(new Date()));
		
		// 执行文件新增操作
		String message = service.newDocument(bean,fileName);
		if("success".equals(message)){
			map.put("message", "文件上传成功");
			map.put("data", bean);
		}else{
			map.put("message", message);
		}
		return map;
	}
	
	/**
	 * 批量文档上传
	 * @param cid 指定的目录id
	 * @return 上传的信息
	 */
	@ResponseBody
	@RequestMapping(value = "uploadFiles", method={RequestMethod.POST})
	@ApiOperation(value = "批量文件上传")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",value="指定的目录id",required=true,dataType="int",paramType="query"),
				}
			)
	public JSONObject uploadFiles(
			int cid ,
			@ApiParam(value="批量上传的文件") @RequestParam(name="fileNames",required=true)MultipartFile[] fileNames 
			) throws Exception{
		
		JSONObject map = new JSONObject();
		
		List<DocManagerBean> list = new ArrayList<DocManagerBean>();
		// 取出每一个文件流
		for(int i=0;i<fileNames.length;i++){
			MultipartFile fileName = fileNames[i];
			//读取上传文件的名称
			String fname = fileName.getOriginalFilename();
			//初始化文档对象,并为其赋值
			DocManagerBean bean = new DocManagerBean();
			bean.setFname(fname); 	bean.setCreateuser("kuuyee");
			bean.setCid(cid);		bean.setFlag(0);
			String type = fname.substring(fname.lastIndexOf("."));
			bean.setType(type); 	
			long size = fileName.getSize();
			bean.setSize(GetParameter.getPrintSize(size));
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			bean.setCreatedate(df.format(new Date()));
			
			// 执行文件新增操作
			String message = service.newDocument(bean,fileName);
			if(!"success".equals(message)){
				map.put("message", message);
				return map;
			}else{
				list.add(bean);
			}
		}
		map.put("message", "文件上传成功");
		map.put("data", list);
		return map;
	}
	
	/**
	 * 删除文档(通过目录id和文档id进行删除,支持批量删除)
	 * @param fid 文件id
	 * @param cid 目录id
	 * @return message
	 */
	@RequestMapping(value="delDocmanage",method={RequestMethod.DELETE})
	@ResponseBody
	@ApiOperation(value = "删除文档(通过目录id和文档id进行删除,支持批量删除)")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="fids",value="文件id,可以传多个",required=true,allowMultiple=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="cid",defaultValue="1",value="定位到的目录id",required=true,dataType="int",paramType="query"),
				}
			)
	public JSONObject delDocmanage(int fids[],int cid) throws Exception{
		JSONObject map = new JSONObject();
		if("success".equals(service.delDocmanage(fids, cid))){
			map.put("message", "删除成功");
		}else{
			map.put("message", service.delDocmanage(fids, cid));
		}
		return map;
	}
	
	/**
	 * 文件下载
	 * @param response 设置响应信息
	 * @param cid 文件所在的目录id
	 * @param fileName 要下载文件名
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="downloadDocument" , method={RequestMethod.GET})
	@ApiOperation(value = "文件下载")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",value="文件所在的目录id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="fileNames",value="要下载文件名",allowMultiple=true,required=true,dataType="string",paramType="query"),
				}
			)
	public JSONObject downloadDocument(int cid,String fileName){
		//创建json对象
		JSONObject map = new JSONObject();
		//依次下载文件
	    boolean isDownloadSuccess = service.downloadDocument(cid, fileName);
	    //文件下载失败处理
	    if(!isDownloadSuccess){
	    	map.put("message", "文件下载失败!");
	    	return map;
	    }
		map.put("message", "文件下载成功!");
		return map;
	}
	
	/**
	 * 查询某个目录下的文档信息
	 * @param cid 查询的目录id
	 * @return 文档的数据
	 */
	@ResponseBody
	@RequestMapping(value="queryDocuments" , method={RequestMethod.GET})
	@ApiOperation(value = "查询某个目录下的文档信息")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",defaultValue="1",value="查询的目录id",required=true,dataType="int",paramType="query")
				}
			)
	public JSONObject queryDocuments(int cid){
		
		JSONObject map = service.queryDocuments(cid);
		
		return map;
	}
	
	/**
	 * 移动文件
	 * @param cid 移动的文件的原目录id
	 * @param fid 移动的文件id
	 * @param descCid 目标存储目录的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="moveFile" , method={RequestMethod.PUT})
	@ApiOperation(value = "移动文件")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",value="源目录id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="fid",value="文件id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="descCid",value="目标目录id",required=true,dataType="int",paramType="query")
				}
			)
	public JSONObject moveFile(int cid , int fid,int descCid){
		JSONObject map = new JSONObject();
		boolean isSuccess = service.moveFile(cid,descCid, fid);
		if(isSuccess){
			map.put("message", "文件移动成功");
		}else{
			map.put("message", "文件移动失败");
		}
		return map;
	}
	
	/**
	 * @param cid 复制文件的原目录id
	 * @param fid 复制文件的id
	 * @param descCid 目标目录的id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="copyFile" , method={RequestMethod.PUT})
	@ApiOperation(value = "复制文件")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",value="源目录id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="fid",value="文件id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="descCid",value="目标目录id",required=true,dataType="int",paramType="query")
				}
			)
	public JSONObject copyFile(int cid , int fid,int descCid){
		JSONObject map = new JSONObject();
		boolean isCopySuccess = service.copyFile(cid, descCid, fid);
		if(isCopySuccess){
			map.put("message", "文件复制成功");
		}else{
			map.put("message", "文件复制失败");
		}
		return map;
	}
	
	/**
	 * 文件重命名
	 * @param cid 重命名文件的上级目录id
	 * @param fid 重命名操作的文件id
	 * @param rename 重命名之后的名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="renameFile" , method={RequestMethod.PUT})
	@ApiOperation(value = "文件重命名")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="cid",value="进行重命名文件的上级目录id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="fid",value="重命名之前的文件id",required=true,dataType="int",paramType="query"),
			@ApiImplicitParam(name="rename",value="重命名之后的名称,暂时加上后缀",required=true,dataType="string",paramType="query")
				}
			)
	public JSONObject renameFile(int cid , int fid,String rename){
		JSONObject map = new JSONObject();
		boolean isSuccess = service.renameFile(cid, fid, rename);
		if(isSuccess){
			map.put("message", "文件重命名成功");
		}else{
			map.put("message", "文件重命名失败");
		}
		return map;
	}
	
	/**
	 * 通过 fid 获取文档的信息
	 * @param fid 文档id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="returnFileInfoByFid" , method={RequestMethod.PUT})
	@ApiOperation(value = "通过 fid 获取文档的信息")
	@ApiImplicitParam(name="fid",value="文档id",required=true,dataType="int",paramType="query")
	public JSONObject returnFileInfoByFid(int fid){
		JSONObject map = new JSONObject();
		DocManagerBean docBean = service.getDocBean(fid);
		map.put("data", docBean);
		return map;
	}
	
	
	
	
	
	
	
	

}
