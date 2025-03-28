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
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet(name = "RefundListServlet", urlPatterns = {"/refund-list", "/refund-details"})
public class RefundListServlet extends HttpServlet {

    private RefundDAO refundDAO;

    public void init() {
        refundDAO = new RefundDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        if (action.equals("/refund-details")) {
            showRefundDetails(request, response);
        } else {
            listRefunds(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    private void listRefunds(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy dữ liệu từ form lọc
            HttpSession session = request.getSession(); // Không cần (false), vì chắc chắn đã có session
            int userID = (Integer) session.getAttribute("loggedInUserID");
            String bankAccountID = request.getParameter("bankAccountID");
            bankAccountID = (bankAccountID != null) ? bankAccountID.trim() : "";

            String refundDate = request.getParameter("refundDate");
            refundDate = (refundDate != null) ? refundDate.trim() : "";

            String refundStatus = request.getParameter("refundStatus");
            refundStatus = (refundStatus != null) ? refundStatus.trim() : "";

            String bankName = request.getParameter("bankName");
            bankName = (bankName != null) ? bankName.trim() : "";

// Lấy TicketID (chuyển về Integer nếu có)
            Integer ticketID = null;
            String ticketIDParam = request.getParameter("ticketID");
            if (ticketIDParam != null && !ticketIDParam.trim().isEmpty()) {
                try {
                    ticketID = Integer.parseInt(ticketIDParam.trim());
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Mã vé không hợp lệ.");
                    request.getRequestDispatcher("RefundList.jsp").forward(request, response);
                    return;
                }
            }

// Gọi DAO với các điều kiện lọc mới
            List<RefundDTO> refunds = refundDAO.getAllRefundDetailsByUser(userID, bankAccountID, refundDate, refundStatus, bankName, ticketID);
            // Nếu không có kết quả, hiển thị thông báo
            if (refunds.isEmpty()) {
                request.setAttribute("message", "Không tìm thấy dữ liệu phù hợp.");
            }

            request.setAttribute("refunds", refunds);
            request.getRequestDispatcher("RefundList.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi khi truy vấn dữ liệu hoàn tiền: " + e.getMessage());
            request.getRequestDispatcher("RefundList.jsp").forward(request, response);
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
        request.getRequestDispatcher("RefundDetailsUser.jsp").forward(request, response);
    }
}
