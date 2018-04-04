/**
 * Main.js  Author:Bean Young
 */
function autoPlay(){
	var myAuto = document.getElementById("myaudio");
	myAuto.play();
}

function StopMusic(){
	var myAuto = document.getElementById("myaudio");
	myAuto.pause();
}

function ReRunMusic(){
	var myAuto = document.getElementById("myaudio");
	myAuto.currentTime = 0;
	myAuto.play();
}

function GetCurrentTime(){
	var myAuto = document.getElementById("myaudio");
	window.alert(myAuto.currentTime);
}

function WordPlay(whichrow){
	var myAuto = document.getElementById("myaudio");
	var goal_node = whichrow.parentNode.parentNode.cells[0];
	var WordTime = goal_node.abbr;
	var start_time = WordTime.split("-")[0];
	var end_time = WordTime.split("-")[1];
	myAuto.currentTime = start_time;
}

function SelectMusic(whichmusic){
	var musi = document.getElementById("myaudio");
	var musiName = document.getElementById("MusicName");
	var path = "./Source/"+whichmusic.innerText;
	musiName.innerHTML = whichmusic.innerText;
	musi.src = path;
}

function FileList(){
	console.log("FileList");
	var file_url = "";
	var file;
	for (var i=0; i< document.getElementById("multifile").files.length; i++){
		file = document.getElementById("multifile").files[i];
		file_url += file.name+";"
	}
	 document.forms["form1"].test.value = file_url;
	 document.form1.submit();
}

function WordExtract(){
	console.log("WordExtract");
	var music_name = "";
	music_name = document.getElementById("MusicName").innerHTML;
	console.log(music_name);
	if (music_name != ""){
		AjaxF(music_name);
	}else{
		window.alert("Hey boy, you must first choose one music!");
	}
}

function AjaxF(music_name){
	console.log("Start Ajax");
	var result = document.getElementById("result");
	var request = getHTTPObject();
	request.onreadystatechange = function(){
		if(request.readyState == 4){
			if(request.status == 200){
				var res = request.responseText;
				result.innerHTML = "<font color=red>"+res+"</font>";
			}
		}
	};
	var url = "../BackendProcess?file="+music_name;
	url = url + "&sid=" + Math.random();
	request.open("get", url, true);
	request.send(null);
	result.innerHTML = "<font color=red>"+"You should wait until the server response......"+"</font>";
}

