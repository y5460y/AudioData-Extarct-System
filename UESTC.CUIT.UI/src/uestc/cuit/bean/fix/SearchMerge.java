package uestc.cuit.bean.fix;
import java.io.File;  
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import org.json.JSONException;
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileInputStream;  
import java.io.RandomAccessFile;
import java.io.InputStreamReader;  
import com.google.gson.JsonIOException;
import javafx.scene.input.DataFormat;
import uestc.cuit.bean.datatype.*;
import uestc.cuit.bean.index.*;

public class SearchMerge {
	private static ArrayList<String> list = null;
	private static ArrayList<String> puc_list = null;
	private static String Signal = null;
	private static String filepath ="/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.txt";  //日志文件的绝对路径
	private static String PosData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.pld";  //记录位置文件的绝对路径
	private static String FlagData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.flag";   //日志文件类型标志位 
	private static String SourceDataPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.txt"; //这是原始文件的路径
	private static String SourceDataPIDPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.pId"; //这是位置文件路径
	private static String SourceDataNamePath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.name"; //这是音乐list文件路径
	
	public SearchMerge(){
		// TODO Auto-generated constructor stub
	}
	
	public static void SearchMergeControl(String signal){
		setSignal(signal);
		CleanOriginLine();
	}
	
	public static void setSignal(String signal) {
		Signal = signal;
	}
	
	//加载所有的标点符号到内存
	public static ArrayList<String> Punc_list(){
		BufferedReader br = null;
		puc_list = new ArrayList<String>();
		try{
			File puc_file = new File("/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/punctuation.txt");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(puc_file)));
			String line = null;
			while((line = br.readLine())!=null){
				puc_list.add(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (br != null){
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				System.out.println("文件流关闭失败F!");
			}
		}
		puc_list.add(""); //接口中获取的字符串还有为空的情况
		return puc_list;
	}
	
