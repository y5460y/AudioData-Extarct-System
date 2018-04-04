<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>大规模语音识别与标注系统</title>
   <link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">  
   <link rel="stylesheet" type="text/css" href="./css/Main.css"/>
   <script type="text/javascript" src="./scripts/Main.js"></script>
   <script type="text/javascript" src="./scripts/getHTTPObject.js"></script>
   <script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
   <script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<nav id="navbar-example" class="navbar navbar-default navbar-static" role="navigation">
   <div class="navbar-header">
      <a class="navbar-brand" href="ProjectPlay.jsp">大规模语音识别与标注系统</a>
   </div>
   <div class="collapse navbar-collapse bs-js-navbar-scrollspy">
      <ul class="nav navbar-nav">
         <li><a href="ProjectBackground.jsp">项目背景</a></li>
         <li><a href="ProjectPlay.jsp">项目演示</a></li>
         <li class="dropdown">
            <a href="#" id="navbarDrop1" class="dropdown-toggle"  data-toggle="dropdown">联系我^-^<b class="caret"></b>
            </a>
            <ul class="dropdown-menu" role="menu"  aria-labelledby="navbarDrop1">
               <li><a href="http://www.uestc.edu.cn/" tabindex="-1">电子科技大学</a></li>
               <li><a href="https://linbinyoung.github.io/" tabindex="-1">About Me</a></li>
            </ul>
         </li>
      </ul>
       <ul class="nav navbar-nav navbar-right">
        <li><a href="#" onclick="DownLoad()"><span class="glyphicon glyphicon-log-in"></span> 数据导出</a></li>
      </ul>
   </div>
</nav>

<div id="footer">CopyRight@LinbinYoung2018</div>
<a  id="downclick" hidden="true" href="./pool/result.txt" download="来自我的系统.txt"></a>
</body>
</html>