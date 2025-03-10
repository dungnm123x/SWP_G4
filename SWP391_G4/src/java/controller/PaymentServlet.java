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

        // 1. Lấy param "bookingCCCD" từ form (nếu form payment.jsp gửi param này)
        String bookingCCCDParam = request.getParameter("bookingCCCD");
        System.out.println("DEBUG PaymentServlet - CCCD param = " + bookingCCCDParam);

        // 2. Nếu param rỗng => fallback session
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

        // 3. Lấy giỏ hàng
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect("schedule");
            return;
        }

        // 4. Kiểm tra user đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 5. Lấy bookingName, bookingEmail, bookingPhone
        String bookingName = (String) session.getAttribute("bookingName");
        String bookingEmail = (String) session.getAttribute("bookingEmail");
        String bookingPhone = (String) session.getAttribute("bookingPhone");

        // Tính tổng
        double totalPrice = 0.0;
        Trip firstTrip = cartItems.get(0).getTrip();
        if (firstTrip == null) {
            request.setAttribute("error", "CartItem chưa set Trip, không xác định được tripID!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        int tripID = firstTrip.getTripID();

        for (CartItem item : cartItems) {
            if (item.getTrip() == null) {
                request.setAttribute("error", "Một CartItem chưa set Trip. Không thể thanh toán!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            totalPrice += item.getPrice();
        }

        // 6. Tạo booking
        Booking booking = new Booking();
        booking.setUserID(user.getUserId());
        booking.setTripID(tripID);
        booking.setRoundTripTripID(null);
        booking.setTotalPrice(totalPrice);
        booking.setPaymentStatus("Paid");
        booking.setBookingStatus("Active");

        BookingDAO bookingDAO = new BookingDAO();
        int bookingID = -1;
        try {
            bookingID = bookingDAO.insertBooking(booking);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo booking: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        if (bookingID < 0) {
            request.setAttribute("error", "Không thể tạo Booking!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // 7. Insert ticket
        TicketDAO ticketDAO = new TicketDAO();

        // Lấy danh sách CCCD hành khách (đã nhập ở passengerInfo.jsp)
        // => sessionScope.idNumberList: index i tương ứng cartItems[i].
        List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);

            Ticket ticket = new Ticket();
            // Mỗi vé => CCCD hành khách = idNumberList[i]
            // chứ KHÔNG phải bookingCCCD (người đặt vé).
            ticket.setCccd(idNumberList.get(i));

            ticket.setBookingID(bookingID);

            int seatID = Integer.parseInt(item.getSeatID());
            ticket.setSeatID(seatID);

            ticket.setTripID(item.getTrip().getTripID());
            ticket.setTicketPrice(item.getPrice());
            ticket.setTicketStatus("Unused");

            try {
                int insertedTicketID = ticketDAO.insertTicket(ticket);

                // Update status
                ticketDAO.updateTicketStatus(insertedTicketID, "Used");
                ticketDAO.updateSeatStatus(seatID, "Booked");

            } catch (Exception ex) {
                ex.printStackTrace();
                // rollback hoặc xử lý
            }
        }

        // 8. Update payment
        try {
            boolean paymentUpdated = bookingDAO.updateBookingPaymentStatus(bookingID, "Paid");
            if (!paymentUpdated) {
                request.setAttribute("error", "Cập nhật trạng thái thanh toán thất bại!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi cập nhật thanh toán: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        // 9. Xóa giỏ
        request.setAttribute("cartItems", cartItems);
        session.removeAttribute("cartItems");

        // 10. Forward success
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("fullNameList", session.getAttribute("fullNameList"));
        request.setAttribute("idNumberList", session.getAttribute("idNumberList"));
        request.setAttribute("bookingName", bookingName);
        request.setAttribute("bookingEmail", bookingEmail);
        request.setAttribute("bookingPhone", bookingPhone);

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
