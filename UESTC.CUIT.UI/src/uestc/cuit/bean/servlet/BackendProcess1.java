package uestc.cuit.bean.servlet;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uestc.cuit.bean.json.ReturnMessage;
import uestc.cuit.bean.datatype.MusicJSON;
import uestc.cuit.bean.index.IndexBuilder;
/**
 * Servlet implementation class BackendProcess1
 */
@WebServlet("/BackendProcess1")
public class BackendProcess1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ArrayList<MusicJSON> temp_contents  = new ArrayList<MusicJSON>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackendProcess1() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		String filename = request.getParameter("file");
		System.out.println("从前端传来的文件名是:"+filename);
		String msg = null;
		IndexBuilder instan = new IndexBuilder();
		msg = instan.SearchWheel(filename);
		ReturnMessage Rsm = new ReturnMessage(filename+"&"+msg);
		temp_contents = Rsm.JSONProcessed();
		Gson json = new Gson();
		String info = json.toJson(temp_contents);
		if(msg == null){
			msg = "No record found!!!";
			response.getWriter().println(msg);
		}else{
			response.getWriter().println(info);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