	public static void CleanOriginLine(){
		list = new ArrayList<String>();
		String temp_line = null;
		int line_number = LogFileIndexBuilder.Fetch_Goal_Position();
		String record_line_all = LogFileIndexBuilder.Fetch_Record(); //获取日志中的目标记录(修改方案), 记录的标志位为N
		System.out.println(record_line_all);
		String record_flag = record_line_all.split("&")[0];
		System.out.println(record_flag);
		String record_line = record_line_all.split("&")[1];
		System.out.println(record_line);
		ArrayList<String> FixScheme = ProcessedInfo(record_line); 
		String file_name = FixScheme.get(0).split("@")[0]; //定位到文件名
		String line = file_name+"&"+IndexBuilder.SearchWheel(file_name); //这是原始文件中返回的需要修改的那个文件记录
		String replaceLine = null;
		if (record_flag.equals("N")){
		    replaceLine = Fix(FixScheme, Signal); //根据修改方案修改完成的最终结果,需要重新写入
		}
		if (record_flag.equals("D")){
			replaceLine = Delete(FixScheme, Signal);
		}
		boolean flag = false;
		BufferedReader bf1 = null;
		BufferedWriter bfw1 = null;
		try{
			bf1 = new BufferedReader(new FileReader(SourceDataPath));
			while((temp_line = bf1.readLine()) != null){
				if(temp_line.equals(line)){
					list.add(replaceLine);
					continue;
				}
				list.add(temp_line);
			}
		}catch(Exception e){
			e.printStackTrace();
			}finally{
				if (bf1 != null){
				try{
				bf1.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				System.out.println("文件流关闭失败K!");
			}}
		try{
			File f_1 = new File(SourceDataPath);
			flag = f_1.delete();
			if(flag){
				System.out.println("源文件删除成功!");
			}
			flag = f_1.createNewFile();
			if (flag){
				System.out.println("源文件创建成功!");
			}
			bfw1 = new BufferedWriter(new FileWriter(SourceDataPath));
		for (String elementS : list){
			bfw1.write(elementS+"\r\n");
		}
		}catch(Exception e){
			e.printStackTrace();
			}finally{
				if (bfw1 != null){
				try{
					bfw1.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				System.out.println("文件流关闭失败L!");
			}}
		IndexRebuild(); 
		//索引文件全部重建
		if (Signal.equals("ROLLBACK")){
			RandomAccessFile randomFile = null;
			long CurrentPosition = 0L;
			try{
				randomFile = new RandomAccessFile(filepath,"rw");
				CurrentPosition = randomFile.length();
				randomFile.seek(CurrentPosition); 
				randomFile.write((record_line+"\r\n").getBytes());
			}catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try{
					randomFile.close();
				}catch(IOException e){}
			}
			LogFileIndexBuilder.SearchEngine(filepath, PosData, FlagData, CurrentPosition, "A", Signal);
			LogFileIndexBuilder.FlagDataModify(line_number, record_flag);
			System.out.println("你完成了一次roll back 操作！");
		}else{
			//只是普通的Modify
			System.out.println("这只是一个简单的MODIFY, 工作完成!");
		}
}
	
	public static void IndexRebuild(){
		IndexBuilder.SearchEngine(SourceDataPath, SourceDataPIDPath, SourceDataNamePath, 0, 1);
	}
	
	public static String Fix(ArrayList<String> FixScheme, String signal){
		puc_list = Punc_list();
		String file_name = FixScheme.get(0).split("@")[0];
		String line = IndexBuilder.SearchWheel(file_name);
		String SentenceStartTime = FixScheme.get(0).split("@")[1];
		JSONArray myja = null;
		JSONObject myjObject = null;
		String newSentence = "";
		int s_ti = 0;
		double sen_s_time = 0.0;
		try{
			myja = new JSONArray(line);
			// convert string variable to JSON format
		}catch(JSONException e){
			e.printStackTrace();
		}

		for (int m=0; m<myja.length(); m++){
			try{
				myjObject = myja.getJSONObject(m);
				s_ti = Integer.parseInt(myjObject.getString("bg"));
				sen_s_time = s_ti/1000.0;
				if ((sen_s_time+"").equals(SentenceStartTime)){
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		for (int i = 1; i<FixScheme.size(); i++){
			int w_ti = 0;
			double wor_s_time = 0.0;
			String StartTime = FixScheme.get(i).split("@")[2].split("-")[0];
			String Txt = null;
			String word_name = null;
			if (signal.equals("ROLLBACK")){
				Txt = FixScheme.get(i).split("@")[0];
			}else{
				Txt = FixScheme.get(i).split("@")[1];
			}
			try{
			JSONArray wordarr = myjObject.getJSONArray("wordsResultList");
			for (int j =0; j<wordarr.length(); j++){
				JSONObject wordobj = wordarr.getJSONObject(j);
				word_name = wordobj.getString("wordsName");
				if (puc_list.contains(word_name)){
					continue;
				}
				w_ti = s_ti + Integer.parseInt(wordobj.getString("wordBg"))*10;
				wor_s_time = w_ti/1000.0;
				if ((wor_s_time+"").equals(StartTime)){
					wordobj.remove("wordsName");
					wordobj.put("wordsName", Txt);
					break;
				}
			}
			}catch(JSONException e){
				e.printStackTrace();
			}
		}// loop the fix_schema we get
		try{
		JSONArray wordarr_1 = myjObject.getJSONArray("wordsResultList");
		for (int n = 0; n<wordarr_1.length(); n++){
			JSONObject obj = wordarr_1.getJSONObject(n);
			newSentence = newSentence + obj.getString("wordsName");
		}
		myjObject.remove("onebest");
		myjObject.put("onebest", newSentence);
		}catch(JSONException e){
			e.printStackTrace();
		}
		System.out.println("在修改后的结果");
		System.out.println(file_name+"&"+myja.toString());
		return file_name+"&"+myja.toString();
	}
	
	public static String Delete(ArrayList<String> FixScheme, String signal){
		puc_list = Punc_list();
		String file_name = FixScheme.get(0).split("@")[0];
		String line = IndexBuilder.SearchWheel(file_name);
		String SentenceStartTime = FixScheme.get(0).split("@")[1];
		JSONArray myja = null;
		JSONObject myjObject = null;
		String newSentence = "";
		String word_word = null;
		String word_flag = null;
		int sentence_len = 0;
		int s_ti = 0;
		double sen_s_time = 0.0;
		try{
			myja = new JSONArray(line);
			//convert string variable to JSON format
		}catch(JSONException e){
			e.printStackTrace();
		}
		for (int i=0; i<myja.length(); i++){
			try{
				myjObject = myja.getJSONObject(i);
				s_ti = Integer.parseInt(myjObject.getString("bg"));
				sen_s_time = s_ti/1000.0;
				if((sen_s_time+"").equals(SentenceStartTime)){
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}//locate the goal sentence
		try{
		JSONArray wordarr_2 = myjObject.getJSONArray("wordsResultList");
		for (int m=0; m<wordarr_2.length(); m++){
			JSONObject wordobj = wordarr_2.getJSONObject(m);
			word_word = wordobj.getString("wordsName");
			try{
			word_flag = wordobj.getString("flag");
			if (word_flag.equals("hidden")){
				continue;
			}
			}catch(JSONException e){
				//e.printStackTrace();
			}// catch exception here, because some word does not have flag attribute
			if (puc_list.contains(word_word)){
				continue;
			}
			sentence_len = sentence_len + 1;
		}
		}catch(Exception e){
			e.printStackTrace();
		}// this try-catch record the current sentence length
		int fix_word_len = FixScheme.size() - 1;
		if (fix_word_len == sentence_len){
			if (signal.equals("ROLLBACK")){
		 			System.out.println("The whole sentence should be rollback!");
		 			myjObject.remove("flag");
		 			System.out.println("Sentence roll back succeed!");
		             }else{
					try{
						System.out.println("The whole sentence should be deleted!");
					myjObject.put("flag", "hidden");
					}catch(JSONException e){}
					System.out.println("Delete sentence task finished!");
		             }
		}else{
		try{
		for (int n = 1; n<FixScheme.size(); n++){
			int w_ti = 0;
			double wor_s_time = 0.0;
			String StartTime = FixScheme.get(n).split("@")[2].split("-")[0];
			JSONArray wordarr_1 = myjObject.getJSONArray("wordsResultList");
			for (int j=0; j< wordarr_1.length(); j++){
				JSONObject wordobj_1 = wordarr_1.getJSONObject(j);
				word_word = wordobj_1.getString("wordsName");
//				try{
//					word_flag = wordobj_1.getString("flag");
//					if (word_flag.equals("hidden")){
//						continue;
//					}
//				}catch(JSONException e){
//					//e.printStackTrace();
//				}// catch exception here, because some word does not have flag attribute
				if (puc_list.contains(word_word)){
					continue;
				}
				w_ti = s_ti + Integer.parseInt(wordobj_1.getString("wordBg"))*10;
				wor_s_time = w_ti/1000.0;
				if ((wor_s_time+"").equals(StartTime)){
					if (signal.equals("ROLLBACK")){
						wordobj_1.remove("flag");
						break;
					}else{
					wordobj_1.put("flag", "hidden");
					break;
					}
				}
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		}//end for else , fix word by word

		try{
			JSONArray wordarr_3 = myjObject.getJSONArray("wordsResultList");
			for (int k = 0; k<wordarr_3.length(); k++){
				JSONObject obj = wordarr_3.getJSONObject(k);
				word_word = obj.getString("wordsName");
				try{
					word_flag = obj.getString("flag");
					if (word_flag.equals("hidden")){
						continue;
					}
				}catch(JSONException e){
					//e.printStackTrace();
				}// catch exception here, because some word does not have flag attribute
				newSentence = newSentence + word_word;
			}
			myjObject.remove("onebest");
			myjObject.put("onebest", newSentence);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("在修改后的结果");
		System.out.println(file_name+"&"+myja.toString());
		return file_name+"&"+myja.toString();
	}
	
	
	public static ArrayList<String> ProcessedInfo(String record_line){
			ArrayList<String> FixScheme = new ArrayList<String>();
			String MsgSplit[] = record_line.split("L");
			if (MsgSplit.length == 2){
				System.out.println("前端没有传来任何修改方案!");
				String target_filename = MsgSplit[1].split("@")[0]; //目标文件名, 如: 1.wav
				String sentence_start_time = MsgSplit[1].split("@")[1]; //该句子的起始时间
				FixScheme.add(target_filename+"@"+sentence_start_time);
			}else{
			String target_filename = MsgSplit[2].split("@")[0]; //目标文件名, 如: 1.wav
			String sentence_start_time = MsgSplit[2].split("@")[1]; //该句子的起始时间
			FixScheme.add(target_filename+"@"+sentence_start_time);
			String record[] = MsgSplit[1].split("H");
			for (int j=0; j<record.length; j++){
				System.out.println(record[j]);
				FixScheme.add(record[j]);
			}
			}
			System.out.println(FixScheme);
			return FixScheme;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
