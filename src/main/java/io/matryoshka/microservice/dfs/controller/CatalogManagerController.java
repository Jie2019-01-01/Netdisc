package io.matryoshka.microservice.dfs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import io.matryoshka.microservice.dfs.bean.CatalogDTO;
import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.Resource;
import io.matryoshka.microservice.dfs.bean.SearchBean;
import io.matryoshka.microservice.dfs.bean.UserRoleCatalogBean;
import io.matryoshka.microservice.dfs.serviec.CatalogManagerService;
import io.matryoshka.microservice.dfs.tools.GetParameter;
import io.matryoshka.microservice.dfs.tools.NameValidation;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * 目录管理的handler
 * @author Administrator
 *
 */
@Controller
@Api("目录管理的后台")
@RestSchema(schemaId = "catalog")
@RequestMapping(path="/")
public class CatalogManagerController {

	@Value("${minio.url}")
	private String url; // 端点URL对象
	@Value("${minio.accessKey}")
	private String accessKey; // 同用户ID,可以唯一标识帐户
	@Value("${minio.secretKey}")
	private String secretKey;// secretKey帐户的密码
	
	@Autowired
	private CatalogManagerService service;
	
	/**
	 * 在指定位置创建目录
	 * supCatalog:设置要存储的上级目录,没指定该值代表创建的是一级目录
	 * newCatalog:将要创建的目录,supCatalog没有指定该参数就是一级目录
	 * isHide: 是否为隐藏目录 0不隐藏 1隐藏
	 * isHide: 目录颜色
	 * range:  可见范围
	 */
	@ResponseBody
	@RequestMapping(value="newCatalog",method=RequestMethod.POST)
	@ApiOperation(value = "新建目录")
	public Map<String,Object> newCatalog(
			@ApiParam(value="所在位置的目录id,如果是最上级目录需要将这个参数设为0",name="supId",required=true)int supId, 
			@ApiParam(value="目录名称",name="newCatalog",required=true)String newCatalog,
			@ApiParam(value="该目录是否隐藏: 不隐藏输入0 , 隐藏输入1",name="isHide",required=true)int isHide,
			@ApiParam(value="目录颜色",name="color",required=true)String color,
			@ApiParam(value="目录可见范围:公开/私有",name="range",required=true)String range)
	{		
		Map<String,Object> map = new HashMap<String,Object>();
		//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象	
		MinioClient minioClient;
		try {
			minioClient = new MinioClient(url, accessKey, secretKey);
			CatalogManagerBean bean = new CatalogManagerBean();
			
			// 判断创建的目录是否含有中文,如果目录含有中文,随机生成10位随机数(字母+数字的组合)
			boolean isContainChinese = NameValidation.isContainChinese(newCatalog);
			String mapperDirName = isContainChinese?NameValidation.mapperDirName():newCatalog;
			
			// 创建的目录是否为一级目录
			if(supId==0){
				// 检测该目录是否存在
				boolean isExist = minioClient.bucketExists(mapperDirName);
				if(isExist){
					map.put("message", "该目录已存在,不能再创建!");
					return map;
				}else{
					//进行新增操作
					bean.setCname(newCatalog);          bean.setCreateuser("kuuyee");
					bean.setMapperName(mapperDirName);  bean.setFlag(0);bean.setRank(1);
					bean.setPath(mapperDirName+"/");    //bean.setCpath(newCatalog+"/");
					bean.setIshide(isHide);             bean.setRange(range);
					bean.setColor(color);
					
					int rows = service.newDirectory(bean);
					if(rows>0){
						//同步到minio
						minioClient.makeBucket(mapperDirName);
						map.put("message", "数据插入成功");
						return map;
					}else{
						map.put("message", "数据插入失败");
						return map;
					}	
				}
			}else{
				
				// 如果新建的不是一级目录,那么就通过所在目录的id查询所有目录的数据
				CatalogManagerBean conditionBean = new CatalogManagerBean();
				conditionBean.setCid(supId); //设置查询条件
				CatalogManagerBean supBean = service.queryByCondition(conditionBean).get(0);

				//进行新增操作
				bean.setCname(newCatalog);      bean.setCreateuser(accessKey);
				bean.setSupDirectory(supId);    bean.setMapperName(mapperDirName.toLowerCase());
				bean.setFlag(0);                bean.setRank(supBean.getRank()+1);
				bean.setIshide(isHide);         bean.setColor(color);
				bean.setPath(supBean.getPath()+mapperDirName+"/");//设置新建的目录中path
				//bean.setCpath(supBean.getCpath()+newCatalog+"/"); //设置新建的目录中cpath
				bean.setRange(range);
				
				int rows = service.newDirectory(bean);
				// 数据插入成功判断
				if(rows>0){
					// 此处不需要同步到minio , 当用户上传文件是指定该目录时,跟随文件一起同步到minio中
					map.put("message", "数据插入成功");
					return map;
				}else{
					map.put("message", "数据插入失败");
					return map;
				}
			}
		} catch (Exception e) {
			map.put("message", "目录名称不符合标准");
			return map;
		} 
	}
	
