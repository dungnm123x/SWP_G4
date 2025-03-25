/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import dal.BookingDAO;
import dal.TicketDAO;
import java.util.HashSet;
import java.util.Set;
// Chú ý import các lớp bên model
import model.Booking;
import model.CartItem;
import model.Ticket;
import model.Trip;
import model.User;

/**
 *
 * @author Admin
 */
public class PaymentServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet PaymentServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PaymentServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        User user = (User) session.getAttribute("user");
//
//        // Nếu chưa đăng nhập
//        if (user == null) {
//            // Lưu lại trang hiện tại (đang yêu cầu) vào session
//            session.setAttribute("redirectAfterLogin", "payment.jsp");
//            // Chuyển hướng đến trang login
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        // Nếu đã đăng nhập, hiển thị trang payment
        request.getRequestDispatcher("payment.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1) Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Chưa đăng nhập => chuyển về login
            response.sendRedirect("login.jsp");
            return;
        }

        // 2) Đọc phương thức thanh toán
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null) {
            // Không có => về lại payment.jsp
            response.sendRedirect("payment");
            return;
        }

        // 3) Tuỳ từng phương thức, ta xử lý khác nhau
        switch (paymentMethod) {
            case "creditCard":
                // Tạm hiển thị trang success luôn
                // Hoặc hiển thị form nhập thẻ, v.v.
                request.getRequestDispatcher("success.jsp").forward(request, response);
                break;

            case "eWallet":
                // Tương tự
                request.getRequestDispatcher("success.jsp").forward(request, response);
                break;

            case "bankTransfer":
                // Tương tự
                request.getRequestDispatcher("success.jsp").forward(request, response);
                break;

            case "vnpay":
                // Nếu là VNPay => chuyển hướng sang VNPayServlet (hoặc code create URL VNPay)
                response.sendRedirect("vnpay");
                // -> Ở VNPayServlet, bạn build URL => user thanh toán => callback ReturnResult
                break;

            default:
                // Phương thức không hợp lệ => quay lại
                response.sendRedirect("payment");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
