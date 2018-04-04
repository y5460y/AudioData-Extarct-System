 package uestc.cuit.bean.index;
import java.io.File;
import java.util.Date;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import org.json.JSONException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import uestc.cuit.bean.index.CheckFileExist;
import uestc.cuit.bean.json.*;
import uestc.cuit.bean.datatype.*;

public class IndexBuilder {
	private static String RawData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.txt";
	private static String PosIdData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.pId";
	private static String FilenameData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.name";
	private static ArrayList<Long> PosId = null;
	private static ArrayList<String> NameList = null;
	private static int status;
	private static long last_position;
	public IndexBuilder() {
		// TODO Auto-generated constructor stub
	}
	public static void SearchEngine(String RawData, String PosIdData, String FilenameData, long lpos, int stat) {
			setRawData(RawData);
			setPosIdData(PosIdData);
			setFilenameData(FilenameData);
			setLastposition(lpos);
			setStatus(stat);
			PosId = new ArrayList<Long>();
			NameList = new ArrayList<String>(); 
			if (status == 0){
				addIndex(RawData);
			}
			else {
				createIndex(RawData);
			}
	}
	public static void setRawData(String Raw) {
		RawData = Raw;
	}
	public static void setPosIdData(String PosId) {
		PosIdData = PosId;
	}
	public static void setFilenameData(String Filename) {
		FilenameData = Filename;
	}
	public static void setStatus(int stat){
		status = stat;
	}
	public static void setLastposition(long lpos){
		last_position = lpos;
	}
	public static void createIndex(String RawData) {
		File audio_name = new File(FilenameData);
		File audio_pos = new File(PosIdData);
		if (audio_name.exists()){
			try{
			audio_name.delete();
			audio_name.createNewFile();
			}catch(IOException e){}
		}
		if (audio_pos.exists()){
			try{
				audio_pos.delete();
				audio_pos.createNewFile();
			}catch(IOException e){}
		}
		BufferedReader in = null;
		BufferedWriter bw_audio = null;
		BufferedWriter bw_pos = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(RawData))); //Dangerous!!!!!!
			bw_audio = new BufferedWriter(new FileWriter(FilenameData));
			bw_pos = new BufferedWriter(new FileWriter(PosIdData));
			File file = new File(RawData);
			String line  = null;
			String name = null;
			long pos = 0L;
			long length = file.length(); // 原始文件总长度
			System.out.println(length);
			while((line = in.readLine()) != null) {
				name = GetName(line);
				bw_audio.append(name + "\r\n"); 
				bw_pos.append(pos+"\r\n"); 
				byte[] b = line.getBytes();
				long bytes = b.length;
				pos = pos + bytes+2; //因为我们每次插入记录的时候,都在后面加入了一条'\r\n'
			} 
		}catch(Exception e) {
			e.printStackTrace();
			}finally{
				if ((in != null)&&(bw_audio != null)&&(bw_pos != null)){
				try{
				in.close();
				bw_audio.close();
				bw_pos.close();
				}catch(IOException e){
					e.printStackTrace();
					}
				}else{
					System.out.println("文件流关闭失败C!");
				}
			}
	}
	
	public static void addIndex(String RawData){
		System.out.println("We add index");
		String line = "";
		String name = "";
		RandomAccessFile accessor = null;
		RandomAccessFile accessor_name = null;
		RandomAccessFile accessor_pos = null;
		try{
			File file_name = new File(FilenameData);
			File file_pos = new File(PosIdData);
			long name_length = file_name.length();
			long pos_length = file_pos.length();
			accessor = new RandomAccessFile(RawData, "r");
			accessor_name = new RandomAccessFile(FilenameData, "rw");
			accessor_pos = new RandomAccessFile(PosIdData, "rw");
			accessor.seek(last_position); //定位到最新记录的位置
			accessor_name.seek(name_length);
			accessor_pos.seek(pos_length);
			line = new String(accessor.readLine().getBytes("ISO-8859-1"), "utf-8"); //拿回最新的记录
			name = GetName(line);
			accessor_name.write((name+"\r\n").getBytes());
			accessor_pos.write((last_position+"\r\n").getBytes());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if ((accessor != null)&&(accessor_name != null)&&(accessor_pos != null)){
			try{
				accessor.close();
				accessor_name.close();
				accessor_pos.close();
			}catch(Exception e){
			    e.printStackTrace();	
			}
			}else{
				System.out.println("文件流关闭失败D!");
			}
		}
	}
	
	//加载索引文件到内存
	public static void loadIndex() {
		//首先初始化list列表
		PosId = new ArrayList<Long>();
		NameList = new ArrayList<String>();
		BufferedReader bf1= null;
		BufferedReader bf2= null;
		try {
			bf1 = new BufferedReader(new FileReader(PosIdData));
			bf2 = new BufferedReader(new FileReader(FilenameData));
			String line_1 = null;
			String line_2 = null;
			while((line_1=bf1.readLine())!=null) {
				PosId.add(Long.parseLong(line_1));
			}
			while ((line_2=bf2.readLine())!=null){
				NameList.add(line_2);
			}
		}catch(Exception e) {
			e.printStackTrace();
			}finally{
				if ((bf1 != null)&&(bf2 != null)){
				try{
					bf1.close();
					bf2.close();
				}catch(Exception e){
				e.printStackTrace();	
				}
				}else{
					System.out.println("文件流关闭失败E!");
				}
			}
	}
	
	//接收文件名, 返回
	public static String SearchWheel(String filename) {
		loadIndex();
		String line = LineReturn(filename);
		if(line != null){
		String info = line.split("&")[1];
		return info;
	}else{
		System.out.println("寻找记录失败!");
		return null;
		//这里需要注意, 后续应该如何处理?
	}
	}
	
	public static String LineReturn(String filename) {
		loadIndex();
		String line = "";
		CheckFileExist tem = new CheckFileExist(filename, FilenameData);
		boolean flag = tem.CheckStatus();
		if (flag){
			RandomAccessFile accessor = null;
		try {
			accessor = new RandomAccessFile(RawData, "r");
			accessor.seek(PosId.get(NameList.indexOf(filename)));
			line = new String(accessor.readLine().getBytes("ISO-8859-1"), "utf-8");
		}catch(IOException e) {
			e.printStackTrace();
		}finally{
			if(accessor != null){
			try{
				accessor.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			System.out.println("文件流关闭失败!");
		}
			}
		return line;
	}else{
		return null;
	}
	}
	
	public static String GetName(String line) {
		String name = null;
		String [] splitline = line.split("&");
		name = splitline[0];
		return name;
	}
	
	public static void main(String[] args){
		String info = "";
		IndexBuilder instan = new IndexBuilder();
		info = instan.SearchWheel("9.wav");
		if(info == null){
			System.out.println("No record found!!!!!");
		}else{
			System.out.println(info);
		}
	}
}