	/**
	 * 根据目录id删除该目录下的数据
	 * @param cid 删除的目录id
	 * @return
	 */
	@ResponseBody
	@DeleteMapping(value="/delCatalogByCid")
	@ApiOperation(value = "删除目录")
	public Map<String, Object> delCatalogByCid(
			@ApiParam(value="删除的目录id",name="cid",required=true)int cid
			) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象	
		MinioClient minioClient = new MinioClient(url, accessKey, secretKey);

		// 通过cid查询这个要删除目录的数据
		CatalogManagerBean delBean = service.queryCatalogByCid(cid);
		
		//递归查询出所有目录数据
		List<CatalogManagerBean> cbeans = service.queryByCondition(null);
		List<CatalogManagerBean> childCbeans = new ArrayList<>();
		List<CatalogManagerBean> cbeanList = service.orgRecursion(childCbeans,cbeans,cid);
		cbeanList.add(0,delBean);
	
/**************************/
		String bucketName = GetParameter.subPathMainDir(delBean.getPath());
		Iterable<Result<Item>> bucketObjectsIterable = minioClient.listObjects(bucketName);
		// 判断要删除的目录是否为minio中的存储桶
		if(delBean.getSupDirectory()==0){
			if(bucketObjectsIterable!=null){
				for (Result<Item> result : bucketObjectsIterable) {
//					System.out.println(result.get().objectName());
					minioClient.removeObject(bucketName, result.get().objectName());
				}
			}
			minioClient.removeBucket(bucketName);
		}else{
			if(bucketObjectsIterable!=null){
				for (Result<Item> result : bucketObjectsIterable) {
					if(result.get().objectName().contains(delBean.getMapperName())){
//						System.out.println(result.get().objectName());
						minioClient.removeObject(bucketName, result.get().objectName());
					}
				}
			}
		}
		
/******************************/		
		
		// 执行数据库删除
		boolean isDelSuccess = service.delCatalogByCids(cbeanList, minioClient);
		if(isDelSuccess){
			map.put("message", "删除成功...");
		}else{
			map.put("message", "删除失败...CatalogManagerController 183");
		}
		
