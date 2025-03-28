/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.TicketDAO;
import model.RailwayDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name = "CancelTicketServlet", urlPatterns = {"/cancel-ticket"})
public class CancelTicketServlet extends HttpServlet {

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
            out.println("<title>Servlet CancelTicketServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CancelTicketServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        TicketDAO ticketDAO = new TicketDAO();
        List<RailwayDTO> allTickets = ticketDAO.getDetailedTicketsByUserID(user.getUserId());
        List<RailwayDTO> availableTickets = new ArrayList<>();

        for (RailwayDTO ticket : allTickets) {
            if ("Unused".equals(ticket.getTicketStatus())) {
                availableTickets.add(ticket);
            }
        }

        // Lấy thông tin lọc từ request
        String filterTicketID = request.getParameter("filterTicketID");
        String filterPassengerName = request.getParameter("filterPassengerName");
        String filterCCCD = request.getParameter("filterCCCD");
        String filterRoute = request.getParameter("filterRoute");

        // 1. Lọc danh sách vé trước khi phân trang
        List<RailwayDTO> filteredTickets = new ArrayList<>();
        for (RailwayDTO ticket : availableTickets) {
            boolean matches = true;

            if (filterTicketID != null && !filterTicketID.isEmpty() && !String.valueOf(ticket.getTicketID()).contains(filterTicketID)) {
                matches = false;
            }
            if (filterPassengerName != null && !filterPassengerName.isEmpty() && !ticket.getPassengerName().toLowerCase().contains(filterPassengerName.toLowerCase())) {
                matches = false;
            }
            if (filterCCCD != null && !filterCCCD.isEmpty() && !ticket.getCccd().contains(filterCCCD)) {
                matches = false;
            }
            if (filterRoute != null && !filterRoute.isEmpty() && !ticket.getRoute().toLowerCase().contains(filterRoute.toLowerCase())) {
                matches = false;
            }

            if (matches) {
                filteredTickets.add(ticket);
            }
        }

        // 2. Xử lý phân trang
        int page = 1;
        int recordsPerPage = 5;
        String pageParam = request.getParameter("page");

        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        int totalRecords = filteredTickets.size();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, totalRecords);
        List<RailwayDTO> paginatedTickets = filteredTickets.subList(start, end);

        // 3. Nếu không tìm thấy vé, hiển thị thông báo
        if (filteredTickets.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy vé nào phù hợp.");
        }

        // 4. Truyền dữ liệu đến JSP
        request.setAttribute("tickets", paginatedTickets);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("filterTicketID", filterTicketID);
        request.setAttribute("filterPassengerName", filterPassengerName);
        request.setAttribute("filterCCCD", filterCCCD);
        request.setAttribute("filterRoute", filterRoute);

        request.getRequestDispatcher("cancelTicket.jsp").forward(request, response);
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
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String[] selectedTicketIDs = request.getParameterValues("selectedTickets");

        TicketDAO ticketDAO = new TicketDAO();
        List<RailwayDTO> allTickets = ticketDAO.getDetailedTicketsByUserID(user.getUserId());
        List<RailwayDTO> availableTickets = new ArrayList<>();

        // Lọc vé chưa sử dụng
        for (RailwayDTO ticket : allTickets) {
            if ("Unused".equals(ticket.getTicketStatus())) {
                availableTickets.add(ticket);
            }
        }

        if (selectedTicketIDs == null || selectedTicketIDs.length == 0) {
            request.setAttribute("error", "Vui lòng chọn ít nhất một vé để hủy.");
            restorePagination(request, response, availableTickets); // Gọi hàm khôi phục phân trang
            return;
        }

        List<RailwayDTO> cancelableTickets = new ArrayList<>();
        List<String> invalidTickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (String ticketID : selectedTicketIDs) {
            for (RailwayDTO ticket : allTickets) {
                if (ticket.getTicketID() == Integer.parseInt(ticketID)) {
                    LocalDateTime departureTime = ticket.getDepartureTime().toLocalDateTime();
                    Duration duration = Duration.between(now, departureTime);

                    if (now.isAfter(departureTime)) {
                        invalidTickets.add("Vé " + ticketID + " đã quá hạn để hủy.");
                    } else if (duration.toHours() < 12) {
                        invalidTickets.add("Vé " + ticketID + " không thể hủy vì còn dưới 12 tiếng trước khi khởi hành.");
                    } else {
                        cancelableTickets.add(ticket);
                    }
                    break;
                }
            }
        }

        if (!invalidTickets.isEmpty()) {
            request.setAttribute("error", String.join("<br>", invalidTickets));
            restorePagination(request, response, availableTickets); // Giữ nguyên phân trang khi có lỗi
            return;
        }

        // Lưu danh sách vé đang chờ hủy vào session
        session.setAttribute("pendingCancelTickets", cancelableTickets);
        response.sendRedirect("confirm-cancel");
    }

    private void restorePagination(HttpServletRequest request, HttpServletResponse response, List<RailwayDTO> allTickets)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int page = 1;
        int recordsPerPage = 5; // Số vé hiển thị mỗi trang
        String pageParam = request.getParameter("page");

        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, allTickets.size());
        List<RailwayDTO> paginatedTickets = allTickets.subList(start, end);

        int totalPages = (int) Math.ceil((double) allTickets.size() / recordsPerPage);
        String filterTicketID = request.getParameter("filterTicketID");

// Kiểm tra nếu filterTicketID không phải số thì đặt về null
        if (filterTicketID != null && !filterTicketID.matches("\\d+")) {
            filterTicketID = null;
            request.setAttribute("error", "Mã vé chỉ được chứa số!");
        }

        request.setAttribute("filterTicketID", filterTicketID);
        request.setAttribute("tickets", paginatedTickets);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("cancelTicket.jsp").forward(request, response);
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
