package uestc.cuit.bean.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * Servlet implementation class FileUpoad
 */
@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUpload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		String savePath = "/home/hadoop/workspace/UESTC.CUIT.UI/WebContent/WebContent/Source";
		File saveMenu = new File(savePath);
		System.out.println(savePath);
		if(!saveMenu.exists() && !saveMenu.isDirectory()){
			System.out.println(savePath+"目录不存在，所以创建！");
			saveMenu.mkdir();
		}
		//消息提示
		String message = "";
		try{
			//创建 一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			//解决上传文件中文乱码
			upload.setHeaderEncoding("UTF-8");
			if(!ServletFileUpload.isMultipartContent(request)){
				return;
			}
			List<FileItem> list = upload.parseRequest(request);
			for(FileItem item : list){
				if(item.isFormField()){
					String name = item.getFieldName();
					String value = item.getString("UTF-8");
					System.out.println(name + "=" + value);//这里是用于获取普通的字符串数据上传
				}
				//如果检测到是文件而不是普通的字符串，则执行else中的处理
				else{
					String filename = item.getName();
					System.out.println(filename);
					if(filename==null || filename.trim().equals("")){
						continue;
					}
					//这一句是为了避免有的浏览器上传的文件名为路径的情况
					filename = filename.substring(filename.lastIndexOf("/")+1);
					InputStream in = item.getInputStream();
					FileOutputStream out = new FileOutputStream(savePath + "/" + filename);
					byte buffer[] = new byte[1024];
					int len= 0;
					while((len = in.read(buffer))>0){
						out.write(buffer,0,len);
					}
					in.close();
					out.close();
					item.delete();
					message = "Y";
				}
			}
		}catch(Exception e){
			message = "N";
			e.printStackTrace();
		}
		response.getWriter().println(message);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
