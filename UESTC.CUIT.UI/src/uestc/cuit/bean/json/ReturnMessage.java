package uestc.cuit.bean.json;
import java.io.File;  
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileInputStream;  

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uestc.cuit.bean.datatype.*;

public class ReturnMessage {
	private static ArrayList<String>list_small_unit = null;
	private static ArrayList<MusicJSON>music = null;
	private static MusicJSON music_unit = null;
	private static ArrayList<String> puc_list = null;
	private static String line;
	private static int s_t = 0;
	
	public  ReturnMessage(String line){
		this.line = line;
		list_small_unit = new ArrayList<String>();
		music = new ArrayList<MusicJSON>();
		music_unit = new MusicJSON();
		puc_list = new ArrayList<String>();
	}
	
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
	
	public static ArrayList<MusicJSON>  JSONProcessed(){
		puc_list = Punc_list();
		String msg[] = line.split("&");
		JSONArray myja;
		String word_flag = null;
		String sentence_flag = null;
		try {
			myja = new JSONArray(msg[1]); //convert string to JSON array
			int s_ti = 0;  //sentence start time
			int e_ti = 0; //sentence end time
			for(int i=0; i< myja.length();i++) {
				int w_s_ti = 0; //word start time
				int w_e_ti = 0; //word end time
				JSONObject myjObject = myja.getJSONObject(i); //Get one JSON from the JSON array
				try{
				sentence_flag = myjObject.getString("flag");
				if (sentence_flag.equals("hidden")){
					continue;
				}
				}catch(JSONException e){
					//e.printStackTrace();
				}
				String txt = myjObject.getString("onebest");//Txt denotes the sentence we extract from JSON object
				try {
					s_ti = Integer.parseInt(myjObject.getString("bg"));
					s_ti = s_ti + s_t*1000; //calculate the staring time of one sentence
					e_ti = Integer.parseInt(myjObject.getString("ed"));
					e_ti = e_ti + s_t*1000; //calculating the ending time of one sentence
				}catch(NumberFormatException e) {}
				double s_1 = s_ti/1000.0;
				double s_2 = e_ti/1000.0;
				music_unit.setSentence(txt+"@"+s_1+"-"+s_2);
				JSONArray ja = myjObject.getJSONArray("wordsResultList");
				for(int j =0; j<ja.length();j++) {
					JSONObject myob = ja.getJSONObject(j);
					String words = myob.getString("wordsName");
					try{
						word_flag = myob.getString("flag");
						if (word_flag.equals("hidden")){
							continue;
						}
					}catch(JSONException e){
						//e.printStackTrace();
					}
					if (puc_list.contains(words)){
						continue;
					}
					try {
						w_s_ti = s_ti + Integer.parseInt(myob.getString("wordBg"))*10;
						w_e_ti = s_ti + Integer.parseInt(myob.getString("wordEd"))*10;
					}catch(NumberFormatException e) {}
					double w_s_1 = w_s_ti/1000.0;
					double w_s_2 = w_e_ti/1000.0;
					list_small_unit.add(words+"@"+w_s_1+"-"+w_s_2);
					w_s_ti =0;
					w_e_ti =0;
				}//end of the j loop
				music_unit.setWordlist(list_small_unit);
				music.add(music_unit);
				list_small_unit = new ArrayList<String>(); //为什么清空list这里是需要重新初始化?
				music_unit = new MusicJSON();
			} //end of the i loop
		}catch(Exception e) {}
		return music;
	}
	
	public static void main(String[] args) {
	}
}
