/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.SeatDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import model.CartItem;
import model.User;

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

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", "passengerinfo");
            response.sendRedirect("login.jsp");
            return;
        }

        Map<String, Boolean> confirmedMap = (Map<String, Boolean>) session.getAttribute("confirmedDOBMap");
        if (confirmedMap == null) {
            confirmedMap = new HashMap<>();
            session.setAttribute("confirmedDOBMap", confirmedMap);
        }

// Mỗi item => key = seatID (hoặc unique)
// Mỗi lần confirmDOB => confirmedMap.put(seatID, true)
// Lúc render => data-confirmedDOB="${confirmedMap[item.seatID]}"
            // GÁN TỪ USER (profile) sang bookingName, bookingEmail, ...
        session.setAttribute("bookingName", user.getFullName());
        session.setAttribute("bookingEmail", user.getEmail());
        session.setAttribute("bookingPhone", user.getPhoneNumber());

        // (Hoặc .getIdentityNumber() tùy bạn đặt tên cột)
        // ... (phần code tạo previousURL)
        String depID = request.getParameter("departureStationID");
        String arrID = request.getParameter("arrivalStationID");
        String dDay = request.getParameter("departureDay");
        String tType = request.getParameter("tripType");
        String rDate = request.getParameter("returnDate");

        String previousURL = "schedule"
                + "?departureStationID=" + URLEncoder.encode(depID == null ? "" : depID, "UTF-8")
                + "&arrivalStationID=" + URLEncoder.encode(arrID == null ? "" : arrID, "UTF-8")
                + "&departureDay=" + URLEncoder.encode(dDay == null ? "" : dDay, "UTF-8")
                + "&tripType=" + URLEncoder.encode(tType == null ? "" : tType, "UTF-8")
                + "&returnDate=" + URLEncoder.encode(rDate == null ? "" : rDate, "UTF-8");
        session.setAttribute("previousURL", previousURL);

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

        // Lấy action
        String action = request.getParameter("action");
        String renderPartial = request.getParameter("renderPartial");
        // Lấy param
        String departureStationID = request.getParameter("departureStationID");
        String arrivalStationID = request.getParameter("arrivalStationID");
        String departureDay = request.getParameter("departureDay");
        String tripType = request.getParameter("tripType");
        String returnDate = request.getParameter("returnDate");
        // true nếu khứ hồi
        String rDate = request.getParameter("returnDate");
        HttpSession session = request.getSession();
//        session.setAttribute("selectedDeparture", departureStationID);
//        session.setAttribute("selectedArrival", arrivalStationID);
//        session.setAttribute("selectedDate", departureDay);
//        session.setAttribute("selectedTripType", tripType);
//        session.setAttribute("selectedReturnDate", returnDate);
        // Lấy session + giỏ

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }
        System.out.println("Trước khi xóa, giỏ hàng có: " + cartItems.size() + " vé.");
        System.out.println("Sau khi xóa, giỏ hàng còn: " + cartItems.size() + " vé.");
        // Kiểm tra null, nếu null thì thay bằng chuỗi rỗng
        String redirectURL = "schedule"
                + "?departureStationID=" + URLEncoder.encode(departureStationID != null ? departureStationID : "", "UTF-8")
                + "&arrivalStationID=" + URLEncoder.encode(arrivalStationID != null ? arrivalStationID : "", "UTF-8")
                + "&departureDay=" + URLEncoder.encode(departureDay != null ? departureDay : "", "UTF-8")
                + "&tripType=" + URLEncoder.encode(tripType != null ? tripType : "", "UTF-8")
                + "&returnDate=" + URLEncoder.encode(returnDate != null ? returnDate : "", "UTF-8");

        // Lưu vào session để sử dụng khi cần quay lại
        session.setAttribute("previousURL", redirectURL);

        // 1) Nếu action = "clearAll" => Xóa tất cả vé nhưng vẫn giữ lại URL tìm kiếm
        if ("clearAll".equals(action)) {
            if (cartItems != null) {
                SeatDAO seatDAO = new SeatDAO();
                for (CartItem item : cartItems) {
                    seatDAO.updateSeatStatus(item.getSeatID(), "Available");
                }
                cartItems.clear();
                session.removeAttribute("cartItems");  // Xóa giỏ hàng khỏi session
            }

            // Quay lại trang schedule với URL trước đó
            if (redirectURL == null || redirectURL.isEmpty()) {
                redirectURL = "schedule?departureStationID=defaultValue&arrivalStationID=defaultValue&departureDay=defaultValue&tripType=defaultValue&returnDate=defaultValue";
            }
            response.sendRedirect(redirectURL);

            return;
        }

        // 2) Nếu action = "removeOne" => Xóa 1 vé nhưng vẫn giữ lại URL tìm kiếm
        if ("removeOne".equals(action)) {
            String seatID = request.getParameter("seatID");
            if (seatID != null) {
                // Xóa item
                cartItems.removeIf(item
                        -> (item.getTrainName() + "_" + item.getDepartureDate() + "_"
                                + item.getCarriageNumber() + "_" + item.getSeatNumber()).equals(seatID)
                );
            }

            // Nếu giỏ đã rỗng => redirect
            if (cartItems.isEmpty()) {
                // Gửi phản hồi đặc biệt cho AJAX
                if ("true".equals(request.getParameter("renderPartial"))) {
                    // Chỉ cần in 1 đoạn JSON hay text “EMPTY” 
                    // => client sẽ handle để window.location = ...
                    response.setContentType("text/plain");
                    response.getWriter().write("EMPTY");
                    return;
                } else {
                    // Trường hợp user submit form thường
                    response.sendRedirect(redirectURL);
                    return;
                }
            }

            // Nếu vẫn còn vé
            if ("true".equals(request.getParameter("renderPartial"))) {
                // partialMode => forward sang JSP
                request.setAttribute("partialMode", true);
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            } else {
                // Forward full
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            }
        }

