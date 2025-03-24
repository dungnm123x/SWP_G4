package controller;

import dal.RefundDAO;
import dto.RefundDTO;
import Utils.SendEmailCancelTicket;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

@WebServlet(name = "RefundController", urlPatterns = {"/refund", "/refundDetails", "/confirmRefund"})
public class RefundController extends HttpServlet {

    private RefundDAO refundDAO;

    public void init() {
        refundDAO = new RefundDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        if (action.equals("/refundDetails")) {
            showRefundDetails(request, response);
        } else {
            listRefunds(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();

        if (action.equals("/confirmRefund")) {
            confirmRefund(request, response);
        }
    }

    private void listRefunds(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy dữ liệu từ form lọc
            String bankAccountID = request.getParameter("bankAccountID");
            String refundDate = request.getParameter("refundDate");
            String refundStatus = request.getParameter("refundStatus");
            String customerName = request.getParameter("customerName");
            String phoneNumber = request.getParameter("phoneNumber");
            String customerEmail = request.getParameter("customerEmail");

            // Gọi DAO với các điều kiện lọc
            List<RefundDTO> refunds = refundDAO.getAllRefundDetails(bankAccountID, refundDate, refundStatus,
                    customerName, phoneNumber, customerEmail);

            // Nếu không có kết quả, hiển thị thông báo
            if (refunds.isEmpty()) {
                request.setAttribute("message", "Không tìm thấy dữ liệu phù hợp.");
            }

            request.setAttribute("refunds", refunds);
            request.getRequestDispatcher("view/employee/RefundManager.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi truy vấn dữ liệu hoàn tiền: " + e.getMessage());
            request.getRequestDispatcher("view/employee/RefundManager.jsp").forward(request, response);
        }
    }

    private void showRefundDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String refundIDParam = request.getParameter("refundID");

        if (refundIDParam != null) {
            try {
                int refundID = Integer.parseInt(refundIDParam);
                RefundDTO refund = refundDAO.getRefundDetailsByID(refundID);
                request.setAttribute("refund", refund);
            } catch (NumberFormatException | SQLException e) {
                request.setAttribute("error", "Không tìm thấy đơn hoàn tiền.");
                e.printStackTrace(); // Debug lỗi nếu có
            }
        } else {
            request.setAttribute("error", "Thiếu mã hoàn tiền.");
        }
        request.getRequestDispatcher("view/employee/RefundDetails.jsp").forward(request, response);
    }

    private void confirmRefund(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String refundIDParam = request.getParameter("refundID");

        if (refundIDParam != null) {
            try {

                int refundID = Integer.parseInt(refundIDParam);
                boolean updated = refundDAO.updateRefundStatus(refundID, "Complete");
                RefundDTO refund = refundDAO.getRefundDetailsByID(refundID);
                if (updated) {
                    // Gửi email thông báo
                    String subject = "Xác nhận hoàn tiền thành công";

                    String message = "<div style='max-width:600px; margin:auto; border:1px solid #ddd; padding:20px; border-radius:10px; font-family:Arial,sans-serif;'>"
                            + "<h2 style='color:#007bff; text-align:center;'>Thông báo hoàn tiền thành công</h2>"
                            + "<p>Chào <strong>" + refund.getCustomerName() + "</strong>,</p>"
                            + "<p>Chúng tôi đã hoàn tiền thành công cho bạn với thông tin chi tiết sau:</p>"
                            + "<table style='width:100%; border-collapse:collapse;'>"
                            + "  <tr><td style='padding:8px; border-bottom:1px solid #ddd;'><strong>Ngân hàng:</strong></td><td style='padding:8px; border-bottom:1px solid #ddd;'>" + refund.getBankName() + "</td></tr>"
                            + "  <tr><td style='padding:8px; border-bottom:1px solid #ddd;'><strong>Số tài khoản:</strong></td><td style='padding:8px; border-bottom:1px solid #ddd;'>" + refund.getBankAccountID() + "</td></tr>"
                            + "  <tr><td style='padding:8px; border-bottom:1px solid #ddd;'><strong>Số tiền hoàn:</strong></td><td style='padding:8px; border-bottom:1px solid #ddd;'>" + refund.getTotalRefund() + " VND</td></tr>"
                            + "<p style='margin-top:15px;'>Nếu có bất kỳ vấn đề nào, vui lòng liên hệ tổng đài hỗ trợ.</p>"
                            + "<p style='text-align:center;'><strong>Trân trọng,</strong><br>OnlineTicketTrainBooking</p>"
                            + "</div>";

                    boolean emailSent = SendEmailCancelTicket.sendEmail(refund.getCustomerEmail(), subject, message);

                    if (emailSent) {
                        request.setAttribute("success", "Hoàn tiền thành công và email đã gửi.");
                    } else {
                        request.setAttribute("error", "Hoàn tiền thành công nhưng gửi email thất bại.");
                    }
                } else {
                    request.setAttribute("error", "Cập nhật trạng thái thất bại.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi xử lý: " + e.getMessage());
            }
        } else {
            request.setAttribute("error", "Thiếu mã hoàn tiền.");
        }

        response.sendRedirect("refundDetails?refundID=" + refundIDParam);
    }

}
