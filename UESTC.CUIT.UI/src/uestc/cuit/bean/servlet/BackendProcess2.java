package uestc.cuit.bean.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uestc.cuit.bean.file.LogFileWriter;


/**
 * Servlet implementation class BackendProcess2
 */
@WebServlet("/BackendProcess2")
public class BackendProcess2 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BackendProcess2() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		String result = request.getParameter("record");
		String signal_front = request.getParameter("signal");
		System.out.println(result);
		System.out.println(signal_front);
		LogFileWriter obj = new LogFileWriter(result, signal_front);
		obj.TxtFileWriter();
		String msg = "后端修改逻辑被成功调用!";
		response.getWriter().println(msg);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
