package io.matryoshka.microservice.dfs.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.api.client.http.HttpResponse;

import io.matryoshka.microservice.dfs.tools.GetParameter;
import io.matryoshka.microservice.dfs.tools.NameValidation;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

public class MinioTest {

	public static void main(String[] args) throws Exception{
//		MinioClient minioClient = new MinioClient("http://123.56.25.48:7090", "kuuyee", "Abcd1234!");
		MinioClient minioClient = new MinioClient("https://play.minio.io:9000", "Q3AM3UQ867SPQQA43P2F",
				"zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
//		boolean isExist = minioClient.bucketExists("sub");
//		if(isExist){
////			minioClient.putObject("test", "l/a.jar", new FileInputStream("D:\\Svn\\a.jar"), "string");
			minioClient.putObject("h1sptzjvof", "hello/world/a.jar", "D:\\test.txt");
//		}else{
//			System.out.println("test 不存在");
//		}	
//		System.out.println(NameValidation.mapperDirName());
//		String reg = "(?![0-9]+$)[0-9a-z-]{3,}+";
//		String str = "啊";
//		System.out.println(str.matches(reg));
//		File file = new File("D:\\Study\\CentOS-7-x86_64-DVD-1804.iso");
//		long size = file.length();
//		
//		System.out.println(GetParameter.getPrintSize(size));
//		minioClient.removeObject("ki2wdehyod","/SUV/");
		ServletRequestAttributes servletRequestAttributes = 
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		String catalog = "h1sptzjvof"; 
		String fileName = "/sub-01/webstrom破解码.txt";
		InputStream stream = minioClient.getObject(catalog, fileName, 155L);
		//激活下载操作
		OutputStream out = response.getOutputStream();
		byte[] buf = new byte[2048];
		int len = -1;
		while ((len = stream.read(buf)) != -1) {
		  out.write(buf, 0, len);
		}
		 out.close();
		 stream.close();
		
//		String url = minioClient.getObjectUrl("test", "QeWeb.java");
//		System.out.println(url);
//		minioClient.copyObject("ki2wdehyod", "SUV/edplaoz2mf/mian.html","feagj8txbe", "edplaoz2mf/main.html");
		
//		InputStream in = minioClient.getObject("ki2wdehyod", "owci6bv4pl/QeWeb.java");
//		minioClient.putObject("ki2wdehyod", "owci6bv4pl/abc.java", in, "application/octet-stream");
		
//		String bucketName = "test";
//		Iterable<Result<Item>> bucketObjectsIterable = minioClient.listObjects(bucketName);
//		Iterator<Result<Item>> bucketObjectsIterator = bucketObjectsIterable.iterator();
//		Result<Item> bucketObjectResult = bucketObjectsIterator.next();
//		for (Result<Item> result : bucketObjectsIterable) {
//			minioClient.removeObject(bucketName, result.get().objectName());
//			if(result.get().objectName().contains("s/")){
//				minioClient.removeObject(bucketName, result.get().objectName());
//			}
//		}
//		minioClient.removeBucket(bucketName);
	}
	
}
