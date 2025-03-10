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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Nếu chưa đăng nhập
        if (user == null) {
            // Lưu lại trang hiện tại (đang yêu cầu) vào session
            session.setAttribute("redirectAfterLogin", "payment.jsp");
            // Chuyển hướng đến trang login
            response.sendRedirect("login.jsp");
            return;
        }

        // Nếu đã đăng nhập, hiển thị trang payment
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

        HttpSession session = request.getSession();

        // (1) Lấy param "bookingCCCD" từ form (nếu có)
        String bookingCCCDParam = request.getParameter("bookingCCCD");
        System.out.println("DEBUG PaymentServlet - CCCD param = " + bookingCCCDParam);

        // Nếu param rỗng => fallback lấy từ session
        String cccdBooking;
        if (bookingCCCDParam != null && !bookingCCCDParam.trim().isEmpty()) {
            cccdBooking = bookingCCCDParam.trim();
            session.setAttribute("bookingCCCD", cccdBooking);
        } else {
            cccdBooking = (String) session.getAttribute("bookingCCCD");
            if (cccdBooking == null || cccdBooking.trim().isEmpty()) {
                cccdBooking = "000000000000"; // fallback
            }
        }
        System.out.println("DEBUG PaymentServlet - CCCD final = " + cccdBooking);

        // (2) Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            // Nếu giỏ trống => chuyển về trang schedule (hoặc home)
            response.sendRedirect("schedule");
            return;
        }

        // (3) Kiểm tra xem user đã đăng nhập chưa
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // (4) Lấy thông tin người đặt vé (đã lưu trong session khi nhập passengerInfo)
        String bookingName = (String) session.getAttribute("bookingName");
        String bookingEmail = (String) session.getAttribute("bookingEmail");
        String bookingPhone = (String) session.getAttribute("bookingPhone");

        // (5) Phân tích các trip trong giỏ để xác định TripID và RoundTripTripID
        Set<Integer> distinctTripIDs = new HashSet<>();
        Integer goTripID = null;     // Chuyến đi
        Integer returnTripID = null; // Chuyến về

        for (CartItem item : cartItems) {
            // Nếu cartItem chưa set Trip => lỗi
            if (item.getTrip() == null) {
                request.setAttribute("error", "Một CartItem chưa có thông tin Trip. Không thể thanh toán!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            distinctTripIDs.add(item.getTrip().getTripID());

            // Xác định đâu là chuyến đi, đâu là chuyến về
            if (item.isReturnTrip()) {
                returnTripID = item.getTrip().getTripID();
            } else {
                goTripID = item.getTrip().getTripID();
            }
        }

        // (6) Tính tổng tiền từ giỏ
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice();
        }

        // (7) Tạo đối tượng Booking
        Booking booking = new Booking();
        booking.setUserID(user.getUserId());
        booking.setTotalPrice(totalPrice);
        booking.setPaymentStatus("Paid");    // Giả sử đã thanh toán
        booking.setBookingStatus("Active");  // Trạng thái booking còn hiệu lực

        // - Nếu có cả chuyến đi và chuyến về => khứ hồi
        if (goTripID != null && returnTripID != null) {
            booking.setTripID(goTripID);
            booking.setRoundTripTripID(returnTripID);
        } else {
            // - Nếu chỉ 1 chuyến => booking TripID = chuyến đầu tiên
            //   RoundTripTripID = null
            int singleTripID = cartItems.get(0).getTrip().getTripID();
            booking.setTripID(singleTripID);
            booking.setRoundTripTripID(null);
        }

        // (8) Insert Booking vào DB
        BookingDAO bookingDAO = new BookingDAO();
        int bookingID;
        try {
            bookingID = bookingDAO.insertBooking(booking);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo Booking: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        if (bookingID < 0) {
            request.setAttribute("error", "Không thể tạo Booking (bookingID < 0)!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // (9) Insert Ticket cho từng vé trong giỏ
        TicketDAO ticketDAO = new TicketDAO();

        // Lấy danh sách CCCD hành khách (được lưu trong sessionScope.idNumberList)
        List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
        if (idNumberList == null || idNumberList.size() != cartItems.size()) {
            request.setAttribute("error", "Không khớp số lượng CCCD với số vé trong giỏ!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // Duyệt qua từng CartItem để thêm Ticket
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            String passengerCCCD = idNumberList.get(i);

            // Tạo ticket
            Ticket ticket = new Ticket();
            ticket.setCccd(passengerCCCD);             // CCCD hành khách
            ticket.setBookingID(bookingID);            // bookingID vừa tạo
            int seatID = Integer.parseInt(item.getSeatID());
            ticket.setSeatID(seatID);                  // ghế
            ticket.setTripID(item.getTrip().getTripID());
            ticket.setTicketPrice(item.getPrice());
            ticket.setTicketStatus("Unused");

            try {
                int insertedTicketID = ticketDAO.insertTicket(ticket);
                // Đánh dấu ghế => "Booked"
                ticketDAO.updateSeatStatus(seatID, "Booked");

            } catch (SQLException ex) {
                ex.printStackTrace();
                // Nếu lỗi, có thể rollback Booking & các vé đã chèn => tùy yêu cầu
                request.setAttribute("error", "Lỗi khi thêm vé: " + ex.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        }

        // (10) Cập nhật PaymentStatus cho Booking = "Paid"
        try {
            boolean paymentUpdated = bookingDAO.updateBookingPaymentStatus(bookingID, "Paid");
            if (!paymentUpdated) {
                request.setAttribute("error", "Cập nhật trạng thái thanh toán thất bại!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi cập nhật thanh toán: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // (11) Xóa giỏ hàng khỏi session
        request.setAttribute("cartItems", cartItems);
        session.removeAttribute("cartItems");

        // (12) Gửi thông tin sang success.jsp
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("fullNameList", session.getAttribute("fullNameList"));
        request.setAttribute("idNumberList", session.getAttribute("idNumberList"));
        request.setAttribute("bookingName", bookingName);
        request.setAttribute("bookingEmail", bookingEmail);
        request.setAttribute("bookingPhone", bookingPhone);

        // (13) Forward sang trang "success.jsp"
        request.getRequestDispatcher("success.jsp").forward(request, response);
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
