package io.matryoshka.microservice.dfs.test.bean;

public class SourceBean {

	private String sourcePath;//资源路径

	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	@Override
	public String toString() {
		return "SourceBean [sourcePath=" + sourcePath + "]";
	}
}
