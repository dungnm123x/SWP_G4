/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dung9
 */
public class UpdateProfile extends HttpServlet {
   
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
            out.println("<title>Servlet UpdateProfile</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateProfile at " + request.getContextPath () + "</h1>");
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
        UserDAO ud = new UserDAO();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("login").forward(request, response);
        } else {
//            User u = ud.getUserByAccountId(user.getAccountId());
            request.setAttribute("userProfile", user);
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
        }
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
       HttpSession session = request.getSession();
        UserDAO ud = new UserDAO();
        User user = (User) session.getAttribute("user");

        String fullName = request.getParameter("fullName").trim();
        String address = request.getParameter("address").trim();
        String phone = request.getParameter("phone").trim();

        // Kiểm tra dữ liệu nhập vào
        if (fullName.isEmpty() || address.isEmpty()) {
            request.setAttribute("userProfile", user);
            request.setAttribute("err1", "Tên hoặc địa chỉ không được để trống");
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
            return;
        }
        if (!validatePhone(phone)) {
            request.setAttribute("userProfile", user);
            request.setAttribute("err", "Số điện thoại không hợp lệ");
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
            return;
        }

        // Cập nhật thông tin user
        ud.update(user.getUserId(), fullName, address, phone);

        // Lấy lại thông tin user sau khi cập nhật
        User updatedUser = ud.getUserById(user.getUserId());
        // Lấy lại thông tin user từ database (nếu userId null thì lấy theo username)
        if (updatedUser == null) {
            updatedUser = ud.getUserByUsername(user.getUsername());
        }
        session.setAttribute("user", updatedUser);

        request.setAttribute("mess", "Cập nhật thông tin thành công");
        request.setAttribute("userProfile", updatedUser);
        request.getRequestDispatcher("userProfile.jsp").forward(request, response);
    }

    private boolean validatePhone(String phone) {
        if (phone.length() != 10) {
            return false;
        }
        if (!phone.startsWith("0")) {
            return false;
        }
        return true;
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
