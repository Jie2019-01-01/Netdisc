package io.matryoshka.microservice.dfs.serviec;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.mapper.DocManagerMapper;
import io.matryoshka.microservice.dfs.tools.GetParameter;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import net.sf.json.JSONObject;

/**
 * 文档管理的service
 * @author Administrator
 *
 */
@Service
public class DocManagerService {

	@Value("${minio.url}")
	private String url; // 端点URL对象
	@Value("${minio.accessKey}")
	private String accessKey; // 同用户ID,可以唯一标识帐户
	@Value("${minio.secretKey}")
	private String secretKey;// secretKey帐户的密码
	
	@Autowired
	private DocManagerMapper mapper;
	
	/**
	 * @param bean	文档对象
	 * @param minioClient minio客户端
	 * @return 文档新增的结果信息
	 */
	@Transactional
	public String newDocument(DocManagerBean bean,MultipartFile fileName){
		try{
			//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象...	
			MinioClient minioClient = new MinioClient(url, accessKey, secretKey);
			// 通过cid查询到这个目录
			CatalogManagerBean selectCidBean = mapper.queryCatalogByCid(bean.getCid());
			if(selectCidBean==null){
				return "无效的文件夹";
			}
			// 从查询的结果中拿到要用的参数
			String bucket = selectCidBean.getPath().substring(0, selectCidBean.getPath().indexOf("/")); // 主目录
			String objectName = selectCidBean.getPath().substring(selectCidBean.getPath().indexOf("/")+1);// 去除主目录后的路径 
			//文件数据插入数据库
			int rows = mapper.newDocument(bean);
			if(rows>0){
				minioClient.putObject(bucket, objectName+bean.getFname(), fileName.getInputStream(),"application/octet-stream");
				return "success";
			}else{
				return "文件数据有误";
			}	
		}catch(Exception e){
			return "文件上传失败";
		}
	}
	
	/**
	 * 查询文件名是否重复
	 * @param cname 目录名称
	 * @param fname 文件名称
	 * @return 0 没有重复 , 非0 重复
	 */
	public String isFileRepeat(String cname, String fname){
		// 对参数进行非空判断
		if("".equals(cname) || cname==null){
			return "目录名不能为空:DocManagerService 62";
		}
		if("".equals(fname) || fname==null){
			return "文件名不能为空:DocManagerService 65";
		}
		// 执行查询操作返回查询结果
		int count = mapper.isFileRepeat(cname, fname);
		if(count>0){
			return "文件名重复:"+fname+" DocManagerService 70";
		}
		return "0";
	}
	
	/**
	 * 批量删除文档
	 * @param fname 文档名称
	 * @param cid 目录id
	 * @return
	 */
	@Transactional
	public String delDocmanage(int fids[], int cid) throws Exception{
		//创建Minio客户端对象	
		MinioClient minioClient = new MinioClient(url, accessKey, secretKey);
		
		// 通过cid查询目录数据
		CatalogManagerBean cbean = mapper.queryCatalogByCid(cid);
		if(cbean==null){
			return "该目录不存在,无法删除文档!";
		}
		//遍历fid集合
		for (int i = 0; i < fids.length; i++) {
			int fid = fids[i];
			//通过fid查询文档表,取出文档名
			DocManagerBean delBean = mapper.getDocBean(fid);
			//删除数据库中的文档记录
			int rows = mapper.delDocmanage(fid, cid);
			if(rows>0){
				// 获取目录的层级结构
				String path = cbean.getPath();
				String bucket = path.substring(0,path.indexOf("/"));
				String objectName = path.substring(path.indexOf("/"));
				// minio中的删除: 		bucket + objectName	+ fileName
				minioClient.removeObject(bucket, objectName+delBean.getFname());
			}else{
				return "第"+(i+1)+"条文档不存在,无法删除";
			}
		}
		return "success";
	}
	
	/**
	 * 文档下载
	 * @param response 响应
	 * @param cid	目录id
	 * @param fileName	文件名
	 */
	public boolean downloadDocument(int cid,String fileName){
		//实例化response对象
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		
		MinioClient minioClient;
		try {
			//设置响应头和客户端保存文件名
		    response.setCharacterEncoding("utf-8");
		    response.setContentType("multipart/form-data");
		    response.addHeader("Content-Disposition", "attachment;fileName="+URLEncoder.encode(fileName,"UTF-8"));
		    //创建Minio客户端对象
			minioClient = new MinioClient(url, accessKey, secretKey);
			// 通过cid查询目录的数据
			CatalogManagerBean cbean = mapper.queryCatalogByCid(cid);
			String bucket = cbean.getPath().substring(0,cbean.getPath().indexOf("/"));
			String objectName = cbean.getPath().substring(cbean.getPath().indexOf("/"));
			// 获取minio下载的输入流
			InputStream stream = minioClient.getObject(bucket, objectName+fileName);
			//激活下载操作,输出到浏览器
		     OutputStream out = response.getOutputStream();
			 byte[] buf = new byte[2048];
			 int len = -1;
			 while ((len = stream.read(buf)) != -1) {
			   out.write(buf, 0, len);
			 }
			 // 资源关闭
			 out.flush();
			 out.close();
			 stream.close();
		} catch (Exception e) {
			return false;
		} 
		
		return true;
	}
	
