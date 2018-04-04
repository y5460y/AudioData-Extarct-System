package uestc.cuit.bean.index;
import java.io.File;
import java.util.Date;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.util.RandomAccess;
import uestc.cuit.bean.json.*;
import uestc.cuit.bean.datatype.*;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import org.json.JSONException;
import java.io.FileNotFoundException;
import uestc.cuit.bean.index.CheckFileExist;
import com.google.gson.reflect.TypeToken;
import java.io.UnsupportedEncodingException;

public class LogFileIndexBuilder {
	private static String RawData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.txt";             //日志文件的绝对路径
	private static String PosIdData ="/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.pld";           //记录位置文件的绝对路径
	private static String FlagData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.flag";             //日志文件类型标志位
	private static String Action = null;
	private static ArrayList<Long> PosId = new ArrayList<Long>();
	private static ArrayList<String> Flag_List = new ArrayList<String>();
	private static String status = null;                 //该信号决定了是进行重建索引还是更新索引 A:更新索引 B:重建索引
	private static long last_position;
	public LogFileIndexBuilder(){
		// TODO Auto-generated constructor stub
	}
	public static void SearchEngine(String RawData, String PosIdData, String FlagData, long lpos, String stat, String Action){
			setRawData(RawData);         
			setPosIdData(PosIdData);   
			setFlagData(FlagData);           
			setLastposition(lpos);
			setStatus(stat);
			setAction(Action);
			PosId = new ArrayList<Long>();
			Flag_List = new ArrayList<String>();
			if (status.equals("A")){
				addIndex();
			}else{
				createIndex();
			}
		}
	
