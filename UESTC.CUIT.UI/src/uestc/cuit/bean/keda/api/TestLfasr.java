package uestc.cuit.bean.keda.api;
import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uestc.cuit.bean.datatype.*;

import com.alibaba.fastjson.JSON;

import org.apache.log4j.PropertyConfigurator;

import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

import uestc.cuit.bean.file.FileWriter;
import uestc.cuit.bean.index.CheckFileExist;

public class TestLfasr {
	//本地文件的路径
	private static  String local_file;  // 音乐文件的URL
	private static String file_name; // 音乐文件名
	private static int sleepSecond; //往服务器上请求间隔用的时间
	private static String SourceDataPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.txt";//这是原始文件的路径
	private static String SourceDataNamePath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.name"; //这是音乐list文件路径
	private static String SourceDataPIDPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.pId"; //这是位置文件路径
	private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
	//标准型 : LfasrType.LFASR_STANDARD_RECORDED_AUDIO
	
	public  TestLfasr(String local_file, String filename, int sleepSecond) {
		//constructor stub
		this.local_file = local_file;
		this.sleepSecond = sleepSecond;
		this.file_name = filename;
	}
	
	public String API_KE_DA() {
		PropertyConfigurator.configure("/home/hadoop/workspace/UESTC.CUIT.UI/source/log4j.properties");
		LfasrClientImp lc = null; //define a client object to prepare for connection
		String task_id = ""; // each commit task has corresponding id
		String contents = "";
		try {
			lc = LfasrClientImp.initLfasrClient();
		}catch(LfasrException e) {
			Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + initMsg.getErr_no());
			System.out.println("failed=" + initMsg.getFailed());
		}
		HashMap<String, String> params = new HashMap<>();
		params.put("has_participle", "true"); // this means we need sentence word segregation
		try {
			Message uploadMsg = lc.lfasrUpload(local_file, type, params);
			int ok = uploadMsg.getOk();
			if (ok == 0) {
				// task established success!
				task_id = uploadMsg.getData();
				System.out.println("task_id" + task_id);
			}else {
				System.out.println("ecode=" + uploadMsg.getErr_no());
				System.out.println("failed=" + uploadMsg.getFailed());
			}
		}catch(LfasrException e) {
			// upload problem report
			Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + uploadMsg.getErr_no());
			System.out.println("failed=" + uploadMsg.getFailed());
		}
		
		while(true) {
			try {
				Thread.sleep(sleepSecond*1000); 
				System.out.println("waiting....");
			}catch(InterruptedException e) {e.printStackTrace();}
			try {
				Message progressMsg = lc.lfasrGetProgress(task_id);
				if (progressMsg.getOk() != 0) {
					System.out.println("task was fail. task_id:" + task_id);
					System.out.println("ecode=" + progressMsg.getErr_no());
					System.out.println("failed=" + progressMsg.getFailed());
					continue;
				}else {
					ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
					if (progressStatus.getStatus() == 9) {

						System.out.println("task was completed. task_id:" + task_id);
						break;	
					} else {
						System.out.println("task was incomplete. task_id:" + task_id + ", status:" + progressStatus.getDesc());
						continue;
					}
				}
			}catch(LfasrException e) {
				Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
				System.out.println("ecode=" + progressMsg.getErr_no());
				System.out.println("failed=" + progressMsg.getFailed());
			}
		}
		try {
			Message resultMsg = lc.lfasrGetResult(task_id);
			if (resultMsg.getOk() == 0) {
				System.out.println(resultMsg.getData());	
				contents = resultMsg.getData();
				return file_name+"&"+contents;
			} else {
				System.out.println("ecode=" + resultMsg.getErr_no());
				System.out.println("failed=" + resultMsg.getFailed());
				contents = null;
			}
		} catch (LfasrException e) {
			Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
			System.out.println("ecode=" + resultMsg.getErr_no());
			System.out.println("failed=" + resultMsg.getFailed());
		}
		return file_name+"&"+contents;
	}
	
	public static int KE_DA_API_logic(){
		int i = 0;
		File f = new File(SourceDataPath);
		if(!f.exists()){
			System.out.println("原始的数据文件不存在!");
			i = 1; 
		}else{
			System.out.println("原始数据文件存在, 首先必须要检查这条记录是否已经存在了!");
			CheckFileExist tem = new CheckFileExist(file_name,SourceDataNamePath);
			boolean flag = tem.CheckStatus();
			if(!flag){
				System.out.println("这是一条新的记录,我们的工作可以继续!");
				i = 0; 
			}else{
				i = 2; //记录已经存在了!
			}
		}
		return i;
	}
	
	public static int KE_DA_API_control(){
		int status_inner = KE_DA_API_logic(); 
		String result = "";
		if(status_inner != 2){
			TestLfasr t = new TestLfasr(local_file,file_name,sleepSecond);
			result = t.API_KE_DA();
			FileWriter.setContents(result);
			FileWriter.setStatus(status_inner);
			FileWriter.TxtFileWriter();
			//因为这里都是静态的变量, 最好的方法是这样来调用
			return status_inner;
		}else{
			System.out.println("严重: 这条记录已经存在了!");
			return status_inner; // do nothing
		}
	}
	
	public static void main(String[] args) {
	}	
}

