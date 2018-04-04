package uestc.cuit.bean.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uestc.cuit.bean.clickaction.*;

/**
 * Servlet implementation class BackendProcess
 */
@WebServlet("/BackendProcess")
public class BackendProcess extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackendProcess() {
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
		System.out.println(filename);
		String msg = null;
		ModelIndexBuild ins = new ModelIndexBuild(filename);
		int i = ins.StartMachine();
		if (i==2){
			msg = "Fatal: This record has already existed!";
			response.getWriter().println(msg);
		}else{
			msg = "Congratualtions: Our task finished!";
			response.getWriter().println(msg);
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