	public static void setRawData(String rawData) {
		RawData = rawData;
	}
	public static void setPosIdData(String posIdData) {
		PosIdData = posIdData;
	}
	public static void setFlagData(String flagData) {
		FlagData = flagData;
	}
	public static void setStatus(String status) {
		LogFileIndexBuilder.status = status;
	}
	public static void setLastposition(long last_position) {
		LogFileIndexBuilder.last_position = last_position;
	}
	public static void setAction(String action) {
		Action = action;
	}
	public static void createIndex(){
		File audio_flag = new File(FlagData);
		File audio_pos = new File(PosIdData);
		if (audio_flag.exists()){
			try{
				audio_flag.delete();
				audio_flag.createNewFile();
			}catch(IOException e){}
		}
		if (audio_pos.exists()){
			try{
				audio_pos.delete();
				audio_pos.createNewFile();
			}catch(IOException e){}
		}
		BufferedReader in = null;
		BufferedWriter bw_pos = null;
		BufferedWriter bw_flag = null;
		try{
			File file = new File(RawData);
			long length = file.length(); 
			 in = new BufferedReader(new InputStreamReader(new FileInputStream(RawData)));
			 bw_pos = new BufferedWriter(new FileWriter(PosIdData));
			 bw_flag = new BufferedWriter(new FileWriter(FlagData));
			 String line = null;
			 long pos = 0L;
			 System.out.println(length); //目前的文件长度
			 while((line=in.readLine())!=null){
				bw_pos.append(pos+"\r\n");
				if (Action.equals("MODIFY")){
				bw_flag.append("N"+"\r\n");
				}
				if (Action.equals("DELETE")){
					bw_flag.append("D"+"\r\n");
				}
				byte[] b = line.getBytes();
				long bytes = b.length;
				pos = pos + bytes + 2;
				}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if((in != null)&&(bw_flag != null)&&(bw_pos != null)){
			try{
		    in.close();
		    bw_pos.close();
		    bw_flag.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			}else{
				System.out.println("文件流关闭失败G!");
			}
		}
	}
	
	public static void addIndex(){
		RandomAccessFile accessor_pos = null;
		RandomAccessFile accessor_flag = null;
		try{
			File file_pos = new File(PosIdData);
			File file_flag = new File(FlagData);
			long pos_length = file_pos.length(); //追踪到文件的末尾
			long flag_length = file_flag.length(); //追踪到文件的末尾
			accessor_pos = new RandomAccessFile(PosIdData, "rw");
			accessor_flag = new RandomAccessFile(FlagData, "rw");
			accessor_pos.seek(pos_length);
			accessor_flag.seek(flag_length);
			accessor_pos.write((last_position+"\r\n").getBytes());
			System.out.println(Action);
			if (Action.equals("MODIFY")){
			    accessor_flag.write(("N"+"\r\n").getBytes());
			}
			if (Action.equals("DELETE")){
				accessor_flag.write(("D" +  "\r\n").getBytes());
			}
			if (Action.equals("ROLLBACK")){
				accessor_flag.write(("RB"+"\r\n").getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if ((accessor_flag != null)&&(accessor_pos != null)){
			try{
			accessor_flag.close();
			accessor_pos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		}else{
			System.out.println("文件流关闭失败H!");
		}
	}
	}
	
	public static void loadLogIndex(){
		//首先初始化list列表
		PosId = new ArrayList<Long>();
		Flag_List = new ArrayList<String>();
		BufferedReader bf1 = null;
		BufferedReader bf2 = null;
		try{
			bf1 = new BufferedReader(new FileReader(PosIdData));
			bf2 = new BufferedReader(new FileReader(FlagData));
			String line_1 = null;
			String line_2 = null;
			while ((line_1=bf1.readLine())!=null){
				PosId.add(Long.parseLong(line_1));
			}
			while ((line_2=bf2.readLine())!=null){
				Flag_List.add(line_2);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		if ((bf1 != null)&&(bf2 != null)){
			try{
			bf1.close();
			bf2.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			System.out.println("文件流关闭失败I!");
		}
		}
	}
	
	public static String Fetch_Record(){
		int goal_line_number = Fetch_Goal_Position();
		long goal_position = PosId.get(goal_line_number);
		String Flag_inner =  Flag_List.get(goal_line_number);
		String F_record = null;
		RandomAccessFile accessor = null;
		try{
			accessor = new RandomAccessFile(RawData, "r");
			accessor.seek(goal_position);
			F_record = new String(accessor.readLine().getBytes("ISO-8859-1"), "utf-8");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(accessor!=null){
			try{
			accessor.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.out.println("文件流关闭失败J!");
		}
		}
		return Flag_inner+"&"+F_record;
	}
	
	public static int Fetch_Goal_Position(){
		loadLogIndex();
		int goal_line_number = 0;
		for (int i = PosId.size()-1; i>-1; i--){
			if (Flag_List.get(i).equals("Y")||Flag_List.get(i).equals("RB")||Flag_List.get(i).equals("E")){
				continue; //Y, E means this record has been rolled back
			}else{
				goal_line_number = i;
				break;
			}
		}
		return goal_line_number;
	}
	
	public static void FlagDataModify(int line_number, String record_flag){
		loadLogIndex();
		if (record_flag.equals("N")){
		Flag_List.set(line_number, "Y");
		}
		if (record_flag.equals("D")){
			Flag_List.set(line_number, "E");
		}
		File f = new File(FlagData);
		boolean flag_d = false;
		flag_d = f.delete();
		if (flag_d){
			System.out.println("标志文件删除成功了!");
		}
		try{
		flag_d = f.createNewFile();
		if (flag_d){
			System.out.println("标志文件创建成功了!");
		}
		}catch(IOException e){
			e.printStackTrace();
			}
		RandomAccessFile randomFile = null;
		try{
			randomFile = new RandomAccessFile(FlagData, "rw");
			for (int i=0; i<Flag_List.size(); i++){
				randomFile.write((Flag_List.get(i)+"\r\n").getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(randomFile != null){
			try{
				randomFile.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}else{
			System.out.println("文件流关闭失败J!");
		}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