	/**
	 * 查询文档表,通过fid查询文档名
	 * @param fid 文档的id
	 * @return fid查询出来的文档名称,没有与之相对应的则返回空
	 */
	public DocManagerBean getDocBean(int fid){

		DocManagerBean bean = mapper.getDocBean(fid);
		return bean;
	}
	
	/**
	 * 查询指定目录下的文档信息
	 * @param cname 目录名
	 * @return 文档的数据
	 */
	public JSONObject queryDocuments(int cid){
		JSONObject map = new JSONObject();
		// 查询文档数据
		List<DocManagerBean> list = mapper.queryDocuments(cid);
		if(list.size()==0){
			map.put("message", "查询结果为空  DocManagerService:132");
			return map;
		}
		
		map.put("data", list);
		return map;
	}
	
	/**
	 * 移动文件
	 * @param cid 移动到的目标目录id
	 * @param fid 移动的文件id
	 * @return
	 */
	@Transactional
	public boolean moveFile(int cid,int descCid,int fid){
		try{
			//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象	
			MinioClient minioClient = new MinioClient(url, accessKey, secretKey);
			
			// 通过fid查询被移动文件的信息
			DocManagerBean moveFileBean = mapper.getDocBean(fid);
			String moveFileName = moveFileBean.getFname();//文件名
			
			// 通过目录id查询源文件的位置
			CatalogManagerBean moveBean = mapper.queryCatalogByCid(cid);
			String cpath = moveBean.getPath();//源文件的路径
			String bucket = GetParameter.subPathMainDir(cpath);
			String object = GetParameter.subPathExMainDir(cpath);
			String objectName = object+moveFileName;
			
			// 通过目标id查询目录数据
			CatalogManagerBean descBean = mapper.queryCatalogByCid(descCid);
			String descPath = descBean.getPath();//目标路径
			String descBucket = GetParameter.subPathMainDir(descPath);
			String descObject = GetParameter.subPathExMainDir(descPath);
			String descObjectName = descObject+moveFileName;
			
			boolean isMoveFileSuccess = mapper.moveFile(descCid, fid);
			if(isMoveFileSuccess){
				//复制到服务端
				minioClient.copyObject(bucket, objectName, descBucket, descObjectName);
				//删除源文件
				minioClient.removeObject(bucket, objectName);
			}
			return isMoveFileSuccess;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 文件复制
	 */
	@Transactional
	public boolean copyFile(int cid,int descCid,int fid){
		try{
			//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象	
			MinioClient minioClient = new MinioClient(url, accessKey, secretKey);
			
			// 通过fid查询要复制文件的信息
			DocManagerBean copyFileBean = mapper.getDocBean(fid);
			//给复制过来的文件设置目录id
			copyFileBean.setCid(descCid);
			//文件名
			String copyFileName = copyFileBean.getFname();
			
			// 通过目录id查询源文件的位置
			CatalogManagerBean moveBean = mapper.queryCatalogByCid(cid);
			String cpath = moveBean.getPath();
			String bucket = GetParameter.subPathMainDir(cpath);
			String object = GetParameter.subPathExMainDir(cpath);
			String objectName = object+copyFileName;
			
			// 通过目录id查询目标位置
			CatalogManagerBean descBean = mapper.queryCatalogByCid(descCid);
			String descPath = descBean.getPath();
			String descBucket = GetParameter.subPathMainDir(descPath);
			String descObject = GetParameter.subPathExMainDir(descPath);
			String descObjectName = descObject+copyFileName;
			
			//复制文件,所以在数据库中应该多出一条该记录
			int rows = mapper.newDocument(copyFileBean);
			if(rows>0){
				//在minio中进行复制
				minioClient.copyObject(bucket, objectName, descBucket, descObjectName);
				return true;
			}
		}catch(Exception e){
			return false;
		}
		
		return false;
	}
	
	/**
	 * 文件重命名
	 * @param fid 重命名操作的文件id
	 * @param renameFile 重命名之后的名称
	 * @return
	 */
	@Transactional
	public boolean renameFile(int cid,int fid,String rename){
		try{
			//使用给定的URL对象，访问密钥和密钥创建Minio客户端对象	
			MinioClient minioClient = new MinioClient(url, accessKey, secretKey);
			
			// 通过fid查询要复制文件的信息
			DocManagerBean fileBean = mapper.getDocBean(fid);
			String fileName = fileBean.getFname();//文件名
			
			// 通过目录id查询文件所在位置
			CatalogManagerBean renameBean = mapper.queryCatalogByCid(cid);
			String cpath = renameBean.getPath();
			
			// 存储桶
			String bucket = GetParameter.subPathMainDir(cpath);
			// 存储桶中的对象
			String object = GetParameter.subPathExMainDir(cpath);
			// 存储桶中的对象+文件名
			String objectName = object+fileName;
			
			// 获取存储桶中的对象输入流
			InputStream in = minioClient.getObject(bucket, objectName);
			
			boolean isRenameSuccess = mapper.renameFile(fid, rename);
			//数据库中重命名成功后,开始同步数据到minio
			if(isRenameSuccess){
				minioClient.putObject(bucket, object+rename, in, "application/octet-stream");
				minioClient.removeObject(bucket, objectName);
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	
	/**
	 * 通过cid查询目录中对应的数据
	 * @param cid 目录id
	 * @return
	 */
	public CatalogManagerBean queryCatalogByCid(int cid){
		CatalogManagerBean bean = mapper.queryCatalogByCid(cid);
		return bean;
	}
}
