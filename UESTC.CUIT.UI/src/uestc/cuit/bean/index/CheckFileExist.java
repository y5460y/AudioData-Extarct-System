package uestc.cuit.bean.index;
import java.io.File;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class CheckFileExist {
	private static String filename = "";
	private static String FilenameData = null;
	private static ArrayList<String> NaList = null;
	private static boolean flag =  false;
	
	public CheckFileExist(String filename, String FilenameData){
		this.filename = filename;
		this.FilenameData = FilenameData;
	}
	
	public  static void LoadFileList(){
		NaList = new ArrayList<String>();
		try{
			BufferedReader bf1 = new BufferedReader(new FileReader(FilenameData));
			String line_1 = null;
			while((line_1=bf1.readLine())!=null){
				NaList.add(line_1);
			}
			bf1.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean CheckStatus(){
		LoadFileList();
		flag = NaList.contains(filename);
		return flag;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CheckFileExist tem = new CheckFileExist("220to240.wav", "doc/test.name");
		System.out.println(tem.CheckStatus());
	}

}