function WordExport(whichsentence){
	console.log("WordExport");
	var table_title = "<table class=\"table\"><thead><tr><th>分词</th><th>播放</th><th>修改方案</th><th>纠错</th><th>确认</th></tr></thead><tbody>";
	var word_pre = "<tr class=\"no_submit\"><td abbr=\"";
	//these two variables have word duration result in between
	var word_after = "\">";
	//these two variables have word result in between
	var play_button ="</td><td><input type=\"button\" onclick=\"WordPlay(this)\" value=\"播放\"/></td>";
	var fix_box = "<td><input type=\"text\" class=\"form-control\" id=\"name\"  disabled=\"disabled\" placeholder=\"修改方案\"></td>";
	var Edit_fix_box = "<td><input type=\"button\" onclick=\"EditTextbox(this)\" value=\"修改\"/></td>";
	var MakeDecision = "<td><input onclick=\"MakeDecision(this)\" type=\"checkbox\"></td>";
	var tail_html = "</tr></tbody></table><input type=\"button\" onclick=\"Rollback()\"  value=\"撤销\"/><input type=\"button\" onclick=\"AllSub()\"  value=\"提交修改\"/><input type=\"button\" onclick=\"DeleteLogic()\" value=\"删除\"/>";
	var inner_contents = "";
	//inner_contents denotes the HTML string we will insert later
	var music_name = document.getElementById("MusicName").innerHTML;
	var goal_area = document.getElementById("lyricshow");
	var myAuto = document.getElementById("myaudio");
	var sentence_s_time = whichsentence.abbr; 
	var sentence_start_node = document.getElementById("sentence_start_time");
	sentence_start_node.value = sentence_s_time;
	//the start time of the sentence
	if(music_name != ""){
		var request = getHTTPObject();
		request.onreadystatechange = function(){
			if(request.readyState == 4){
				if(request.status == 200){	
					myAuto.currentTime = sentence_s_time;//这句话的意思就是我在点击歌词的时候，音轨需要移动到相应的位置
					var res = request.responseText; //注意的是后端传来的数据是JSON格式，即使原来是list也需要通过 net.sf 包里的方法进行JSON数据转换
					var obj = eval("("+res+")");
					for (var key in obj){
						var inner = obj[key];
						var sentn  = inner["sentence"];
						var sentenc_start_duration = sentn.split("@")[1].split("-")[0]; //time_duration of sentence
						if(sentence_s_time != sentenc_start_duration){
							continue;
						}
						// this if's goal is to locate the sentence we just click
						inner_contents +=table_title;
						var wordlist = inner["wordlist"];
						for (var id in wordlist){
							var word_sin = wordlist[id];
							var word_word = word_sin.split("@")[0];
							var word_duration = word_sin.split("@")[1]; //the start and end time of the word
							inner_contents +=word_pre;
							inner_contents +=word_duration;
							inner_contents +=word_after;
							inner_contents +=word_word;
							inner_contents +=play_button;
							inner_contents +=fix_box;
							inner_contents +=Edit_fix_box;
							inner_contents +=MakeDecision;
						}
						inner_contents +=tail_html;
						break;
					}
					goal_area.innerHTML = inner_contents;
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

function Fileupload(){
	console.log("Fileload Ajax");
	var result = document.getElementById("result");
	var request = getHTTPObject();
	var Data = new FormData(document.forms.namedItem("formfile"));
	console.log(Data);
	request.onreadystatechange=function(){
		if(request.readyState == 4){
			if(request.status == 200){
				var res = request.responseText;
				console.log(res);
				if (res == "N"){
					window.alert("文件上传失败! 请重新尝试!");
				}else{
					window.alert("文件上传成功! 现在可以进行导入操作!");
				}
			}
		}
	};
	var url = "../FileUpload";
	url = url + "?sid=" + Math.random();
	request.open("post", url, true);
	request.send(Data);
}

function lyricload(){
	console.log("lyricload");
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
						var sentenc_duration = sentn.split("@")[1].split("-")[0]; //start time of one sentence
						htmlcode += "<tr><td align=\"center\" class = \"test\" id = \"origin\"  onclick = \"WordExport(this)\" abbr=\""+sentenc_duration+"\">"+sentenc+"</td></tr>";
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
	var lyriclist = document.getElementsByClassName("test");
	console.log(lyric);
	console.log("log");
	audiobut.ontimeupdate = function(e){
		for(var i=0; i<lyriclist.length;i++){
			if(audiobut.currentTime > lyriclist[i].abbr){
					AttributeClassSet(i);
			}
		}
	};
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
	return curTime;
}

function EditTextbox(whichrow){
	var goal_node = whichrow.parentNode.parentNode.cells[1].nextElementSibling.firstChild;
	if(goal_node.disabled){
		goal_node.disabled = false;
		whichrow.value = "保存";
	}else{
		goal_node.disabled = true;
		whichrow.value = "修改";
	}
}

function MakeDecision(whichrow){
	var goal_node = whichrow.parentNode.parentNode;
	if (whichrow.checked){
		goal_node.setAttribute("class", "wait_submit");
	}else{
		goal_node.setAttribute("class", "no_submit");
	}
}

function AllSub(){
	var obj = document.getElementsByClassName("wait_submit");
	var music_name = document.getElementById("MusicName").innerHTML;
	var sentence_start_time_sub = document.getElementById("sentence_start_time").value;
	var myAuto = document.getElementById("myaudio");
	var curtime = SYStime();
	var JSONcontents = "";
	JSONcontents = JSONcontents + curtime + "L";
	for (i=0; i<obj.length; i++){
		var object_n = obj[i];
		var word_start_time = object_n.cells[0].abbr;
		var old_value = object_n.cells[0].innerText;
		var new_value = object_n.cells[1].nextElementSibling.firstChild.value;
		if (i == obj.length-1){
			if (new_value == ""){
			JSONcontents = JSONcontents + old_value+"@"+old_value+"@"+word_start_time+"L";
			continue;
			}else{
				JSONcontents = JSONcontents + old_value+"@"+new_value+"@"+word_start_time+"L";
			continue;
			}
		}
		if (new_value == ""){
			continue;
			//如果用户此时没有进行修改,反而选中了该行的情况
		}
		JSONcontents = JSONcontents + old_value+"@"+new_value+"@"+word_start_time+"H";
	}
	JSONcontents = JSONcontents + music_name + "@" + sentence_start_time_sub;
	console.log(JSONcontents);
	var request = getHTTPObject();
	request.onreadystatechange = function(){
		if (request.readyState == 4){
			if(request.status == 200){
				var res = request.responseText;
				window.alert(res);
				document.getElementById("ModelClickExport").click();
			}
		}
	};//end of function
	var url = "../BackendProcess2?record=" + JSONcontents;
	url = url + "&signal=" + "MODIFY";
	url = url + "&sid=" + Math.random();
	request.open("get", url, true);
	request.send(null);
}


function Rollback(){
	var request = getHTTPObject();
	request.onreadystatechange = function(){
		if(request.readyState == 4){
			if (request.status == 200){
				var res = request.responseText;
				window.alert(res);
				document.getElementById("ModelClickExport").click();
			}
		}
	};
	var url = "../BackendProcess3?signal=" + "ROLLBACK";
	url = url + "&sid=" + Math.random();
	request.open("get", url, true);
	request.send(null);
}

function DeleteLogic(){
	var obj = document.getElementsByClassName("wait_submit");
	var music_name = document.getElementById("MusicName").innerHTML;
	var sentence_start_time_sub = document.getElementById("sentence_start_time").value;
	var myAuto = document.getElementById("myaudio");
	var curtime = SYStime();
	var JSONcontents = "";
	JSONcontents = JSONcontents + curtime + "L";
	for (i=0; i<obj.length; i++){
		var object_n = obj[i];
		var word_start_time = object_n.cells[0].abbr;
		var old_value = object_n.cells[0].innerText;
		var new_value = object_n.cells[1].nextElementSibling.firstChild.value;
		if (new_value == ""){
			new_value = "测试";
			//如果用户此时没有进行修改,反而选中了该行的情况
		}
		if (i == obj.length-1){
			JSONcontents = JSONcontents + old_value+"@"+new_value+"@"+word_start_time+"L";
			continue;
		}
		JSONcontents = JSONcontents + old_value+"@"+new_value+"@"+word_start_time+"H";
	}
	JSONcontents = JSONcontents + music_name + "@" + sentence_start_time_sub;
	console.log(JSONcontents);
	var request = getHTTPObject();
	request.onreadystatechange = function(){
		if (request.readyState == 4){
			if(request.status == 200){
				var res = request.responseText;
				window.alert(res);
				document.getElementById("ModelClickExport").click();
			}
		}
	};//end of function
	var url = "../BackendProcess2?record=" + JSONcontents;
	url = url + "&signal=" + "DELETE";
	url = url + "&sid=" + Math.random();
	request.open("get", url, true);
	request.send(null);
}

function DownLoad(){
	console.log("Start download");
	var request = getHTTPObject();
	request.onreadystatechange = function(){
		if (request.readyState == 4){
			if (request.status == 200){
				var res = request.responseText;
				if (res == "NO"){
					window.alert("你还有进行任何数据采集的操作哦!");
				}else{
					//这里是文件下载
					document.getElementById("downclick").click();
				}
			}
		}
	};
	var url = "../DownLoadFile?sid=" + Math.random();
	request.open("get", url, true);
	request.send(null);
}

