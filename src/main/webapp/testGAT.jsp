<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>高安屯接口测试</title>
</head>
<body>
	<!-- <form action="uploadFiles" method="post" enctype="multipart/form-data">
		cid:<input type="text" name="cid"><br>
		color:<input type="text" name="color"><br>
		文件:<input type="file" name="fileNames"><br> 
		文件:<input type="file" name="fileNames"><br>
		文件:<input type="file" name="fileNames"><br>
		<input type="submit">
	</form>-->
	<!-- <form action="downloadDocument" method="get"> -->
	<form action="download" method="get">
		目录:<input type="text" name="cid"><br>
		文件1:<input type="text" name="fileNames"><br>
		文件2:<input type="text" name="fileNames"><br>
		文件3:<input type="text" name="fileNames"><br>
		<input type="submit">
	</form>
</body>
</html>