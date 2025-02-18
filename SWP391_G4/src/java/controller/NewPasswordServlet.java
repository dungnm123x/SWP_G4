/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author dung9
 */
public class NewPasswordServlet extends HttpServlet {
   
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
            out.println("<title>Servlet NewPasswordServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewPasswordServlet at " + request.getContextPath () + "</h1>");
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
        processRequest(request, response);
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
         // Kiểm tra session xem người dùng có đăng nhập không
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy email từ session
        String email = (String) session.getAttribute("email");
        System.out.println("DEBUG: Email trong session: " + email); // Debug để kiểm tra email

        // Lấy thông tin từ form
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        boolean hasError = false;

        // Kiểm tra mật khẩu hợp lệ
        if (!newPassword.matches("^(?=.*[^A-Za-z0-9]).{6,}$")) {
            request.setAttribute("passwordError", "Mật khẩu phải có ít nhất 1 ký tự đặc biệt và tối thiểu 6 ký tự!");
            hasError = true;
        } else if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("repasswordError", "Mật khẩu nhập lại không khớp!");
            hasError = true;
        }

        // Nếu có lỗi, quay lại trang nhập mật khẩu với thông báo lỗi
        if (hasError) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("newPassword.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Gọi UserDAO để cập nhật mật khẩu
        UserDAO userDAO = new UserDAO();
        boolean updateSuccess = userDAO.updateForgotPassword(email, newPassword);

        if (updateSuccess) {
            request.setAttribute("successMessage", "Cập nhật mật khẩu thành công!");
        } else {
            request.setAttribute("errorMessage", "Cập nhật mật khẩu thất bại! Hãy thử lại.");
        }

        // Quay lại trang newPassword.jsp để hiển thị thông báo
        RequestDispatcher dispatcher = request.getRequestDispatcher("newPassword.jsp");
        dispatcher.forward(request, response);
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