//        if ("removeOne".equals(action)) {
//            String seatID = request.getParameter("seatID");
//            System.out.println("Request seatID to remove: " + seatID);
//
//            if (seatID != null && !seatID.isEmpty()) {
//                boolean removed = cartItems.removeIf(item
//                        -> (item.getTrainName() + "_" + item.getDepartureDate() + "_" + item.getCarriageNumber() + "_" + item.getSeatNumber()).equals(seatID)
//                );
//
//                if (removed) {
//                    System.out.println("Successfully removed seat: " + seatID);
//                } else {
//                    System.out.println("No matching seat found for: " + seatID);
//                }
//            }
//
//            // Nếu giỏ hàng vẫn còn vé, quay lại passengerInfo.jsp
//            if (!cartItems.isEmpty()) {
////                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
//            } else {
//                response.sendRedirect(redirectURL);
//            }
//            return;
//        }
        if ("confirmDOB".equals(action)) {
            String indexStr = request.getParameter("index");
            String dob = request.getParameter("dob");
            int idx = Integer.parseInt(indexStr);

            // Lấy session
            List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
            boolean[] confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");

            // Tùy logic: 
            //   - Nếu CHỈ Trẻ em => idNumberList.set(idx, dob), confirmedDOB[idx] = true
            //   - Nếu Người cao tuổi => CHỈ store cờ confirmedDOB, KHÔNG ghi đè idNumber
            idNumberList.set(idx, dob);        // or check if isChild => set
            confirmedDOB[idx] = true;

            session.setAttribute("idNumberList", idNumberList);
            session.setAttribute("confirmedDOB", confirmedDOB);

            response.setContentType("text/plain");
            response.getWriter().write("OK");
            return;
        }

        // 3) Người dùng bấm "Tiếp tục"
        int passengerCount = 0;
        try {
            passengerCount = Integer.parseInt(request.getParameter("passengerCount"));
        } catch (NumberFormatException e) {
            // param sai => fallback
            passengerCount = cartItems.size();
        }

        double totalAmount = 0.0;
        List<String> fullNameList = new ArrayList<>();
        List<String> idNumberList = new ArrayList<>();
        List<String> typeList = new ArrayList<>();
        List<Double> finalPriceList = new ArrayList<>();
        Set<String> cccdSet = new HashSet<>();
        Set<String> cccdSetGo = new HashSet<>();
        Set<String> cccdSetReturn = new HashSet<>();

        for (int i = 0; i < passengerCount; i++) {
            String fullName = request.getParameter("fullName" + i);
            String passengerType = request.getParameter("passengerType" + i);
            String idNumber = request.getParameter("idNumber" + i);
            String tripIDStr = request.getParameter("tripID" + i);  // lấy tripID cho item i
// for (int i=0; i<passengerCount; i++){
            String passengerCCCD = request.getParameter("passengerCCCD" + i);
// Lưu vào sessionScope.idNumberList[i] = passengerCCCD
// ...

            int tripID = 0;
            try {
                tripID = Integer.parseInt(tripIDStr);
            } catch (NumberFormatException e) {
                // Xử lý lỗi: tripID không hợp lệ
                request.setAttribute("errorMessage", "Thiếu hoặc sai tripID ở vé thứ " + (i + 1));
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            }
            // Lấy giá vé gốc
            double basePrice = 0.0;
            try {
                basePrice = Double.parseDouble(request.getParameter("price" + i));
            } catch (NumberFormatException e) {
                basePrice = 0.0;
            }

            // Chỉ kiểm tra trùng nếu là vé một chiều
            // Chuẩn hóa tên và CCCD trước khi kiểm tra
            String normalizedFullName = normalizeString(fullName);
            String normalizedCCCD = normalizeString(idNumber);
            boolean needCCCDRegex = true;
            if ("Trẻ em".equals(passengerType)) {
                // Tùy ý => skip regex check
                needCCCDRegex = false;
            }

            TicketDAO ticketDAO = new TicketDAO();
            if (ticketDAO.ticketExistsByCCCDAndPaid(idNumber, tripID)) {
                request.setAttribute("errorMessage",
                        "CCCD '" + idNumber + "' đã có vé (đã thanh toán) trên chuyến này!");
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            }

// Kiểm tra nếu tên hoặc CCCD chỉ chứa dấu cách hoặc rỗng
            if (normalizedFullName.isEmpty()) {
                request.setAttribute("errorMessage", "Họ tên không hợp lệ, vui lòng nhập lại.");
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            }
            if (normalizedCCCD.isEmpty() && needCCCDRegex) {
                request.setAttribute("errorMessage", "CMND/Hộ chiếu không hợp lệ, vui lòng nhập lại.");
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                return;
            }
            // Ví dụ: chấp nhận 9 hoặc 12 chữ số

            if (needCCCDRegex) {
                String cccdRegex = "^(\\d{9}|\\d{12})$";
                if (!normalizedCCCD.matches(cccdRegex)) {
                    request.setAttribute("errorMessage", "CCCD/Hộ chiếu phải là 9 hoặc 12 chữ số!");
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
            }

            // Xác định vé khứ hồi
            boolean isRoundTrip = "2".equals(tripType);
            boolean isReturnTrip = false;

// Kiểm tra trong giỏ hàng có vé về không
            for (CartItem item : cartItems) {
                if (item.isReturnTrip()
                        && item.getDepartureStationName().equals(arrivalStationID)
                        && item.getArrivalStationName().equals(departureStationID)
                        && item.getDepartureDate().equals(returnDate)) {
                    isReturnTrip = true;
                    break;
                }

            }

// Nếu là vé khứ hồi, không kiểm tra trùng tên
            boolean thisIsReturnTrip = cartItems.get(i).isReturnTrip();
            // Hoặc so sánh param "tripID" hay departureDate, 
            // miễn xác định item i là đi hay về

            if (thisIsReturnTrip) {
                // Kiểm tra cccdSetReturn
                if (cccdSetReturn.contains(idNumber)) {
                    request.setAttribute("errorMessage",
                            "Không được trùng CCCD/Hộ chiếu trong cùng chuyến về: " + idNumber);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
                cccdSetReturn.add(idNumber);
            } else {
                // Kiểm tra cccdSetGo
                if (cccdSetGo.contains(idNumber)) {
                    request.setAttribute("errorMessage",
                            "Không được trùng CCCD/Hộ chiếu trong cùng chuyến đi: " + idNumber);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
                cccdSetGo.add(idNumber);
            }
            // Tính discountRate
            double discountRate = 0.0;
            // Check type
            if ("Trẻ em".equals(passengerType)) {
                // Lấy birthDay + check age
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);
                int age = calculateAge(dayStr, monthStr, yearStr);

                if (age < 6) {
                    request.setAttribute("errorMessage",
                            "Trẻ em dưới 6 tuổi không cần vé: " + fullName);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                } else if (age <= 10) {
                    discountRate = 50.0;
                } else {
                    request.setAttribute("errorMessage",
                            "Hành khách '" + fullName + "' không đúng độ tuổi Trẻ em (6-10).");
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
            } else if ("Người cao tuổi".equals(passengerType)) {
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);
                int age = calculateAge(dayStr, monthStr, yearStr);

                if (age < 60) {
                    request.setAttribute("errorMessage",
                            "Hành khách '" + fullName + "' chưa đủ 60 tuổi để giảm giá người cao tuổi.");
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                } else {
                    discountRate = 30.0;
                }
            } else if ("Sinh viên".equals(passengerType)) {
                discountRate = 20.0;
            } else if ("VIP".equals(passengerType)) {
                String vipCard = request.getParameter("vipCard" + i);
                if (vipCard == null || vipCard.trim().isEmpty()) {
                    request.setAttribute("errorMessage",
                            "Vui lòng nhập thẻ VIP cho hành khách: " + fullName);
                    request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
                    return;
                }
                discountRate = 10.0;
            }
            // Người lớn => discountRate=0

            // Tính finalPrice
            double discountAmount = basePrice * discountRate / 100.0;
            double finalPrice = basePrice - discountAmount + 1000;
            totalAmount += finalPrice;
            finalPriceList.add(finalPrice);

            // Lưu
            fullNameList.add(fullName);
            idNumberList.add(idNumber);
            typeList.add(passengerType);
        }
        session.setAttribute("finalPriceList", finalPriceList);
        // Lấy thông tin người đặt vé
        String bookingName = request.getParameter("bookingName");
        String bookingEmail = request.getParameter("bookingEmail");
        String bookingPhone = request.getParameter("bookingPhone");
        String bookingCCCD = request.getParameter("bookingCCCD");

        String normalizedBookingName = normalizeString(bookingName);
        String normalizedBookingCCCD = normalizeString(bookingCCCD);
// Kiểm tra rỗng hoặc chỉ chứa khoảng trắng
        if (normalizedBookingName.isEmpty()) {
            request.setAttribute("errorMessage", "Họ và tên người đặt vé không được để trống hoặc chỉ chứa khoảng trắng.");
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            return;
        }

        if (normalizedBookingCCCD.isEmpty()) {
            request.setAttribute("errorMessage", "CMND/Hộ chiếu của người đặt vé không được để trống hoặc chỉ chứa khoảng trắng.");
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            return;
        }

        if (bookingEmail.isEmpty() || !bookingEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("errorMessage", "Email không hợp lệ. Vui lòng nhập đúng định dạng.");
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            return;
        }

        if (bookingPhone.isEmpty() || !bookingPhone.matches("^\\d{10,11}$")) {
            request.setAttribute("errorMessage", "Số điện thoại không hợp lệ. Vui lòng nhập 10-11 chữ số.");
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            return;
        }
//        String cccd = request.getParameter("idNumber"); // ví dụ
//        String tripIDParam = request.getParameter("tripID");
//        if (tripIDParam == null || tripIDParam.trim().isEmpty()) {
//            request.setAttribute("errorMessage", "Thiếu thông tin tripID");
//            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
//            return;
//        }
//        int tripID = Integer.parseInt(tripIDParam);
//
//        
//        if (ticketDAO.ticketExistsByCCCDAndPaid(cccd, tripID)) {
//            // Nếu trả về true, nghĩa là đã có vé trùng CCCD & trip
//            request.setAttribute("errorMessage", "Vé với CCCD này đã tồn tại trên chuyến đi này.");
//            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
//            return;
//        }

        // Lưu session
        session.setAttribute("bookingName", bookingName);
        session.setAttribute("bookingEmail", bookingEmail);
        session.setAttribute("bookingPhone", bookingPhone);
        session.setAttribute("bookingCCCD", bookingCCCD);
        System.out.println("DEBUG: bookingCCCD from param = " + bookingCCCD);

        // Lưu kết quả
        session.setAttribute("fullNameList", fullNameList);
        session.setAttribute("idNumberList", idNumberList);
        session.setAttribute("typeList", typeList);
        session.setAttribute("totalAmount", totalAmount);

        request.setAttribute("cartItems", session.getAttribute("cartItems"));

        // Forward sang confirm.jsp
        request.getRequestDispatcher("confirm.jsp").forward(request, response);

        // Debug log
        for (int i = 0; i < passengerCount; i++) {
            System.out.println("fullName" + i + " = " + fullNameList.get(i));
            System.out.println("idNumber" + i + " = " + idNumberList.get(i));
        }
    }

    // Tính tuổi ở server
    private int calculateAge(String dayStr, String monthStr, String yearStr) {
        try {
            int d = Integer.parseInt(dayStr);
            int m = Integer.parseInt(monthStr);
            int y = Integer.parseInt(yearStr);
            LocalDate birthDate = LocalDate.of(y, m, d);
            LocalDate now = LocalDate.now();
            int age = now.getYear() - birthDate.getYear();
            if (now.getMonthValue() < m || (now.getMonthValue() == m && now.getDayOfMonth() < d)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            return -1; // Lỗi parse => -1
        }
    }

    // Hàm chuẩn hóa chuỗi (loại bỏ dấu và khoảng trắng thừa)
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }

        // Chuẩn hóa khoảng trắng: Loại bỏ khoảng trắng đầu, cuối và dư thừa giữa các từ
        input = input.trim().replaceAll("\\s+", " ");

        // Nếu sau khi chuẩn hóa mà chuỗi bị rỗng, thì trả về rỗng luôn
        if (input.isEmpty()) {
            return "";
        }

        // Loại bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("").toLowerCase();
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
