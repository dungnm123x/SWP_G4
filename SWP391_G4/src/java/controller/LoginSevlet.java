package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dung9
 */
public class LoginSevlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginSevlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginSevlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       request.getRequestDispatcher("login.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password");

        System.out.println("DEBUG: Username nhập vào = " + username);
        System.out.println("DEBUG: Password nhập vào = " + password);

        UserDAO userDAO = new UserDAO();
        User user = userDAO.checkUserLogin(username, password);

        if (user == null) {
            System.out.println("DEBUG: Tên đăng nhập hoặc mật khẩu sai!");
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu sai!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            System.out.println("DEBUG: Đăng nhập thành công với tài khoản " + username);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            String rememberMe = request.getParameter("remember"); // Fix lỗi checkbox nhớ mật khẩu
            if ("on".equals(rememberMe)) {
                Cookie cuser = new Cookie("cuser", username);
                Cookie cpass = new Cookie("cpass", password);  // Lưu mật khẩu vào cookie
                Cookie crem = new Cookie("crem", "on");

                cuser.setMaxAge(60 * 60 * 24 * 5);
                cpass.setMaxAge(60 * 60 * 24 * 5);
                crem.setMaxAge(60 * 60 * 24 * 5);

                response.addCookie(cuser);
                response.addCookie(cpass);
                response.addCookie(crem);
            } else {
                // Xóa cookie nếu người dùng bỏ chọn "Nhớ mật khẩu"
                Cookie cuser = new Cookie("cuser", "");
                Cookie cpass = new Cookie("cpass", "");
                Cookie crem = new Cookie("crem", "");

                cuser.setMaxAge(0);
                cpass.setMaxAge(0);
                crem.setMaxAge(0);

                response.addCookie(cuser);
                response.addCookie(cpass);
                response.addCookie(crem);
            }

            if (user.getRoleID() == 1 ) {
                response.sendRedirect("dashboard");
            } 
            
           if(user.getRoleID() == 2 ) {
                response.sendRedirect("dashboard");
            }
           else{
               response.sendRedirect("home");
           }
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