		return map;
	}
	
	/**
	 * 目录重命名
	 * @param cid 重命名的目录id
	 * @param cname 目录新名称
	 */
	@ResponseBody
	@PutMapping(value="/catalogRename")
	@ApiOperation(value = "目录重命名")
	public Map<String , Object> catalogRename(
			@ApiParam(value="目录id",name="cid",required=true)int cid,
			@ApiParam(value="目录新名称",name="newName",required=true)String newName){
		//通过cid查询到要重命名的目录数据	
		Map<String, Object> map = service.catalogRename(cid, newName);
//		Map<String, Object> map = service.catalogRename(cid, newName,renameAfterCpath);
		return map;
	}
	
	/**
	 * 给某个用户针对指定目录授予角色
	 * @param userid 用户id
	 * @param roleid 目录id
	 */
	@ResponseBody
	@PostMapping(value="/userAuthorizationForDir")
	@ApiOperation(value = "给某个用户针对指定目录授予角色")
	public Map<String,Object> userAuthorizationForDir(
			@ApiParam(value="角色id",name="roleid",required=true)int roleid,
			@ApiParam(value="目录id",name="cid",required=true)int cid){
		Map<String,Object> map = new HashMap<String,Object>();
		UserRoleCatalogBean urcBean = new UserRoleCatalogBean();
		urcBean.setRoleid(roleid);		urcBean.setCid(cid);
		//执行新增权限的操作
		int rows = service.userAuthorizationForDir(urcBean);
		if(rows>0){
			map.put("message", "权限设置成功");
		}else{
			map.put("message", "权限设置失败");
		}
		return map;
	}
	
	/**
	 * 查询用户对指定目录有什么角色
	 * @param userid 用户id
	 * @param cid 目录id
	 * @return
	 */
	@ResponseBody
	@GetMapping(value="/queryDirPermissions")
	@ApiOperation(value = "查询用户对指定目录有什么角色")
	public Map<String,Object> queryDirPermissions(
			@ApiParam(value="userid",name="用户id",required=true)int userid,
			@ApiParam(value="cid",name="目录id",required=true)int cid){
		Map<String,Object> map = new HashMap<String,Object>();
		//权限控制对象的初始化赋值,作为后续传递的参数
		UserRoleCatalogBean urcBean = new UserRoleCatalogBean();
		urcBean.setCid(cid);urcBean.setUserid(userid);
		//通过目录id和用户id查询该用户对象该目录有什么样的权限
		int roleid = service.queryDirPermissions(urcBean);
		map.put("data", roleid);
		return map;
	}
	
	/**
	 * 全局搜索
	 * @param keyword 全局搜索的关键字
	 */
	@ResponseBody
	@GetMapping(value="/globalSearch")
	@ApiOperation(value = "全局搜索")
	public Map<String,Object> globalSearch(
			@ApiParam(value="keyword",name="全局搜索的关键字",required=true)String keyword
			){
		Map<String,Object> map = new HashMap<String,Object>();
		SearchBean searchBean = service.globalSearch("%"+keyword+"%");
		map.put("data", searchBean);
		return map;
	}
	
	/**
	 * 修改文件夹颜色 
	 * @param cid 目录id
	 * @param color 目录颜色
	 * @return 
	 */
	@ResponseBody
	@PutMapping(value="/modifyFolderColor")
	@ApiOperation(value="修改文件夹颜色 ")
	public Map<String,Object> modifyFolderColor(
			@ApiParam(value="cid",name="目录id",required=true)int cid,
			@ApiParam(value="color",name="目录颜色",required=true)String color){
		Map<String,Object> map = new HashMap<String,Object>();
		int rows = service.modifyFolderColor(cid, color);
		if(rows==1){
			map.put("message", "文件夹颜色修改成功");
		}
		return map;
	}
	
	/**
	 * 返回所有目录,树形结构显示(包含资源标记)
	 * @return
	 */
	@ResponseBody
	@GetMapping(value="/queryCatalogs")
	@ApiOperation("返回所有目录,树形结构显示(包含权限标记)")
	public Map<String, Object> queryCatalogs(
			@ApiParam(value="用户id",name="userid",required=true)int userid,
			@RequestBody @ApiParam(value="角色id和资源路径")Map<String, List<Resource>> roleidAndSourcesMap
			){
		Map<String, Object> map = service.queryCatalogAndRoleByUserid(userid,roleidAndSourcesMap);
		return map;
	}
}
