/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.BookingDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.RailwayDTO;
import model.Ticket;
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name = "ConfirmCancelTicketServlet", urlPatterns = {"/confirm-cancel"})
public class ConfirmCancelTicketServlet extends HttpServlet {

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
            out.println("<title>Servlet ConfirmCancelTicketServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ConfirmCancelTicketServlet at " + request.getContextPath() + "</h1>");
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
        // Kiểm tra xem danh sách vé đã chọn có tồn tại không
        if (request.getSession().getAttribute("pendingCancelTickets") == null) {
            response.sendRedirect("cancel-ticket"); // Chuyển về trang chọn vé nếu không có vé nào
            return;
        }

        // Forward đến trang JSP hiển thị thông tin xác nhận hủy vé
        request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        List<RailwayDTO> pendingTickets = (List<RailwayDTO>) session.getAttribute("pendingCancelTickets");
        User user = (User) session.getAttribute("user");

        if (pendingTickets == null || pendingTickets.isEmpty() || user == null) {
            response.sendRedirect("cancel-ticket"); // Nếu không có vé hợp lệ, quay lại trang chọn vé
            return;
        }

        // 🔹 Bước 1: Tính tổng số tiền hoàn (80% tổng giá vé)
        double totalRefund = 0;
        for (RailwayDTO ticket : pendingTickets) {
            totalRefund += ticket.getTicketPrice() * 0.8; // Hoàn lại 80%
        }

        // 🔹 Bước 2: Lấy userID
        int userID = user.getUserId();

        // 🔹 Bước 3: Gọi insertRefund() để lưu vào database
        BookingDAO bookingDAO = new BookingDAO();
        int refundBookingID = -1;
        try {
            refundBookingID = bookingDAO.insertRefund(userID, totalRefund);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi xử lý hoàn tiền. Vui lòng thử lại sau.");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }

        if (refundBookingID == -1) {
            request.setAttribute("errorMessage", "Không thể hoàn tiền, vui lòng thử lại sau.");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }

        // 🔹 Bước 4: Hủy vé sau khi hoàn tiền thành công
        TicketDAO ticketDAO = new TicketDAO();
        for (RailwayDTO ticket : pendingTickets) {
            ticketDAO.cancelTicket(ticket.getTicketID(), ticket.getSeatNumber());
        }

        // Xóa session sau khi hoàn tất
        session.removeAttribute("pendingCancelTickets");

        // Hiển thị thông báo thành công
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Hủy vé thành công</title>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("<script type='text/javascript'>");
        out.println("Swal.fire({");
        out.println("  icon: 'success',");
        out.println("  title: 'Bạn đã hủy vé thành công!',");
        out.println("  text: 'Số tiền hoàn lại: " + totalRefund + " VND',");
        out.println("  confirmButtonText: 'OK'");
        out.println("}).then((result) => {");
        out.println("  if (result.isConfirmed) {");
        out.println("    window.location.href='home.jsp';"); // Điều hướng về trang chủ
        out.println("  }");
        out.println("});");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
        out.close();
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
