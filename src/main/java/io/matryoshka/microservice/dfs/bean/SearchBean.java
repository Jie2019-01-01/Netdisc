package io.matryoshka.microservice.dfs.bean;

import java.util.List;

/**
 * 全局搜索设置属性的bean
 * @author Administrator
 *
 */
public class SearchBean {
	
	List<CatalogManagerBean> catalogList;//搜索出来的目录集合
	List<DocManagerBean> documentList;//搜索出来的文档集合
	public List<CatalogManagerBean> getCatalogList() {
		return catalogList;
	}
	public void setCatalogList(List<CatalogManagerBean> catalogList) {
		this.catalogList = catalogList;
	}
	public List<DocManagerBean> getDocumentList() {
		return documentList;
	}
	public void setDocumentList(List<DocManagerBean> documentList) {
		this.documentList = documentList;
	}
}
