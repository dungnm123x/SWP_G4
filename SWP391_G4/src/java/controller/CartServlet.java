/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dal.CartDAO;
import dal.StationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import model.CartItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.Trip;

/**
 *
 * @author Admin
 */
public class CartServlet extends HttpServlet {

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
            out.println("<title>Servlet CartServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CartServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    private final Gson gson = new Gson();

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
        // Lấy giỏ hàng từ session
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        // Forward về cart.jsp
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("cart.jsp").forward(request, response);
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

        // Lấy session + giỏ
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }
        String removeSeatID = request.getParameter("removeSeatID");
        if (removeSeatID != null && !removeSeatID.trim().isEmpty()) {
            removeItemFromCart(removeSeatID, request);

            // Sau khi xóa xong, forward về cart.jsp để lấy HTML cập nhật
            request.setAttribute("cartItems", session.getAttribute("cartItems"));
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        // 2) Nếu action=checkout => chuyển sang trang passengerinfo
        String action = request.getParameter("action");
//        if ("checkout".equals(action)) {
//            // Chuyển sang servlet/URL "passengerinfo"
//            response.sendRedirect("passengerinfo");
//            return;
//        }
        if ("checkout".equals(action)) {
            // Lấy tripID do form gửi lên
            String tripID = request.getParameter("tripID");
            String departureStationID = request.getParameter("departureStationID");
            String arrivalStationID = request.getParameter("arrivalStationID");
            String departureDay = request.getParameter("departureDay");
            String tripType = request.getParameter("tripType");
            String returnDate = request.getParameter("returnDate");
            // Nếu tripID bị null/rỗng => xử lý lỗi hoặc redirect
            if (tripID == null || tripID.trim().isEmpty()) {
                // ... Thông báo lỗi ...
            } else {
                // Gắn vào URL
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
        

        // 3) Nếu không phải xóa, thì là THÊM vé vào giỏ
        // Lấy param
        String ticketID = request.getParameter("ticketID");
        String trainName = request.getParameter("trainName");
        String departureDate = request.getParameter("departureDate");
        String carriageNumber = request.getParameter("carriageNumber");
        String seatNumber = request.getParameter("seatNumber");
        String seatID = request.getParameter("seatID");
        String priceStr = request.getParameter("price");

        // Param tìm kiếm
        String departureStationID = request.getParameter("departureStationID");
        String arrivalStationID = request.getParameter("arrivalStationID");
        String day = request.getParameter("departureDay");
        String tripType = request.getParameter("tripType");
        String returnDate = request.getParameter("returnDate");

        // Bắt buộc: tripID (để set Trip) - parse cẩn thận
        String tripIDStr = request.getParameter("tripID");
        int tripID = 0;
        if (tripIDStr == null || tripIDStr.trim().isEmpty()) {
            // Nếu thiếu tripID => có thể báo lỗi hoặc bỏ qua
            // Ở đây tạm báo lỗi
            request.setAttribute("message", "Thiếu tripID!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        } else {
            try {
                tripID = Integer.parseInt(tripIDStr);
            } catch (NumberFormatException e) {
                request.setAttribute("message", "tripID không hợp lệ!");
                request.getRequestDispatcher("cart.jsp").forward(request, response);
                return;
            }
        }

        // Kiểm tra input
        if (ticketID == null || seatID == null || priceStr == null
                || ticketID.trim().isEmpty() || seatID.trim().isEmpty() || priceStr.trim().isEmpty()) {
            request.setAttribute("message", "Dữ liệu không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        double price = 0.0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Giá trị không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        // Tạo Trip object
        Trip trip = new Trip();
        trip.setTripID(tripID);

        // Lấy tên ga từ DB (nếu cần) - tạm code cứng, hoặc StationDAO
        StationDAO stationDAO = new StationDAO();
        int depID = (departureStationID != null && !departureStationID.isEmpty())
                ? Integer.parseInt(departureStationID)
                : 0;
        String depStationName = stationDAO.getStationNameById(depID);

        int arrID = (arrivalStationID != null && !arrivalStationID.isEmpty())
                ? Integer.parseInt(arrivalStationID)
                : 0;
        String arrStationName = stationDAO.getStationNameById(arrID);

        // Kiểm tra param isReturnTrip
        String isReturnParam = request.getParameter("isReturnTrip");
        boolean returnTrip = "true".equalsIgnoreCase(isReturnParam);

        // Tạo CartItem
        CartItem newItem = new CartItem(
                ticketID, trainName, departureDate, carriageNumber, seatNumber, seatID,
                price, trip, depStationName, arrStationName, returnTrip
        );
        System.out.println("DEBUG: New CartItem added - " + newItem.getDepartureStationName()
                + " -> " + newItem.getArrivalStationName() + " | Return: " + newItem.isReturnTrip());

//        cartItems.add(newItem);
        // session.setAttribute("cartItems", cartItems);
        // Kiểm tra vé này đã tồn tại chưa
        boolean exists = false;
        for (CartItem item : cartItems) {
            if (item.getSeatID().equals(seatID)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            cartItems.add(newItem);
        }
        System.out.println("isReturnTrip param = " + request.getParameter("isReturnTrip"));

        session.setAttribute("cartItems", cartItems);
        session.setAttribute("isReturnTrip", returnTrip);
        System.out.println("Session returnTrip value: " + session.getAttribute("isReturnTrip"));
        request.setAttribute("cartItems", cartItems);
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
//         Redirect về schedule kèm param
//        String redirectURL = "schedule"
//                + "?departureStationID=" + URLEncoder.encode(departureStationID, "UTF-8")
//                + "&arrivalStationID=" + URLEncoder.encode(arrivalStationID, "UTF-8")
//                + "&departureDay=" + URLEncoder.encode(day == null ? "" : day, "UTF-8")
//                + "&tripType=" + URLEncoder.encode(tripType == null ? "" : tripType, "UTF-8")
//                + "&returnDate=" + URLEncoder.encode(returnDate == null ? "" : returnDate, "UTF-8");
//
//        response.sendRedirect(redirectURL);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String seatID = request.getParameter("seatID");
        removeItemFromCart(seatID, request);
        response.sendRedirect("cart.jsp");
    }

    private void removeItemFromCart(String seatID, HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems != null) {
            cartItems.removeIf(item -> item.getSeatID().equals(seatID));
        }
        session.setAttribute("cartItems", cartItems);
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
