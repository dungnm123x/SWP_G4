/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package VNPay;

import Utils.BookingEmailSender;
import controller.CartServlet;
import dal.BookingDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Booking;
import model.CartItem;
import model.Ticket;
import model.User;

/**
 *
 * @author Admin
 */
public class ReturnResult extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReturnResult</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReturnResult at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Lấy session + user
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // Nếu chưa đăng nhập => chuyển login
            response.sendRedirect("login.jsp");
            return;
        }

        // 2) Đọc các tham số trả về từ VNPay
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_BankCode = request.getParameter("vnp_BankCode");
        String vnp_PayDate_raw = request.getParameter("vnp_PayDate");
        String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
        System.out.println("TransactionStatus: " + vnp_TransactionStatus);

        // (Tùy chọn) parse thời gian thanh toán
        String vnp_PayDate = "";
        if (vnp_PayDate_raw != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            try {
                Date payDate = inputFormat.parse(vnp_PayDate_raw);
                vnp_PayDate = outputFormat.format(payDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 3) Gắn các tham số lên request để hiển thị nếu cần
        request.setAttribute("vnp_TxnRef", vnp_TxnRef);
        request.setAttribute("vnp_Amount", vnp_Amount);
        request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
        request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
        request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
        request.setAttribute("vnp_BankCode", vnp_BankCode);
        request.setAttribute("vnp_PayDate", vnp_PayDate);
        request.setAttribute("vnp_TransactionStatus", vnp_TransactionStatus);

        // 4) Lấy giỏ vé (cartItems) từ session
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            response.sendRedirect("schedule");
            return;
        }

        // Lấy thông tin người đặt vé (đã lưu ở session)
        String bookingName = (String) session.getAttribute("bookingName");
        String bookingEmail = (String) session.getAttribute("bookingEmail");
        String bookingPhone = (String) session.getAttribute("bookingPhone");

        // 5) Kiểm tra kết quả thanh toán: "00" = thành công
        if ("00".equals(vnp_TransactionStatus)) {
            // A) Tính tổng tiền
            double totalPrice = 0;
            if (session.getAttribute("totalAmount") != null) {
                totalPrice = (double) session.getAttribute("totalAmount");
            } else {
                try {
                    long vnpAmountLong = Long.parseLong(vnp_Amount);
                    totalPrice = vnpAmountLong / 100.0;
                } catch (NumberFormatException e) {
                    for (CartItem item : cartItems) {
                        totalPrice += item.getPrice();
                    }
                }
            }

            // B) Tạo Booking (PaymentStatus = "Paid")
            Booking booking = new Booking();
            booking.setUserID(user.getUserId());
            booking.setTotalPrice(totalPrice);
            booking.setPaymentStatus("Paid");
            booking.setBookingStatus("Active");

            // Xác định chuyến đi / chuyến về
            Integer goTripID = null;
            Integer returnTripID = null;
            for (CartItem item : cartItems) {
                if (item.isReturnTrip()) {
                    returnTripID = item.getTrip().getTripID();
                } else {
                    goTripID = item.getTrip().getTripID();
                }
            }
            if (goTripID != null && returnTripID != null) {
                booking.setTripID(goTripID);
                booking.setRoundTripTripID(returnTripID);
            } else {
                booking.setTripID(cartItems.get(0).getTrip().getTripID());
                booking.setRoundTripTripID(null);
            }

            // C) Insert Booking
            BookingDAO bookingDAO = new BookingDAO();
            int bookingID = -1;
            try {
                bookingID = bookingDAO.insertBooking(booking);
            } catch (SQLException ex) {
                ex.printStackTrace();
                request.setAttribute("error", "Lỗi khi tạo Booking: " + ex.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            if (bookingID < 0) {
                request.setAttribute("error", "Không thể tạo Booking (bookingID < 0)!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // D) Tạo Ticket + update ghế
            TicketDAO ticketDAO = new TicketDAO();
            List<String> fullNameList = (List<String>) session.getAttribute("fullNameList");
            List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
            List<String> typeList = (List<String>) session.getAttribute("typeList");
            List<Double> finalPriceList = (List<Double>) session.getAttribute("finalPriceList");
            if (idNumberList == null || fullNameList == null || typeList == null 
                    || idNumberList.size() != cartItems.size() 
                    || fullNameList.size() != cartItems.size() 
                    || typeList.size() != cartItems.size()) {
                request.setAttribute("error", "Không khớp số lượng tên/CCCD với số vé trong giỏ!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            for (int i = 0; i < cartItems.size(); i++) {
                CartItem item = cartItems.get(i);
                String passengerCCCD = idNumberList.get(i);
                String passengerName = fullNameList.get(i);
                String passengerType = typeList.get(i);
                Ticket ticket = new Ticket();

                ticket.setPassengerName(passengerName);
                ticket.setPassengerType(passengerType);
                ticket.setCccd(passengerCCCD);
                ticket.setBookingID(bookingID);
                ticket.setSeatID(Integer.parseInt(item.getSeatID()));
                ticket.setTripID(item.getTrip().getTripID());
                double ticketPrice = (finalPriceList != null && finalPriceList.size() > i)
                        ? finalPriceList.get(i)
                        : item.getPrice();
                ticket.setTicketPrice(ticketPrice);
                ticket.setTicketStatus("Unused");

                try {
                    ticketDAO.insertTicket(ticket);
                    ticketDAO.updateSeatStatus(ticket.getSeatID(), "Booked");
                    item.setPrice(ticketPrice);
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Lỗi khi thêm vé: " + e.getMessage());
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                    return;
                }
            }

            // E) Xoá giỏ hàng
            session.removeAttribute("cartItems");

            // F) Forward sang trang success
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("fullNameList", session.getAttribute("fullNameList"));
            request.setAttribute("idNumberList", session.getAttribute("idNumberList"));
            request.setAttribute("typeList", session.getAttribute("typeList"));
            request.setAttribute("bookingName", bookingName);
            request.setAttribute("bookingEmail", bookingEmail);
            request.setAttribute("bookingPhone", bookingPhone);
            Object totalAmountValue = session.getAttribute("totalAmount");
            request.setAttribute("totalAmount", totalAmountValue);

            request.getRequestDispatcher("success.jsp").forward(request, response);
            try {
                BookingEmailSender.sendBookingSuccessEmail(
                        user.getEmail(),
                        bookingName,
                        cartItems,
                        fullNameList,
                        idNumberList,
                        typeList,
                        bookingPhone
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Xóa các thuộc tính lưu trong session sau khi đặt vé thành công
            session.removeAttribute("cartItems");
            session.removeAttribute("fullNameList");
            session.removeAttribute("typeList");
            session.removeAttribute("idNumberList");
            session.removeAttribute("birthDayList");
            session.removeAttribute("birthMonthList");
            session.removeAttribute("birthYearList");
            session.removeAttribute("totalAmount");
            session.removeAttribute("bookingCCCD");
            System.out.println("Thanh toán thành công");
        } else {
            // Giao dịch không thành công => chuyển trang fail.jsp
            System.out.println("Thanh toán KHÔNG thành công hoặc thiếu thông tin");
            request.getRequestDispatcher("failpaymet.jsp").forward(request, response);
        }
    }

    private static String formatNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###.###");
        return formatter.format(number);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
