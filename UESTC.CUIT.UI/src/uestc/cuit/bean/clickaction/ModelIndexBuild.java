package uestc.cuit.bean.clickaction;
import uestc.cuit.bean.file.FileWriter;
import uestc.cuit.bean.keda.api.TestLfasr;
import uestc.cuit.bean.index.CheckFileExist;
import uestc.cuit.bean.index.IndexBuilder;
public class ModelIndexBuild {
	private static String file_path = null;
	private static String file_name = null;
	private static String file_prefix = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/Source/";
	
	public ModelIndexBuild(String file_name){
		this.file_name = file_name;
	}
	
	public int StartMachine(){
		TestLfasr t = new TestLfasr(file_prefix+file_name,file_name,20);
		int result = t.KE_DA_API_control();
		System.out.println("打印出这一流程的最终状态:"+result);
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ModelIndexBuild ins = new ModelIndexBuild("2.wav");
		ins.StartMachine();
	}
}
