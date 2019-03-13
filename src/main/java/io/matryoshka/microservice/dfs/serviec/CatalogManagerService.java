package io.matryoshka.microservice.dfs.serviec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.matryoshka.microservice.dfs.bean.CatalogDTO;
import io.matryoshka.microservice.dfs.bean.CatalogManagerBean;
import io.matryoshka.microservice.dfs.bean.DocManagerBean;
import io.matryoshka.microservice.dfs.bean.Resource;
import io.matryoshka.microservice.dfs.bean.SearchBean;
import io.matryoshka.microservice.dfs.bean.UserRoleCatalogBean;
import io.matryoshka.microservice.dfs.mapper.CatalogManagerMapper;
import io.matryoshka.microservice.dfs.tools.GetParameter;
import io.minio.MinioClient;

/**
 * 目录管理的service
 * 
 * @author Administrator
 *
 */
@Service
public class CatalogManagerService {

	@Autowired
	private CatalogManagerMapper mapper;
	
	List<CatalogManagerBean> newList = new ArrayList<>();

	/**
	 * 递归获取某个父机构节点下面的所有子机构节点
	 * @param childCbean 要返回的结果
	 * @param orgList  数据库查询出来的所有机构集合
	 * @param pid      父id
	 * 注:本身的机构节点不会添加进去
	 */
	public List<CatalogManagerBean> orgRecursion(List<CatalogManagerBean> childCbeans,List<CatalogManagerBean> cbeanList, int cid) {
		for (CatalogManagerBean cbean : cbeanList) {
			if (cbean.getSupDirectory() != 0) {
				//遍历出父id等于参数的id，add进子节点集合
				if (cbean.getSupDirectory() == cid) {
					//递归遍历下一级
					orgRecursion(childCbeans,cbeanList, cbean.getCid());
					//末级机构才添加进去(依自己业务定义)
					childCbeans.add(cbean);
				}
			}
		}
		return childCbeans;
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
	/**
	 * 对递归查询出来的目录集合做出处理
	 * @param cid
	 * @return
	 */
	public List<Map<CatalogManagerBean, List<DocManagerBean>>> handlerBean(List<CatalogManagerBean> cbeanList){
		// 用来存储目录和文档的集合
		List<Map<CatalogManagerBean, List<DocManagerBean>>> cdLists 
				= new ArrayList<Map<CatalogManagerBean, List<DocManagerBean>>>();
		for (CatalogManagerBean cbean : cbeanList) {
			// Key:每一个目录对象, Value:目录下的所有文件
			Map<CatalogManagerBean, List<DocManagerBean>> cdMaps 
					= new HashMap<CatalogManagerBean, List<DocManagerBean>>();
			// 通过cbean中的cid查询出所有的文档数据
			List<DocManagerBean> dbeanList = mapper.queryFilesByCid(cbean.getCid());
			// 目录和文档存储的map集合
			cdMaps.put(cbean, dbeanList);
			cdLists.add(cdMaps);
		}
		return cdLists;
	}
	
	/**
	 * 查询文件表 得到与cid相关的数据
	 * @param cid 关联的目录id
	 * @return
	 */
	public List<DocManagerBean> queryFilesByCid(int cid){
		List<DocManagerBean> dbeanList = mapper.queryFilesByCid(cid);
		return dbeanList;
	}
	
	/**
	 * 通过条件查询目录下的子目录
	 * @param bean 此处将bean对象中的某一参数作为条件,以达到动态查询效果
	 * @return
	 */
	public List<CatalogManagerBean> queryByCondition(CatalogManagerBean bean) {
		List<CatalogManagerBean> list = mapper.queryByCondition(bean);
		return list;
	}

	/**
	 * 创建目录并存储到数据库中
	 */
	public int newDirectory(CatalogManagerBean bean){
		int rows = mapper.newDirectory(bean);
		return rows;
	}

	/**
	 * 目录移动
	 * 
	 * @param srcDirId
	 *            选择移动的源目录id
	 * @param tarDirId
	 *            要移动到的目录下的id
	 * @return
	 */
	public int moveCatalog(
			CatalogManagerBean mainCbean,
			List<CatalogManagerBean> childCbeans,
			int d_value,
			String descPath,
			int tarRank) {
		int rows = 0;
		for (CatalogManagerBean childBean : childCbeans) {
			if(mainCbean.getCid()!=childBean.getCid()){
				String srcPath = GetParameter.subAppointDir(mainCbean.getRank(), childBean.getPath());
				
//				System.out.println("descpath="+descPath+srcPath);
				childBean.setPath(descPath+srcPath);
				childBean.setRank(childBean.getRank()+d_value);
				mapper.updateSubCatalog(childBean);
			}else{
				String srcPath = GetParameter.subAppointDir(mainCbean.getRank(), mainCbean.getPath());
//				System.out.println(srcPath);
				mainCbean.setPath(descPath+srcPath);
			}
		}
		mainCbean.setRank(tarRank+1);
		rows = mapper.moveCatalog(mainCbean);
		return rows;
	}

	/**
	 * 通过目录名查询对应的这条数据,目录名是唯一值
	 * 
	 * @param catalogName
	 *            目录名
	 * @return 通过目录名称查询出来的数据
	 */
	public CatalogManagerBean queryByCatalogName(String catalogName) {
		CatalogManagerBean data = mapper.queryByCatalogName(catalogName);
		if (data == null) {
			System.out.println(catalogName + "目录不存在...");
		}
		return data;
	}

	/**
	 * 删除指定节点的目录以及下面的子目录
	 * 
	 * @param cid
	 *            要删除的目录节点id
	 * @param minioClient
	 *            minio服务
	 * @return 执行此操作数据库受影响的行数
	 * @throws Exception
	 */
	public boolean delCatalogByCids(List<CatalogManagerBean> cbeanList, MinioClient minioClient) throws Exception {
		
		// 找出集合中的所有目录id
		List<Integer> cidsList = new ArrayList<Integer>();
		for (CatalogManagerBean cbean : cbeanList) {
			cidsList.add(cbean.getCid());
		}
		
		// 进行文件删除操作
		boolean isDelDocSuccess = mapper.delDocByCids(cidsList);
		// 进行目录删除操作
		boolean isDelCatalogSuccess = mapper.delCatalogByCids(cidsList); // 执行此操作数据库受影响的行数
		if (isDelDocSuccess || isDelCatalogSuccess) {
			return true;
		}
		return false;
	}
	
	/**
	 * 目录重命名
	 * @param cid 目录id
	 * @param cname 目录的重命名后的名称
	 */
	public Map<String, Object> catalogRename(int cid,String newName){
		Map<String, Object> map = new HashMap<String, Object>();
		// 修改数据库中的cname的值
		boolean isSuccess = mapper.catalogRename(cid, newName);
		if(isSuccess){
			map.put("message", "成功");
		}else{
			map.put("message", "失败");
		}
		return map;
	}
	
	
	/**
	 * 用户针对指定目录进行授权
	 * @param urcBean
	 * @return
	 */
	public int userAuthorizationForDir(UserRoleCatalogBean urcBean){
		//设置权限时保证该用户没有此权限,所有设置权限之前要进行一步删除权限操作
		mapper.deleteAuthorization(urcBean);
		int rows = mapper.userAuthorizationForDir(urcBean);
		return rows;
	}
	
	/**
	 * 根据目录的类型,查询哪些用户拥有该目录何种权限
	 * @param urcBean
	 * @return
	 */
	public int queryDirPermissions(UserRoleCatalogBean urcBean){
		int roleid = mapper.queryDirPermissions(urcBean);
		return roleid;
	}
	
	/**
	 * 全局搜索
	 * @param keyword 全局搜索的关键字
	 */
	public SearchBean globalSearch(String keyword){
		//初始化全局搜索对象
		SearchBean searchBean = new SearchBean();
		//通过关键字模糊查询文档和目录
		List<CatalogManagerBean> queryCatalogLikeBean = mapper.queryCatalogLike(keyword);
		List<DocManagerBean> queryDocLikeBean = mapper.queryDocLike(keyword);
		//将查询到的结果设置给搜索的bean对象
		searchBean.setCatalogList(queryCatalogLikeBean);
		searchBean.setDocumentList(queryDocLikeBean);
		//返回
		return searchBean;
	}
	
	/**
	 * 修改文件夹颜色 
	 * @param cid 目录id
	 * @param color 目录颜色
	 * @return 
	 */
	@Transactional
	public int modifyFolderColor(int cid,String color){
		int rows = mapper.modifyFolderColor(cid, color);
		if(rows==0){
			throw new RuntimeException("文件夹颜色修改失败");
		}
		return rows;
	}
	
	/**
	 * 通过userid查询该用户下对每个目录(包括公有)有什么角色
	 * @param userid 用户id
	 * @return 用户id和角色id的集合
	 */
	public Map<String, Object> queryCatalogAndRoleByUserid(
			int userid,
			Map<String,List<Resource>> roleidAndSourceMap // 角色id和资源的对应的集合
	){
		
		//存放所有的DTO数据,以树形结构显示
		List<CatalogDTO> list = new ArrayList<CatalogDTO>();
		//查询目录表中所有的数据
		List<CatalogDTO> dtos = mapper.queryCatalogs();
		//返回给前台的数据结构
		Map<String, Object> map = new HashMap<String, Object>();
		//存放目录id和资源的map
		Map<Integer, List<Resource>> cidAndResourceMap = new HashMap<Integer, List<Resource>>();
		//角色id和资源的集合中取出所有的角色id
		Set<String> roleidsMap = roleidAndSourceMap.keySet();
		//集合(用户id 角色id 目录id)
		List<UserRoleCatalogBean> userid_roleid_cid = mapper.queryCatalogAndRoleByUserid(userid);
		//遍历key为角色id的集合
		for(String roleidMap : roleidsMap){
			//遍历 (用户id 角色id 目录id) 集合
			for(UserRoleCatalogBean urcBean : userid_roleid_cid){
				//判断如果两个集合中的角色id相等
				if(Integer.parseInt(roleidMap)==urcBean.getRoleid()){
					cidAndResourceMap.put(urcBean.getCid(), roleidAndSourceMap.get(roleidMap));
				}
			}
		}
			
		//遍历查询的所有目录DTO
		for (CatalogDTO dto : dtos) {
			dto.setUserid(userid_roleid_cid.get(0).getUserid());
			//如果是最外层DTO目录
			if (dto.getRank() == 1) {
				//递归查询其下的子目录,并添加到父DTO目录中
				list.add(tree(dto, dtos,cidAndResourceMap));
			}
		}
		map.put("data", list);
		return map;
	}
	
	/**
	 * 递归找子节点
	 * @param parentNode  父类节点
	 * @param dtos 查询出来的所有目录
	 * @return
	 */
	public CatalogDTO tree(
			CatalogDTO parentNode, // 表示父节点
			List<CatalogDTO> dtos, // 里面包含所有数据,从中取出父级对应的子级
			Map<Integer, List<Resource>> cidAndSourceMap // 里面是目录id对应的资源集合
			) {
		//获得目录id对应资源Map中的cid
		Set<Integer> cids = cidAndSourceMap.keySet();
		//遍历cid的集合
		for(Integer cid: cids){
			//判断资源map中的目录id和父节点中一致的目录,将资源设置给该目录
			if(parentNode.getCid()==cid){
				parentNode.setResources(cidAndSourceMap.get(cid));
			}
		}
		//遍历所有的数据,找到对应的子节点
		for (CatalogDTO childNode : dtos) {
			//查找 "父节点id" 等于 "子节点父级id" 的数据
			if (parentNode.getCid() == childNode.getSupDirectory()) {
				//防止空指针 , 进行初始化
				if (parentNode.getZcatalogChild() == null) {
					parentNode.setZcatalogChild(new ArrayList<CatalogDTO>());
				}
				//父节点递归赋值
				parentNode.getZcatalogChild().add(tree(childNode, dtos,cidAndSourceMap));
			}
		}
		//返回父节点
		return parentNode;
	}
	
	
	
	/**
	 * src目录复制到desc目录
	 * @param srcid	开始目录
	 * @param descid 目标目录
	 */
	public void copyCatalog(int srcid,int descid){
		//通过源目录id查询该目录对象
		CatalogManagerBean srcObj = mapper.queryCatalogByCid(srcid);
		//假设目录层级和父级目录已经作好修改,新增该目录到数据库
		mapper.newDirectory(srcObj);
		//此时srcObj的主键返回,然后通过srcid查询文档表
		List<DocManagerBean> docs = mapper.queryFilesByCid(srcid);
		//循环遍历docs,修改cid为srcObj.getCid(),插入数据库
//		for(....)
		//通过srcid查询目录表
//		List<CatalogManagerBean> list = mapper.queryByCondition(new CatalogManagerBean().setSupDirectory(srcid));
/*		for(CatalogManagerBean l : list){
			List<CatalogManagerBean> list2 = mapper.queryByCondition(new CatalogManagerBean().setSupDirectory(l.cid));
			if(list2==null || list2.size()==0){
			    直接将l插入数据库,父级目录是srcid
			}else{
			     先执行新增操作,父级目录是srcid,得到主键:lCid
			  for(CatalogManagerBean l2 : list){
			     List<CatalogManagerBean> list3 = 
			     		mapper.queryByCondition(new CatalogManagerBean().setSupDirectory(l2.cid));
			     if(list3==null || list3.size()==0){
			     	直接将l2插入数据库,父级目录是lCid
			     }else{
			      	先执行新增操作,父级目录是srcid,得到主键:lC2id
			     	 for(CatalogManagerBean l3 : list){
			     	 	  List<CatalogManagerBean> list4 = 
			     				mapper.queryByCondition(new CatalogManagerBean().setSupDirectory(l3.cid));
			     	 }
			     }
			  }
			}	
		}
*/
	}
}
