/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Utils.SendEmailCancelTicket;
import dal.BookingDAO;
import dal.RefundDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
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
            response.sendRedirect("cancel-ticket");
            return;
        }
        String bankAccountID = request.getParameter("bankAccount");
        String bankName = request.getParameter("bankName");

        // Kiểm tra nếu không nhập tài khoản ngân hàng
        if (bankAccountID == null || bankName == null || bankAccountID.isEmpty() || bankName.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập thông tin tài khoản ngân hàng để hoàn tiền!");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }
        if (!bankAccountID.matches("\\d{1,50}")) {
            request.setAttribute("errorMessage", "Số tài khoản ngân hàng chỉ được chứa các chữ số và tối đa 50 số!");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }
        String enteredOtp = request.getParameter("otp");
        Object sessionOtpObj = request.getSession().getAttribute("otp");
        String sessionOtp = (sessionOtpObj != null) ? String.valueOf(sessionOtpObj) : null;

        if (sessionOtp == null || !sessionOtp.equals(enteredOtp)) {
            // OTP không hợp lệ, gửi lại thông báo lỗi
            request.setAttribute("errorMessage", "Mã OTP không chính xác. Vui lòng thử lại!");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }
        double totalRefund = 0;
        int toalTickets = 0;
        List<Integer> ticketIDs = new ArrayList<>();
        TicketDAO ticketDAO = new TicketDAO();
        for (RailwayDTO ticket : pendingTickets) {
            ticketDAO.cancelTicket(ticket.getTicketID(), ticket.getSeatNumber());
            totalRefund += ticket.getTicketPrice() * 0.8;
            toalTickets++;
            ticketIDs.add(ticket.getTicketID()); // Lưu danh sách TicketID
        }
        RefundDAO refundDAO = new RefundDAO();
        try {
            int refundID = refundDAO.insertRefund(user.getUserId(), bankAccountID, bankName, totalRefund);
            if (refundID == -1) {
                request.setAttribute("errorMessage", "Lỗi khi lưu thông tin hoàn tiền. Vui lòng thử lại!");
                request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
                return;
            }

            // Cập nhật RefundID trong Ticket
            boolean updated = refundDAO.updateTicketsWithRefundID(refundID, ticketIDs);
            if (!updated) {
                request.setAttribute("errorMessage", "Lỗi khi cập nhật RefundID cho vé. Vui lòng thử lại!");
                request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu. Vui lòng thử lại sau!");
            request.getRequestDispatcher("confirmCancelTicket.jsp").forward(request, response);
            return;
        }

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("<html><head><style>")
                .append("body { font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px; }")
                .append(".container { max-width: 800px; background: white; padding: 30px; margin: auto; border-radius: 10px; ")
                .append("box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.15); text-align: left; }")
                .append("h2 { color: #007bff; font-size: 24px; margin-bottom: 20px; text-align: center; }")
                .append("p { font-size: 16px; color: #333; line-height: 1.6; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; border: 1px solid #ddd; }")
                .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: center; font-size: 16px; }")
                .append("th { background-color: #007bff; color: white; font-weight: bold; }")
                .append("tr:nth-child(even) { background-color: #f8f9fa; }")
                .append(".summary { margin-top: 15px; font-size: 17px; }")
                .append(".total-count { font-weight: bold; }")
                .append(".total-refund { font-weight: bold; color: #28a745; }")
                .append("</style></head><body>");

        emailContent.append("<div class='container'>")
                .append("<h2>Xác nhận hủy vé thành công</h2>")
                .append("<p>Chào <strong>").append(user.getFullName()).append("</strong>,</p>")
                .append("<p>Hệ thống xác nhận rằng vé của bạn đã được hủy thành công. Dưới đây là chi tiết vé:</p>");

        emailContent.append("<table><tr>")
                .append("<th>Mã vé</th><th>Tên hành khách</th><th>Đối Tượng</th><th>CCCD</th><th>Hành trình</th><th>Tàu</th><th>Thời gian khởi hành</th><th>Toa</th><th>Chỗ ngồi</th><th>Giá vé</th><th>Tiền hoàn</th></tr>");

        int totalTickets = 0;
        for (RailwayDTO ticket : pendingTickets) {
            emailContent.append("<tr>")
                    .append("<td>").append(ticket.getTicketID()).append("</td>")
                    .append("<td>").append(ticket.getPassengerName()).append("</td>")
                    .append("<td>").append(ticket.getPassengerType()).append("</td>")
                    .append("<td>").append(ticket.getCccd()).append("</td>")
                    .append("<td>").append(ticket.getRoute()).append("</td>")
                    .append("<td>").append(ticket.getTrainCode()).append("</td>")
                    .append("<td>").append(ticket.getDepartureTime()).append("</td>")
                    .append("<td>").append(ticket.getCarriageNumber()).append("</td>")
                    .append("<td>").append(ticket.getSeatNumber()).append("</td>")
                    .append("<td>").append(ticket.getTicketPrice()).append(" VND</td>")
                    .append("<td>").append(ticket.getTicketPrice() * 0.8).append(" VND</td>")
                    .append("</tr>");
        }

        emailContent.append("</table>");

        emailContent.append("<p class='summary'><span class='total-count'>Tổng số vé: ")
                .append(toalTickets)
                .append("</span></p>");

        emailContent.append("<p class='summary'><span class='total-refund'>Tổng tiền hoàn: ")
                .append(totalRefund)
                .append(" VND</span></p>");
        emailContent.append("<p class='summary'><span class='total-count'>Ngân hàng: ")
                .append(bankName)
                .append("</span></p>");
        emailContent.append("<p class='summary'><span class='total-count'>Số tài khoản: ")
                .append(bankAccountID)
                .append("</span></p>");
        emailContent.append("<p class='footer'>Cảm ơn bạn đã sử dụng dịch vụ đặt vé tàu của chúng tôi!<br>Trân trọng,Online Booing Ticket Train</p>");
        emailContent.append("</div></body></html>");

        SendEmailCancelTicket.sendEmail(user.getEmail(), "Xác nhận hủy vé thành công", emailContent.toString());
        request.getSession().removeAttribute("pendingCancelTickets");
        request.getSession().removeAttribute("otp");

        // 🔹 Hiển thị thông báo SweetAlert2
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><head>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/sweetalert2@11'></script>");
            out.println("</head><body>");
            out.println("<script>");
            out.println("Swal.fire({");
            out.println("  icon: 'success',");
            out.println("  title: 'Bạn đã hủy vé thành công!',");
            out.println("  text: 'Số tiền hoàn lại: " + totalRefund + " VND',");
            out.println("  confirmButtonText: 'OK'");
            out.println("}).then(() => window.location.href='home');");
            out.println("</script>");
            out.println("</body></html>");
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
