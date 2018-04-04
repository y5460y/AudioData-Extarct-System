<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>大规模语音识别与标注系统</title>
</head>
<body>
<jsp:include page="Title.jsp"></jsp:include>
<div class="content">
<div class="primary">
<div class="panel panel-info">
<div class="panel-heading">
        <h3 class="panel-title">文字标注模块</h3>
 </div>
 <div id="lyricshow" style="overflow:auto; height:800px;"></div>
</div>
</div>
<div class="secondary">
<div id="MusicControl">
<div class="panel panel-info">
<div class="panel-heading">
        <h3 class="panel-title">音乐控制模块</h3>
 </div>
<div id="MusicInfo" style="margin-left: 5px;">
<p>曲目名：<span id="MusicName"></span></p>
<p>音乐家：Linbin Young</p>
<p>时间：<span id="MusicDuration"></span></p>
</div>
<div id="MusicPlay" style="margin-top: 10px; margin-left: 5px;" >
<audio id="myaudio" src=""  loop="false" controls="controls" style="width: 50%;" ></audio>
</div>
<div class="controlbutton" style="margin-top: 10px; margin-left: 5px;">
<input type="button" onclick="autoPlay()" value="播放"/>
<input type="button" onclick="StopMusic()" value="暂停"/>
<input type="button" onclick="ReRunMusic()" value="重新播放"/>
<input type="hidden" id="sentence_start_time" value="value"> 
</div>
<div class="fileinput" style="margin-top: 10px; margin-left: 5px;">
<form class="form-inline"  enctype="multipart/form-data" method="post" name="formfile">
<div class="form-group"><input type="file" id="multifile"  multiple size="80" name="audioget"></div>
<div class="form-group"><input type="button" onclick="Fileupload()"  value="上传"></div>
<div class="form-group"><input type="button" onclick="FileList()"  value="导入系统"></div>
<div class="form-group"><input type="button"  onclick="WordExtract()" value="文字提取"></div>
<div class="form-group"><input type="button"  id = "ModelClickExport" onclick="lyricload()" value="导出"></div>
<div class="form-group"><div id="result"></div></div>
</form>
</div>
</div>
</div>
<hr>
<div class="panel panel-info" id="downside_part">
<div class="panel-heading">
        <h3 class="panel-title">音乐翻译模块</h3>
 </div>
<div id="bay">
<div id="filelist">
<table class="table table-striped">
	<thead>
		<tr>
			<th>曲目名</th>
		</tr>
	</thead>
	<tbody>
    <%
     String s=new String();
    String t = null;
     t=request.getParameter("test");
     if(t!=null){
    %>
    <% 
       String [] elem = t.split(";");
    %>
    <% 
        for (int i=0;i<elem.length;i++){
        	out.print("<tr><td><span  onclick=\"SelectMusic(this)\">");
        	%><%=elem[i] %><% 
    }%>
    <%} %>
	</tbody>
</table>
</div>
<div id="lyric"></div>
</div>
</div>
</div>
</div>
<form name="form1" method="post" action="ProjectPlay.jsp" >
<input type="hidden" name="test" value=<%=s %>  />
</form>
</body>
</html>