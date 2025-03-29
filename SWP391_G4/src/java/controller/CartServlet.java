package controller;

import dal.SeatDAO;
import dal.StationDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import model.CartItem;
import model.Trip;

public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        // Xử lý xóa vé
        String removeSeatID = request.getParameter("removeSeatID");
        if (removeSeatID != null && !removeSeatID.trim().isEmpty()) {
            removeItemFromCart(removeSeatID, request);
            request.setAttribute("cartItems", session.getAttribute("cartItems"));
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        // Xử lý "checkout"
        String action = request.getParameter("action");
        if ("checkout".equals(action)) {
            // Lấy tham số, redirect sang passengerinfo
            String tripID = request.getParameter("tripID");
            String departureStationID = request.getParameter("departureStationID");
            String arrivalStationID = request.getParameter("arrivalStationID");
            String departureDay = request.getParameter("departureDay");
            String tripType = request.getParameter("tripType");
            String returnDate = request.getParameter("returnDate");
            if (tripID == null || tripID.trim().isEmpty()) {
                // ... báo lỗi ...
            } else {
                String redirectURL = "passengerinfo"
                        + "?tripID=" + URLEncoder.encode(tripID, "UTF-8")
                        + "&departureStationID=" + URLEncoder.encode(departureStationID, "UTF-8")
                        + "&arrivalStationID=" + URLEncoder.encode(arrivalStationID, "UTF-8")
                        + "&departureDay=" + URLEncoder.encode(departureDay, "UTF-8")
                        + "&tripType=" + URLEncoder.encode(tripType, "UTF-8")
                        + "&returnDate=" + URLEncoder.encode(returnDate == null ? "" : returnDate, "UTF-8");

                response.sendRedirect(redirectURL);
                return;
            }
        }

        // Trường hợp THÊM vé vào giỏ
        String ticketID = request.getParameter("ticketID");
        String trainName = request.getParameter("trainName");
        String departureDate = request.getParameter("departureDate");
        String carriageNumber = request.getParameter("carriageNumber");
        String seatNumber = request.getParameter("seatNumber");
        String seatID = request.getParameter("seatID");
        String priceStr = request.getParameter("price");
        String tripIDStr = request.getParameter("tripID");

        if (seatID == null || seatID.trim().isEmpty()) {
            request.setAttribute("message", "Thiếu seatID!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            request.setAttribute("message", "Giá vé không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        // Tạo Trip
        int tripID = 0;
        try {
            tripID = Integer.parseInt(tripIDStr);
        } catch (Exception e) {
            request.setAttribute("message", "tripID không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }
        Trip trip = new Trip();
        trip.setTripID(tripID);

        // Tạo CartItem
        StationDAO stationDAO = new StationDAO();
        String departureStationID = request.getParameter("departureStationID");
        String arrivalStationID = request.getParameter("arrivalStationID");
        int depID = (departureStationID != null && !departureStationID.isEmpty()) ? Integer.parseInt(departureStationID) : 0;
        int arrID = (arrivalStationID != null && !arrivalStationID.isEmpty()) ? Integer.parseInt(arrivalStationID) : 0;
        String depStationName = stationDAO.getStationNameById(depID);
        String arrStationName = stationDAO.getStationNameById(arrID);

        String isReturnParam = request.getParameter("isReturnTrip");
        boolean returnTrip = "true".equalsIgnoreCase(isReturnParam);

        CartItem newItem = new CartItem(
                ticketID, trainName, departureDate, carriageNumber, seatNumber, seatID,
                price, trip, depStationName, arrStationName, returnTrip
        );

        // Kiểm tra ghế đã tồn tại trong giỏ chưa
        boolean exists = false;
        for (CartItem ci : cartItems) {
            if (ci.getSeatID().equals(seatID)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            // 1) Thêm vào giỏ
            cartItems.add(newItem);
            // 2) Update DB => cập nhật trạng thái ghế thành "Booked"
            SeatDAO seatDAO = new SeatDAO();
            seatDAO.updateSeatStatus(seatID, "Booked");
        }

        session.setAttribute("cartItems", cartItems);
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }

    private void removeItemFromCart(String seatID, HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null) {
            cartItems.removeIf(ci -> ci.getSeatID().equals(seatID));
        }
        session.setAttribute("cartItems", cartItems);
        SeatDAO seatDAO = new SeatDAO();
        seatDAO.updateSeatStatus(seatID, "Available");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị giỏ
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }
}
