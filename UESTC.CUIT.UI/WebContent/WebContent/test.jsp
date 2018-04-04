<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="scripts/test.js"></script>
<script type="text/javascript" src="./scripts/getHTTPObject.js"></script>
 <link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">
 <link rel="stylesheet" type="text/css" href="./css/Main.css"/>  
 <script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
 <script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<span id="MusicName">2.wav</span>
<audio id="myaudio" src="Source/2.wav"  loop="false" controls="controls" style="width: 50%;" ></audio><br>
<input type="button" value="加载文字" onclick="SYStime()">
<div id="contents">
</div>
<div id="lyric">
</div>
</body>
</html>