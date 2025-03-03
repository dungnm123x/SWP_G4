/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dal.CartDAO;
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
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
//
//        if (cartItems == null) {
//            cartItems = new ArrayList<>();
//            session.setAttribute("cartItems", cartItems);
//        }
//
//        request.setAttribute("cartItems", cartItems);
//        request.getRequestDispatcher("cart.jsp").forward(request, response);
//    }
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
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String ticketID = request.getParameter("ticketID");
//        String trainName = request.getParameter("trainName");
//        String departureDate = request.getParameter("departureDate");
//        String carriageNumber = request.getParameter("carriageNumber");
//        String seatNumber = request.getParameter("seatNumber");
//        String seatID = request.getParameter("seatID");
//        String priceStr = request.getParameter("price");
//
//        if (ticketID == null || seatID == null || priceStr == null || ticketID.trim().isEmpty()
//                || seatID.trim().isEmpty() || priceStr.trim().isEmpty()) {
//            request.setAttribute("message", "Dữ liệu không hợp lệ!");
//            request.getRequestDispatcher("cart.jsp").forward(request, response);
//            return;
//        }
//
//        double price;
//        try {
//            price = Double.parseDouble(priceStr);
//        } catch (NumberFormatException e) {
//            request.setAttribute("message", "Giá trị không hợp lệ!");
//            request.getRequestDispatcher("cart.jsp").forward(request, response);
//            return;
//        }
//
//        CartItem newItem = new CartItem(ticketID, trainName, departureDate, carriageNumber, seatNumber, seatID, price);
//        HttpSession session = request.getSession();
//        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
//
//        if (cartItems == null) {
//            cartItems = new ArrayList<>();
//        }
//
//        boolean exists = cartItems.stream().anyMatch(item -> item.getSeatID().equals(seatID));
//
//        if (!exists) {
//            cartItems.add(newItem);
//        }
//
//        session.setAttribute("cartItems", cartItems);
//        String redirectURL = "schedule?departureStation=" + URLEncoder.encode(request.getParameter("departureStation"), "UTF-8")
//                + "&arrivalStation=" + URLEncoder.encode(request.getParameter("arrivalStation"), "UTF-8")
//                + "&departureDay=" + URLEncoder.encode(request.getParameter("departureDay"), "UTF-8")
//                + "&tripType=" + URLEncoder.encode(request.getParameter("tripType"), "UTF-8");
//
//        response.sendRedirect(redirectURL);
//    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra xem đây là yêu cầu xóa hay thêm
        String removeSeatID = request.getParameter("removeSeatID");
        if (removeSeatID != null && !removeSeatID.trim().isEmpty()) {
            removeItemFromCart(removeSeatID, request);

            // Đọc lại param (nếu bạn đang dùng)
            String departureStationID = request.getParameter("departureStationID");
            String arrivalStationID = request.getParameter("arrivalStationID");
            String day = request.getParameter("departureDay");
            String tripType = request.getParameter("tripType");
            String returnDate = request.getParameter("returnDate");

            // Giống như khi thêm vé, ta redirect kèm param
            String redirectURL = "schedule"
                    + "?departureStationID=" + URLEncoder.encode(departureStationID == null ? "" : departureStationID, "UTF-8")
                    + "&arrivalStationID=" + URLEncoder.encode(arrivalStationID == null ? "" : arrivalStationID, "UTF-8")
                    + "&departureDay=" + URLEncoder.encode(day == null ? "" : day, "UTF-8")
                    + "&tripType=" + URLEncoder.encode(tripType == null ? "" : tripType, "UTF-8")
                    + "&returnDate=" + URLEncoder.encode(returnDate == null ? "" : returnDate, "UTF-8");

            response.sendRedirect(redirectURL);
            return;
        }

        // Nếu không phải xóa, thì là thêm
        String ticketID = request.getParameter("ticketID");
        String trainName = request.getParameter("trainName");
        String departureDate = request.getParameter("departureDate");
        String carriageNumber = request.getParameter("carriageNumber");
        String seatNumber = request.getParameter("seatNumber");
        String seatID = request.getParameter("seatID");
        String priceStr = request.getParameter("price");

        // Các param tìm kiếm
        String departureStationID = request.getParameter("departureStationID");
        String arrivalStationID = request.getParameter("arrivalStationID");
        String day = request.getParameter("departureDay");
        String tripType = request.getParameter("tripType");
        String returnDate = request.getParameter("returnDate");

        if (ticketID == null || seatID == null || priceStr == null
                || ticketID.trim().isEmpty() || seatID.trim().isEmpty() || priceStr.trim().isEmpty()) {
            request.setAttribute("message", "Dữ liệu không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Giá trị không hợp lệ!");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        // Tạo CartItem
        CartItem newItem = new CartItem(ticketID, trainName, departureDate, carriageNumber, seatNumber, seatID, price);

        // Lấy session và giỏ
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Kiểm tra tồn tại
        boolean exists = false;
        for (CartItem item : cartItems) {
            if (item.getSeatID().equals(seatID)) {
                exists = true;
                break;
            }
        }

        // Thêm nếu chưa tồn tại
        if (!exists) {
            cartItems.add(newItem);
        }

        session.setAttribute("cartItems", cartItems);

        // Redirect về /schedule kèm param để không mất dữ liệu
        String redirectURL = "schedule"
                + "?departureStationID=" + URLEncoder.encode(departureStationID, "UTF-8")
                + "&arrivalStationID=" + URLEncoder.encode(arrivalStationID, "UTF-8")
                + "&departureDay=" + URLEncoder.encode(day, "UTF-8")
                + "&tripType=" + URLEncoder.encode(tripType, "UTF-8")
                + "&returnDate=" + URLEncoder.encode(returnDate == null ? "" : returnDate, "UTF-8");

        response.sendRedirect(redirectURL);
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
