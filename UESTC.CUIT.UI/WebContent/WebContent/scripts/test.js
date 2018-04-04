/**
 * bean test javascript
 */
function fileload(){
	console.log("fileload");
	var music_name = document.getElementById("MusicName").innerHTML;
	var audiobut = document.getElementById("myaudio");
	var lyric_area = document.getElementById("lyric");
	var htmlcode = "";
	var lrc = new Array();
	if(music_name != ""){
		console.log("Start Ajax");
		var request = getHTTPObject();
		request.onreadystatechange = function(){
			if(request.readyState == 4){
				if(request.status == 200){
					var res = request.responseText;
					var obj = eval("("+res+")");
					console.log(obj);
					htmlcode += "<table>";
					for (var key in obj){
						console.log(obj[key]);
						var inner = obj[key];
						var sentn  = inner["sentence"];
						var sentenc = sentn.split("@")[0] //sentence
						var sentenc_duration = sentn.split("@")[1].split("-")[0]; //time_duration of sentence
						htmlcode += "<tr><td align=\"center\" class = \"test\" id = \"origin\" abbr=\""+sentenc_duration+"\">"+sentenc+"</td></tr>";
						//console.log(sentenc);
						lrc.push([sentenc, sentenc_duration]);
					}// sentence loop
					htmlcode += "</table>";
					lyric_area.innerHTML = htmlcode;
					MusicControl(lrc);
				}
			}
		};
		var url = "../BackendProcess1?file="+music_name;
		url = url + "&sid=" + Math.random();
		request.open("get", url, true);
		request.send(null);
	}else{
		window.alert("Hey boy, you must first choose one music!");
	}
}

function MusicControl(lyric){
	var audiobut = document.getElementById("myaudio");
	var goal_area = document.getElementById("contents");
	var lyriclist = document.getElementsByClassName("test");
	console.log(lyric);
	console.log("log");
	audiobut.play();
	audiobut.ontimeupdate = function(e){
		for(var i=0; i<lyriclist.length;i++){
			if(audiobut.currentTime > lyriclist[i].abbr){
					AttributeClassSet(i);
			}
		}
	}
}

function AttributeClassSet(i){
	var goalyric = document.getElementsByClassName("test");
	for(var j=0; j<goalyric.length; j++){
		if(j == i){
			goalyric[j].setAttribute("id","hover");
		}else{
			goalyric[j].setAttribute("id","origin");
		}
	}
}

 function WordExport(){
		console.log("杨林彬");
		SYStime()
		var prefixtm = "<table class=\"table table-striped\"><thead><tr><th>句子</th><td>";
		var prefixtm1 = "</td></tr><tr><th>分词结果</th><th>播放</th><th>修改框</th><th>修改</th><th>保存修改</th></tr></thead><tbody>";
		var prefixtm2 = "<tr><td>";
		var playbutton ="</td><td><input type=\"button\" onclick=\"autoPlay()\" value=\"播放\"/></td><td><input type=\"text\" class=\"form-control\" id=\"name\"  disabled=\"disabled\" placeholder=\"输入修改方案\"></td><td><input type=\"button\" onclick=\"autoPlay()\" value=\"纠错\"/></td></td><td><input type=\"button\"  value=\"提交修改\"/></div></td></tr>";
		var tailhtm1 = "</tbody></table>";
		var innercontents = "";
		var music_name = "";
		var goal_area = document.getElementById("contents");
		music_name = document.getElementById("MusicName").innerHTML;
		if(music_name != ""){
			console.log("Start Ajax");
			var result = document.getElementById("contents");
			var request = getHTTPObject();
			request.onreadystatechange = function(){
				if(request.readyState == 4){
					if(request.status == 200){
						var res = request.responseText; //注意的是后端传来的数据是JSON格式，即使原来是list也需要通过 net.sf 包里的方法进行JSON数据转换
						var obj = eval("("+res+")");
						console.log(obj);
						for (var key in obj){
							innercontents +=prefixtm;
							console.log(obj[key]);
							var inner = obj[key];
							var sentn  = inner["sentence"];
							var sentenc = sentn.split("@")[0] //sentence
							innercontents += sentenc;
							innercontents +=prefixtm1;
							var sentenc_duration = sentn.split("@")[1] //time_duration of sentence
							var wordlist = inner["wordlist"];
							console.log(sentenc);
							for (var id in wordlist){
								var word_sin = wordlist[id];
								var word_word = word_sin.split("@")[0];
								innercontents +=prefixtm2;
								innercontents +=word_word;
								innercontents +=playbutton;
								var word_duration = word_sin.split("@")[1];
								console.log(word_word);
							}//word_list loop
							innercontents +=tailhtm1;
						}// sentence loop
						//here we add the result to the innerHTML
						goal_area.innerHTML = innercontents;
					}
				}
			};
			var url = "../BackendProcess1?file="+music_name;
			url = url + "&sid=" + Math.random();
			request.open("get", url, true);
			request.send(null);
//			result.innerHTML = "<font color=red>"+"You should wait until the server response"+"</font>";
		}else{
			window.alert("Hey boy, you must first choose one music!");
		}
	}
 
 function EditBox(whichbutton){
	 var nod = whichbutton.parentNode.parentNode;
	 var resultnode = nod.childNodes;
	 for(var count=0; count<resultnode.length; count++){
		 var cnode = resultnode[count].id;
		 if (cnode == "name"){
			 alert(resultnode[count].innerHTML);
			 var test = resultnode[count].childNodes;
			 alert(test.value);
		 }
	 }
 }
 
 function SYStime(){
		var d = new Date();
		var weeks = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
		var year = d.getFullYear();
		var mouth = d.getMonth() + 1;
		var day = d.getDate();
		var hour = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
		var minutes = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
		var second = d.getSeconds() < 10 ? '0' + d.getSeconds() : d.getSeconds();
		var weekIndex = d.getDay();
		var week = weeks[weekIndex];
		var curTime = year + '年' + mouth + '月' + day + '日' + hour + ':' + minutes + ':' + second + week;
		alert(curTime);
	}