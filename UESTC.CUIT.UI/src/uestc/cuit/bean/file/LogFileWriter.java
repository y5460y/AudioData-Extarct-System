package uestc.cuit.bean.file;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileInputStream;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import uestc.cuit.bean.index.LogFileIndexBuilder;
import uestc.cuit.bean.fix.*;

public class LogFileWriter {
	private static String filepath ="/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.txt";  //日志文件的绝对路径
	private static String PosData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.pld";  //记录位置文件的绝对路径
	private static String FlagData = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/log/log.flag";  //日志文件类型标志位 
	private String contents = null;
	private String status = null;
	private long CurrentPosition = 0L;
	public LogFileWriter(String contents, String status){
		this.contents = contents;
		this.status = status;
	}
	
	public void TxtFileWriter(){
		File f = new File(filepath);
		boolean flag = false;
		if (status.equals("ROLLBACK")){
			SearchMerge.SearchMergeControl(status);
		}else{
			//MODIFY OR DELETE
			if (!f.exists()){
				System.out.println("Sorry, the file does not exists, so create a new file!");
				try{
					flag = f.createNewFile();
					if (flag){
						System.out.println("源文件创建成功了!");
					}else{
						System.out.println("源文件创建失败!");
						return;
					}
				}catch(IOException e){}
				RandomAccessFile randomFile = null;
				try{
					randomFile = new RandomAccessFile(filepath, "rw");
					randomFile.write((contents+"\r\n").getBytes());
					CurrentPosition = 0;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{
						randomFile.close();
					}catch(IOException e){}
				}
				LogFileIndexBuilder.SearchEngine(filepath, PosData, FlagData, CurrentPosition, "B", status);
				SearchMerge.SearchMergeControl(status);
			}else{
				//文件已经存在的情况
				RandomAccessFile randomFile = null;
				try{
					randomFile = new RandomAccessFile(filepath,"rw");
					CurrentPosition = randomFile.length();
					randomFile.seek(CurrentPosition); 
					randomFile.write((contents+"\r\n").getBytes());
				}catch(Exception e){
					e.printStackTrace();
				}
				finally{
					try{
						randomFile.close();
					}catch(IOException e){}
				}
				LogFileIndexBuilder.SearchEngine(filepath, PosData, FlagData, CurrentPosition, "A", status);
				SearchMerge.SearchMergeControl(status);
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogFileWriter obj = new LogFileWriter("2018年4月2日15:50:51星期一L况且@并且@0.54-1.37H根本@原本@1.54-1.84L8.wav@0.4", "ROLLBACK");
		obj.TxtFileWriter();
	}

}
