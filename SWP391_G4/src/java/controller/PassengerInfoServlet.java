/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.CartItem;

/**
 *
 * @author Admin
 */
public class PassengerInfoServlet extends HttpServlet {

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
            out.println("<title>Servlet PassengerInfoServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PassengerInfoServlet at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        // Lấy action
        String action = request.getParameter("action");
        // Lấy param để quay lại schedule
        String depID = request.getParameter("departureStationID");
        String arrID = request.getParameter("arrivalStationID");
        String dDay = request.getParameter("departureDay");
        String tType = request.getParameter("tripType");
        String rDate = request.getParameter("returnDate");

        // Lấy session + cart
        HttpSession session = request.getSession();
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

//        // 1) Nếu action=clearAll => xóa hết
//        if ("clearAll".equals(action)) {
//            cartItems.clear(); // Hoặc session.removeAttribute("cartItems");
//            // Quay lại schedule
//            String redirectURL = "schedule"
//                    + "?departureStationID=" + URLEncoder.encode(depID == null ? "" : depID, "UTF-8")
//                    + "&arrivalStationID=" + URLEncoder.encode(arrID == null ? "" : arrID, "UTF-8")
//                    + "&departureDay=" + URLEncoder.encode(dDay == null ? "" : dDay, "UTF-8")
//                    + "&tripType=" + URLEncoder.encode(tType == null ? "" : tType, "UTF-8")
//                    + "&returnDate=" + URLEncoder.encode(rDate == null ? "" : rDate, "UTF-8");
//            response.sendRedirect(redirectURL);
//            return;
//        }
//
//        // 2) Nếu action=removeOne => xóa 1 vé
//        if ("removeOne".equals(action)) {
//            String seatIndexStr = request.getParameter("seatIndex");
//            // Hoặc seatIDStr = request.getParameter("seatID");
//            if (seatIndexStr != null) {
//                int idx = Integer.parseInt(seatIndexStr);
//                if (idx >= 0 && idx < cartItems.size()) {
//                    cartItems.remove(idx);
//                }
//            }
//            // Quay lại schedule
//            String redirectURL = "schedule"
//                    + "?departureStationID=" + URLEncoder.encode(depID == null ? "" : depID, "UTF-8")
//                    + "&arrivalStationID=" + URLEncoder.encode(arrID == null ? "" : arrID, "UTF-8")
//                    + "&departureDay=" + URLEncoder.encode(dDay == null ? "" : dDay, "UTF-8")
//                    + "&tripType=" + URLEncoder.encode(tType == null ? "" : tType, "UTF-8")
//                    + "&returnDate=" + URLEncoder.encode(rDate == null ? "" : rDate, "UTF-8");
//            response.sendRedirect(redirectURL);
//            return;
//        }
        if ("clearAll".equals(action)) {
            cartItems.clear();
            // Quay lại schedule (hoặc mapping tùy ý)
            response.sendRedirect("schedule");
            return;
        }

        // TH2: Xóa 1 vé
        if ("removeOne".equals(action)) {
            String seatIndexStr = request.getParameter("seatIndex");
            if (seatIndexStr != null) {
                int idx = Integer.parseInt(seatIndexStr);
                if (idx >= 0 && idx < cartItems.size()) {
                    cartItems.remove(idx);
                }
            }
            // Nếu sau khi xóa, giỏ vẫn còn vé => ở lại trang passengerInfo
            if (!cartItems.isEmpty()) {
                // forward (hoặc redirect) về chính trang passengerInfo.jsp
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            } else {
                // Nếu hết vé => quay lại schedule
                response.sendRedirect("schedule");
                return;
            }
        }
        // 3) Nếu không phải clearAll/removeOne => user bấm “Tiếp tục”
        // => Xử lý logic passengerCount, tính discount, v.v.
        int passengerCount = Integer.parseInt(request.getParameter("passengerCount"));
        double totalAmount = 0.0;

        // Tạo Set để kiểm tra trùng (fullName + idNumber)
        Set<String> usedSet = new HashSet<>();

        for (int i = 0; i < passengerCount; i++) {
            String fullName = request.getParameter("fullName" + i);
            String passengerType = request.getParameter("passengerType" + i);
            String idNumber = request.getParameter("idNumber" + i);
            double basePrice = Double.parseDouble(request.getParameter("price" + i));

            // Kiểm tra trùng
            String key = fullName.trim().toLowerCase() + "#" + idNumber.trim().toLowerCase();
            if (usedSet.contains(key)) {
                request.setAttribute("errorMessage",
                        "Không được trùng Họ tên + CMND/Hộ chiếu: " + fullName + " - " + idNumber);
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            } else {
                usedSet.add(key);
            }

            // Lấy số hành khách
            double discountRate = 0.0; // Mặc định 0%

            if ("Trẻ em".equals(passengerType)) {
                // Lấy ngày sinh
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);

                int age = calculateAge(dayStr, monthStr, yearStr);
                // Quy định: <6 => không cần vé, 6..10 => 50%, >10 => không phải trẻ em
                if (age < 6) {
                    request.setAttribute("errorMessage",
                            "Trẻ em dưới 6 tuổi không cần vé: " + fullName);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                } else if (age >= 6 && age <= 10) {
                    discountRate = 50.0;
                } else {
                    request.setAttribute("errorMessage",
                            "Hành khách '" + fullName + "' không đúng độ tuổi Trẻ em (6-10).");
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }

            } else if ("Người cao tuổi".equals(passengerType)) {
                // Lấy ngày sinh
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);

                int age = calculateAge(dayStr, monthStr, yearStr);
                // >=60 => giảm 30%
                if (age >= 60) {
                    discountRate = 30.0;
                } else {
                    request.setAttribute("errorMessage",
                            "Hành khách '" + fullName + "' chưa đủ 60 tuổi để giảm giá người cao tuổi.");
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }

            } else if ("Sinh viên".equals(passengerType)) {
                discountRate = 20.0;

            } else if ("VIP".equals(passengerType)) {
                // Kiểm tra thẻ VIP
                String vipCard = request.getParameter("vipCard" + i);
                if (vipCard == null || vipCard.trim().isEmpty()) {
                    request.setAttribute("errorMessage",
                            "Vui lòng nhập thẻ VIP cho hành khách: " + fullName);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
                discountRate = 10.0;

            }
            // Người lớn => discountRate = 0

            // Tính finalPrice
            double discountAmount = basePrice * discountRate / 100.0;
            double finalPrice = basePrice - discountAmount + 1000;
            totalAmount += finalPrice;
        }

        // Lưu totalAmount => chuyển sang 1 trang confirm.jsp (hoặc hiển thị)
        request.setAttribute("totalAmount", totalAmount);
        request.getRequestDispatcher("confirm.jsp").forward(request, response);
    }
    // Hàm tính tuổi ở server

    private int calculateAge(String dayStr, String monthStr, String yearStr) {
        try {
            int d = Integer.parseInt(dayStr);
            int m = Integer.parseInt(monthStr);
            int y = Integer.parseInt(yearStr);
            LocalDate birthDate = LocalDate.of(y, m, d);
            LocalDate now = LocalDate.now();
            int age = now.getYear() - birthDate.getYear();
            // Kiểm tra đã qua sinh nhật chưa
            if (now.getMonthValue() < m || (now.getMonthValue() == m && now.getDayOfMonth() < d)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            // Parse lỗi => -1
            return -1;
        }
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
