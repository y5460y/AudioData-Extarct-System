package uestc.cuit.bean.file;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import uestc.cuit.bean.datatype.MusicJSON;
import uestc.cuit.bean.index.IndexBuilder;
import uestc.cuit.bean.json.ReturnMessage;

public class FileWriter {
	private static String SourceDataPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.txt"; // 这是原始文件的路径
	private static String SourceDataPIDPath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.pId"; // 这是位置文件路径
	private static String SourceDataNamePath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/doc/test.name"; // 这是音乐list文件路径
	private static String contents = null; // 接收从接口中传来的字符串
	private static int status; // 这里的status有两个,1: 新建原始文件 2: 在原来的原始文件的基础上增加记录
	private static long CurrentPosition; // 写入新纪录之前,整体文件的长度

	public static void setContents(String contents) {
		FileWriter.contents = contents;
	}

	public static void setStatus(int status) {
		FileWriter.status = status;
	}

	public static void TxtFileWriter() {
		File f = new File(SourceDataPath);
		System.out.println("接收到的状态为:");
		if (status == 1) {
			System.out.println("原始文件不存在, 创建一个新的文件!");
			RandomAccessFile randomFile = null;
			try {
				f.createNewFile(); // 创建新的原始文件
				randomFile = new RandomAccessFile(SourceDataPath, "rw");
				randomFile.write((contents + "\r\n").getBytes());
				CurrentPosition = 0; // 这里是随便写的，后面没有用上
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (randomFile != null) {
					try {
						randomFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("文件流关闭失败A!");
				}
			}
		}
		if (status == 0) {
			RandomAccessFile randomFile = null;
			try {
				randomFile = new RandomAccessFile(SourceDataPath, "rw");
				CurrentPosition = randomFile.length();
				randomFile.seek(CurrentPosition); // 跟踪到文件的末尾
				randomFile.write((contents + "\r\n").getBytes()); // 防止出现乱码
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (randomFile != null) {
					try {
						randomFile.close();
						System.out.println("成功写入一条新的记录!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("文件流关闭失败B!");
				}
			}
		} else {
			System.out.println("此条记录已经存在了!");
		}
		// 这里最好是添加一个配置文件, 这样这些路径就不用写在代码里面了
		IndexBuilder.SearchEngine(SourceDataPath, SourceDataPIDPath,SourceDataNamePath, CurrentPosition, status);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}
}