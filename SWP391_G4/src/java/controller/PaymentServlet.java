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

        // Nếu chưa đăng nhập => hiển popup => login
        if (user == null) {
            request.setAttribute("mustLogin", true);
            request.getRequestDispatcher("payment.jsp").forward(request, response);
            return;
        }

        // Nếu đã đăng nhập => hiển thị payment.jsp
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

        // Lấy thông tin người đặt vé từ session
        String bookingName = (String) session.getAttribute("bookingName");
        String bookingEmail = (String) session.getAttribute("bookingEmail");
        String bookingPhone = (String) session.getAttribute("bookingPhone");

// Kiểm tra nếu giá trị bị null (có thể do bị mất session)
        if (bookingName == null) {
            bookingName = "Không có thông tin";
        }
        if (bookingEmail == null) {
            bookingEmail = "Không có thông tin";
        }
        if (bookingPhone == null) {
            bookingPhone = "Không có thông tin";
        }

// Lưu vào request để truyền sang trang success.jsp
        request.setAttribute("bookingName", bookingName);
        request.setAttribute("bookingEmail", bookingEmail);
        request.setAttribute("bookingPhone", bookingPhone);

        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Chưa đăng nhập => chuyển login
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy giỏ hàng từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            // Giỏ rỗng => quay lại trang schedule
            response.sendRedirect("schedule");
            return;
        }

        // Lấy phương thức thanh toán
        String paymentMethod = request.getParameter("paymentMethod");
        // Tuỳ ý xử lý paymentMethod (creditCard/eWallet/bankTransfer)...

        // Tính tổng tiền
        double totalPrice = 0.0;
        // Lấy TripID từ item đầu (giả sử giỏ hàng chỉ chứa vé cho 1 chuyến)
        Trip firstTrip = cartItems.get(0).getTrip();
        if (firstTrip == null) {
            // Nếu cartItem chưa set Trip => báo lỗi
            request.setAttribute("error", "CartItem chưa set Trip, không xác định được tripID!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        int tripID = firstTrip.getTripID();

        // Cộng dồn giá
        for (CartItem item : cartItems) {
            // Kiểm tra item.getTrip() != null
            if (item.getTrip() == null) {
                request.setAttribute("error", "Một CartItem chưa set Trip. Không thể thanh toán!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            totalPrice += item.getPrice();
        }

        // Tạo 1 booking
        Booking booking = new Booking();
        booking.setUserID(user.getUserId());  // userID = người mua
        booking.setTripID(tripID);
        booking.setRoundTripTripID(null);     // Nếu khứ hồi => set ID chuyến về
        booking.setTotalPrice(totalPrice);
        booking.setPaymentStatus("Paid");     // Giả sử
        booking.setBookingStatus("Active");   // Giả sử

        // Insert booking
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

        // Insert ticket cho từng CartItem
        TicketDAO ticketDAO = new TicketDAO();
        String cccdBooking = (String) session.getAttribute("bookingCCCD");
        if (cccdBooking == null) {
            cccdBooking = "000000000000"; // fallback nếu chưa nhập
        }
        for (CartItem item : cartItems) {
            Ticket ticket = new Ticket();
            ticket.setCccd(cccdBooking);
            ticket.setBookingID(bookingID);

            int seatID = Integer.parseInt(item.getSeatID());
            ticket.setSeatID(seatID);

            ticket.setTripID(item.getTrip().getTripID());
            ticket.setTicketPrice(item.getPrice());
            ticket.setTicketStatus("Unused");

            try {
                // Chèn vé 1 lần duy nhất
                int insertedTicketID = ticketDAO.insertTicket(ticket);

                // Nếu muốn chuyển vé sang "Used" ngay lập tức (thường vé mới tạo sẽ là "Unused")
                // thì updateTicketStatus như sau:
                ticketDAO.updateTicketStatus(insertedTicketID, "Used");

                // Đánh dấu ghế đã Booked
                ticketDAO.updateSeatStatus(seatID, "Booked");

            } catch (Exception ex) {
                ex.printStackTrace();
                // Xử lý rollback hoặc báo lỗi nếu cần
            }
        }

        // Xoá giỏ hàng
        request.setAttribute("cartItems", session.getAttribute("cartItems"));
        session.removeAttribute("cartItems"); // Chỉ xóa sau khi đã lưu vào request

        // Forward sang trang "paymentSuccess.jsp"
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("fullNameList", session.getAttribute("fullNameList"));
        request.setAttribute("idNumberList", session.getAttribute("idNumberList"));
        request.setAttribute("bookingName", session.getAttribute("bookingName"));
        request.setAttribute("bookingEmail", session.getAttribute("bookingEmail"));
        request.setAttribute("bookingPhone", session.getAttribute("bookingPhone"));

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
