<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<input type="button" value="打印" onclick="printMsg();"/>
</body>
<script type="text/javascript">
	function printMsg(){
		window.location.href = "PrintServlet";
	}
</script>
</html>