package io.matryoshka.microservice.dfs.tools;

import org.apache.commons.lang.StringUtils;

/**
 * 获取参数的工具类
 *
 */
public class GetParameter {

	/**
	 * 获取文件转换之后的大小
	 * @param size 文件大小 单位是bit
	 * @return
	 */
	public static String getPrintSize(long size) {
		//如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		//如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		//因为还没有到达要使用另一个单位的时候
		//接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			//因为如果以MB为单位的话，要保留最后1位小数，
			//因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + "MB";
		} else {
			//否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + "GB";
		}
	}
	
	/**
	 * 从path路径中截取minio存储区的目录名
	 */
	public static String subPathMainDir(String path){
		String mainDir = "";
		if(path!=null){
			mainDir = path.substring(0,path.indexOf("/"));
		}
		return mainDir;
	}
	/**
	 * 从path路径中截取排除minio存储区的目录一直到文件的路径
	 */
	public static String subPathExMainDir(String path){
		String exMainDir = "";
		if(path!=null){
			exMainDir = path.substring(path.indexOf("/")+1);
		}
		return exMainDir;
	}
	public static String subAppointDir(int rank ,String path){
		String srcPath;
		if(rank-1>0){
			srcPath = path.substring(StringUtils.ordinalIndexOf(path, "/", rank-1)+1);
		}else{
			srcPath = path.substring(0);
		}
		return srcPath;
	}
}
