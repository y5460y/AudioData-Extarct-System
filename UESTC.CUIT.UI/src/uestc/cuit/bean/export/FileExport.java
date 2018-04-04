package uestc.cuit.bean.export;
import java.io.File;  
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStreamReader;  
import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.FileInputStream;  
import java.io.RandomAccessFile;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileExport {
	private static String SourceDataPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.txt";
	private static String NewDataPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/pool/result.txt";
	private static ArrayList<String> puc_list = null;
	public FileExport(){
		this.puc_list = new ArrayList<String>();
	}
	public static String getSourceDataPath() {
		return SourceDataPath;
	}

	public static void setSourceDataPath(String sourceDataPath) {
		SourceDataPath = sourceDataPath;
	}
	
	public int FinalDataGenerate(){
		File new_file = new File(NewDataPath);
		File source_file = new File(SourceDataPath);
		boolean flag = false;
		if (!source_file.exists()){
			System.out.println("你还未开始进行音频文本数据提取工作!");
			return 0;
		}
		if (new_file.exists()){
			try{
				flag = new_file.delete();
				if (flag){
					System.out.println("导出文件删除成功!");
				}
				flag = new_file.createNewFile();
				if (flag){
					System.out.println("导出文件创建成功!");
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		RandomAccessFile randomFile = null;
		BufferedReader br = null;
		String line = null;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(source_file)));
			randomFile = new RandomAccessFile(new_file, "rw");
			while ((line = br.readLine())!=null){
				randomFile.write((MessageFilter(line)+"<NEXT>\r\n").getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if ((br != null)&&(randomFile != null)){
				try{
					br.close();
					randomFile.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}// end for finally
		return 1;
	}
	
	public static ArrayList<String> Punc_list(){
		BufferedReader br = null;
		puc_list = new ArrayList<String>();
		try{
			File puc_file = new File("/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/punctuation.txt");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(puc_file)));
			String line_1 = null;
			while((line_1 = br.readLine())!=null){
				puc_list.add(line_1);
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
	
	public static String MessageFilter(String line){
		puc_list = Punc_list();
		String final_result = null;
		String msg[] = line.split("&");
		JSONArray myja = null;
		int s_ti = 0;
		String Sentence_flag = null;
		String Word_flag = null;
		String Word_word = null;
		String new_sentence = "";
		try{
			myja = new JSONArray(msg[1]); //convert string to JSON Array
		}catch(JSONException e){
			e.printStackTrace();
		}

		for (int i =0; i<myja.length(); i++){
			try{
				JSONObject myjObject = myja.getJSONObject(i);
				try{
				Sentence_flag = myjObject.getString("flag");
				if (Sentence_flag.equals("hidden")){
					continue;
				}
				}catch(JSONException e){
					//do nothing
				}
				try{
					s_ti = Integer.parseInt(myjObject.getString("bg"));
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				int w_s_ti = 0;
				int w_e_ti = 0;
				double wor_s_time = 0.0;
				double wor_e_time = 0.0;
				JSONArray wordarr = myjObject.getJSONArray("wordsResultList");
				for (int m =0; m<wordarr.length(); m++){
					JSONObject wordobj = wordarr.getJSONObject(m);
					Word_word = wordobj.getString("wordsName");
					try{
						Word_flag = wordobj.getString("flag");
						if (Word_flag.equals("hidden")){
							continue;
						}
					}catch(JSONException e){
						//do nothing
					}
					if (puc_list.contains(Word_word)){
						continue;
					}
					try{
					w_s_ti = s_ti + Integer.parseInt(wordobj.getString("wordBg"))*10;
					w_e_ti = s_ti + Integer.parseInt(wordobj.getString("wordEd"))*10;
					wor_s_time = w_s_ti/1000.0;
					wor_e_time = w_e_ti/1000.0;
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
					new_sentence = new_sentence + Word_word + "@" + wor_s_time + "@" + wor_e_time + "\r\n";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return new_sentence;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileExport instan = new FileExport();
		instan.FinalDataGenerate();
	}

}
