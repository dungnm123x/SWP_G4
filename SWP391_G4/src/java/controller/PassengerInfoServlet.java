package controller;

import dal.SeatDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;
import model.CartItem;
import model.User;

public class PassengerInfoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    //======================== doGet ========================//
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // Kiểm tra đăng nhập
        if (user == null) {
            String qs = request.getQueryString();
            String reqURL = "passengerinfo" + (qs != null && !qs.isEmpty() ? "?" + qs : "");
            session.setAttribute("redirectAfterLogin", reqURL);
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy giỏ hàng; nếu null, khởi tạo
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        // Đồng bộ các danh sách dữ liệu với số lượng vé hiện có
        session.setAttribute("fullNameList", syncList((List<String>) session.getAttribute("fullNameList"), cartItems.size(), ""));
        session.setAttribute("typeList", syncList((List<String>) session.getAttribute("typeList"), cartItems.size(), "Người lớn"));
        session.setAttribute("idNumberList", syncList((List<String>) session.getAttribute("idNumberList"), cartItems.size(), ""));
        session.setAttribute("confirmedDOB", syncBooleanArray((boolean[]) session.getAttribute("confirmedDOB"), cartItems.size()));

        // Nếu loại vé là "Trẻ em", tự động đánh dấu confirmedDOB = true
        List<String> typeList = (List<String>) session.getAttribute("typeList");
        boolean[] confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
        for (int i = 0; i < cartItems.size(); i++) {
            if ("Trẻ em".equals(typeList.get(i))) {
                confirmedDOB[i] = true;
            }
        }
        session.setAttribute("confirmedDOB", confirmedDOB);

        // Lưu thông tin người đặt vé
        session.setAttribute("bookingName", user.getFullName());
        session.setAttribute("bookingEmail", user.getEmail());
        session.setAttribute("bookingPhone", user.getPhoneNumber());

        // Lưu previousURL từ query string
        String depID = request.getParameter("departureStationID");
        String arrID = request.getParameter("arrivalStationID");
        String dDay = request.getParameter("departureDay");
        String tType = request.getParameter("tripType");
        String rDate = request.getParameter("returnDate");
        String previousURL = "schedule"
                + "?departureStationID=" + URLEncoder.encode(depID != null ? depID : "", "UTF-8")
                + "&arrivalStationID=" + URLEncoder.encode(arrID != null ? arrID : "", "UTF-8")
                + "&departureDay=" + URLEncoder.encode(dDay != null ? dDay : "", "UTF-8")
                + "&tripType=" + URLEncoder.encode(tType != null ? tType : "", "UTF-8")
                + "&returnDate=" + URLEncoder.encode(rDate != null ? rDate : "", "UTF-8");
        session.setAttribute("previousURL", previousURL);

        request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
    }

    // Hàm đồng bộ danh sách: nếu size hiện tại không bằng newSize, tạo list mới với giá trị mặc định cho phần thiếu
    private List<String> syncList(List<String> list, int newSize, String defaultValue) {
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.size() < newSize) {
            for (int i = list.size(); i < newSize; i++) {
                list.add(defaultValue);
            }
        } else if (list.size() > newSize) {
            list = list.subList(0, newSize);
        }
        return list;
    }

    // Đồng bộ mảng boolean
    private boolean[] syncBooleanArray(boolean[] arr, int newSize) {
        boolean[] newArr = new boolean[newSize];
        if (arr != null) {
            int min = Math.min(arr.length, newSize);
            for (int i = 0; i < min; i++) {
                newArr[i] = arr[i];
            }
        }
        return newArr;
    }
    
    // Phương thức tiện ích để gửi lỗi theo đúng url (AJAX partial hay forward)
    private void sendError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        String previousURL = (String) session.getAttribute("previousURL");
        String renderPartial = request.getParameter("renderPartial");
        if ("true".equals(renderPartial)) {
            response.setContentType("text/plain");
            // Gửi về dạng "ERROR|<previousURL>|<errorMessage>"
            response.getWriter().write("ERROR|" + previousURL + "|" + errorMessage);
        } else {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
        }
    }

    //======================== doPost ========================//
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String renderPartial = request.getParameter("renderPartial");

        // Lấy các tham số chuyến đi
        String departureStationID = request.getParameter("departureStationID");
        String arrivalStationID = request.getParameter("arrivalStationID");
        String departureDay = request.getParameter("departureDay");
        String tripType = request.getParameter("tripType");
        String returnDate = request.getParameter("returnDate");

        // Sử dụng previousURL đã lưu trong session (đã được set ở doGet)
        String previousURL = (String) session.getAttribute("previousURL");

        // Lấy cartItems
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        //==================================================
        // 1) CLEAR ALL
        //==================================================
        if ("clearAll".equals(action)) {
            if (!cartItems.isEmpty()) {
                SeatDAO seatDAO = new SeatDAO();
                for (CartItem item : cartItems) {
                    seatDAO.updateSeatStatus(item.getSeatID(), "Available");
                }
                cartItems.clear();
                session.removeAttribute("cartItems");
            }
            response.sendRedirect(previousURL != null ? previousURL : "schedule");
            return;
        }

        //==================================================
        // 2) REMOVE ONE
        //==================================================
        if ("removeOne".equals(action)) {
            // Cập nhật session lists nếu dữ liệu được gửi lên (AJAX có gửi đầy đủ dữ liệu form)
            if (request.getParameter("fullName0") != null) {
                int passengerCount = cartItems.size();
                List<String> fullNameList = (List<String>) session.getAttribute("fullNameList");
                List<String> typeList = (List<String>) session.getAttribute("typeList");
                List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
                for (int i = 0; i < passengerCount; i++) {
                    String nameParam = request.getParameter("fullName" + i);
                    if (nameParam != null) {
                        fullNameList.set(i, nameParam);
                    }
                    String typeParam = request.getParameter("passengerType" + i);
                    if (typeParam != null) {
                        typeList.set(i, typeParam);
                    }
                    String idParam = request.getParameter("idNumber" + i);
                    if (idParam != null) {
                        idNumberList.set(i, idParam);
                    }
                }
                session.setAttribute("fullNameList", fullNameList);
                session.setAttribute("typeList", typeList);
                session.setAttribute("idNumberList", idNumberList);
            }

            // Xác định vé cần xóa dựa trên seatID
            String seatID = request.getParameter("seatID");
            int removeIndex = -1;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem item = cartItems.get(i);
                String currentSeatID = item.getTrip().getTripID() + "_" + item.getTrainName() + "_"
                        + item.getDepartureDate() + "_" + item.getCarriageNumber() + "_" + item.getSeatNumber();
                if (currentSeatID.equals(seatID)) {
                    removeIndex = i;
                    break;
                }
            }
            // ... (phần cập nhật fullNameList, typeList, idNumberList và mảng confirmedDOB sau khi xóa vé)
            if (removeIndex != -1) {
                cartItems.remove(removeIndex);
                List<String> fullNameList = (List<String>) session.getAttribute("fullNameList");
                List<String> typeList = (List<String>) session.getAttribute("typeList");
                List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
                if (removeIndex < fullNameList.size()) {
                    fullNameList.remove(removeIndex);
                }
                if (removeIndex < typeList.size()) {
                    typeList.remove(removeIndex);
                }
                if (removeIndex < idNumberList.size()) {
                    idNumberList.remove(removeIndex);
                }
                session.setAttribute("fullNameList", fullNameList);
                session.setAttribute("typeList", typeList);
                session.setAttribute("idNumberList", idNumberList);

                // Cập nhật mảng confirmedDOB
                boolean[] confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
                if (confirmedDOB != null && confirmedDOB.length > removeIndex) {
                    boolean[] newConfirmedDOB = new boolean[cartItems.size()];
                    for (int i = 0; i < removeIndex; i++) {
                        newConfirmedDOB[i] = confirmedDOB[i];
                    }
                    for (int i = removeIndex; i < newConfirmedDOB.length; i++) {
                        newConfirmedDOB[i] = confirmedDOB[i + 1];
                    }
                    session.setAttribute("confirmedDOB", newConfirmedDOB);
                }

                // Chèn đoạn cập nhật readonly cho các vé "Trẻ em"
                typeList = (List<String>) session.getAttribute("typeList");
                confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
                for (int i = 0; i < cartItems.size(); i++) {
                    if ("Trẻ em".equals(typeList.get(i))) {
                        confirmedDOB[i] = true;
                    }
                }
                session.setAttribute("confirmedDOB", confirmedDOB);
            }

            // Nếu giỏ hàng trống, chuyển hướng về previousURL
            if (cartItems.isEmpty()) {
                if ("true".equals(renderPartial)) {
                    response.setContentType("text/plain");
                    String finalURL = (previousURL != null && !previousURL.isEmpty())
                            ? previousURL : "schedule";
                    response.getWriter().write("EMPTY|" + finalURL);
                    return;
                } else {
                    response.sendRedirect(previousURL != null ? previousURL : "schedule");
                    return;
                }
            }

            if ("true".equals(renderPartial)) {
                request.setAttribute("partialMode", true);
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            }
            return;
        }

        //==================================================
        // 2.5) AJAX confirmDOB
        //==================================================
        if ("confirmedDOBMap".equals(action)) {
            String indexStr = request.getParameter("index");
            String dob = request.getParameter("dob");
            try {
                int idx = Integer.parseInt(indexStr);
                List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
                boolean[] confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
                if (idNumberList != null && idx < idNumberList.size()) {
                    idNumberList.set(idx, dob);
                }
                if (confirmedDOB != null && idx < confirmedDOB.length) {
                    confirmedDOB[idx] = true;
                }
                session.setAttribute("idNumberList", idNumberList);
                session.setAttribute("confirmedDOB", confirmedDOB);
                response.setContentType("text/plain");
                response.getWriter().write("OK");
            } catch (NumberFormatException e) {
                response.setContentType("text/plain");
                response.getWriter().write("ERROR");
            }
            return;
        }

        //==================================================
        // 3) Xử lý bấm "Tiếp tục" => chuyển sang confirm.jsp
        //==================================================
        List<CartItem> cartItems2 = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems2 == null || cartItems2.isEmpty()) {
            response.sendRedirect(previousURL != null ? previousURL : "schedule");
            return;
        }
        int passengerCount;
        try {
            passengerCount = Integer.parseInt(request.getParameter("passengerCount"));
        } catch (NumberFormatException e) {
            passengerCount = cartItems2.size();
        }
        passengerCount = Math.min(passengerCount, cartItems2.size());

        double totalAmount = 0.0;
        List<String> fullNameListFinal = new ArrayList<>();
        List<String> idNumberListFinal = new ArrayList<>();
        List<String> typeListFinal = new ArrayList<>();
        List<Double> finalPriceList = new ArrayList<>();

        // Dùng TicketDAO cho kiểm tra vé
        TicketDAO ticketDAO = new TicketDAO();
        // Các Set kiểm tra trùng CCCD trong cùng chuyến đi/ về
        Set<String> cccdSetGo = new HashSet<>();
        Set<String> cccdSetReturn = new HashSet<>();

        for (int i = 0; i < passengerCount; i++) {
            String fullName = request.getParameter("fullName" + i);
            String passengerType = request.getParameter("passengerType" + i);
            String idNumber = request.getParameter("idNumber" + i);
            String tripIDStr = request.getParameter("tripID" + i);
            int tripID = 0;
            try {
                tripID = Integer.parseInt(tripIDStr);
            } catch (NumberFormatException e) {
                sendError(request, response, "Thiếu hoặc sai tripID ở vé thứ " + (i + 1));
                return;
            }

            double basePrice = 0.0;
            try {
                basePrice = Double.parseDouble(request.getParameter("price" + i));
            } catch (NumberFormatException e) {
                basePrice = 0.0;
            }

            String normalizedFullName = normalizeString(fullName);
            String normalizedCCCD = normalizeString(idNumber);
            boolean needCCCDRegex = !"Trẻ em".equals(passengerType);

            // Kiểm tra vé trùng theo TicketDAO
            if (ticketDAO.isTicketActiveByCCCD(idNumber, tripID)) {
                sendError(request, response, "CCCD '" + idNumber + "' đã có vé (đã thanh toán) trên chuyến này!");
                return;
            }
            if (normalizedFullName.isEmpty()) {
                sendError(request, response, "Họ tên không hợp lệ, vui lòng nhập lại.");
                return;
            }
            if (normalizedCCCD.isEmpty() && needCCCDRegex) {
                sendError(request, response, "CMND/Hộ chiếu không hợp lệ, vui lòng nhập lại.");
                return;
            }
            if (needCCCDRegex) {
                String cccdRegex = "^(\\d{9}|\\d{12})$";
                if (!normalizedCCCD.matches(cccdRegex)) {
                    sendError(request, response, "CCCD/Hộ chiếu phải là 9 hoặc 12 chữ số!");
                    return;
                }
            }

            CartItem currentItem = cartItems2.get(i);
            boolean thisIsReturnTrip = currentItem.isReturnTrip();
            if (thisIsReturnTrip) {
                if (cccdSetReturn.contains(idNumber)) {
                    sendError(request, response, "Không được trùng CCCD/Hộ chiếu trong cùng chuyến về: " + idNumber);
                    return;
                }
                cccdSetReturn.add(idNumber);
            } else {
                if (cccdSetGo.contains(idNumber)) {
                    sendError(request, response, "Không được trùng CCCD/Hộ chiếu trong cùng chuyến đi: " + idNumber);
                    return;
                }
                cccdSetGo.add(idNumber);
            }

            double discountRate = 0.0;
            if ("Trẻ em".equals(passengerType)) {
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);
                int age = calculateAge(dayStr, monthStr, yearStr);
                if (age < 6) {
                    sendError(request, response, "Trẻ em dưới 6 tuổi không cần vé: " + fullName);
                    return;
                } else if (age <= 10) {
                    discountRate = 50.0;
                } else {
                    sendError(request, response, "Hành khách '" + fullName + "' không đúng độ tuổi Trẻ em (6-10).");
                    return;
                }
            } else if ("Người cao tuổi".equals(passengerType)) {
                String dayStr = request.getParameter("birthDay" + i);
                String monthStr = request.getParameter("birthMonth" + i);
                String yearStr = request.getParameter("birthYear" + i);
                int age = calculateAge(dayStr, monthStr, yearStr);
                if (age < 60) {
                    sendError(request, response, "Hành khách '" + fullName + "' chưa đủ 60 tuổi để giảm giá người cao tuổi.");
                    return;
                } else {
                    discountRate = 30.0;
                }
            } else if ("Sinh viên".equals(passengerType)) {
                discountRate = 20.0;
            } else if ("VIP".equals(passengerType)) {
                String vipCard = request.getParameter("vipCard" + i);
                if (vipCard == null || vipCard.trim().isEmpty()) {
                    sendError(request, response, "Vui lòng nhập thẻ VIP cho hành khách: " + fullName);
                    return;
                }
                discountRate = 10.0;
            }

            double discountAmount = basePrice * discountRate / 100.0;
            double finalPrice = basePrice - discountAmount + 1000;
            totalAmount += finalPrice;
            finalPriceList.add(finalPrice);

            // Lưu dữ liệu tạm cho vé này
            fullNameListFinal.add(fullName);
            idNumberListFinal.add(idNumber);
            typeListFinal.add(passengerType);
        }

        session.setAttribute("finalPriceList", finalPriceList);

        String bookingName = request.getParameter("bookingName");
        String bookingEmail = request.getParameter("bookingEmail");
        String bookingPhone = request.getParameter("bookingPhone");
        String bookingCCCD = request.getParameter("bookingCCCD");

        String normalizedBookingName = normalizeString(bookingName);
        String normalizedBookingCCCD = normalizeString(bookingCCCD);
        if (normalizedBookingName.isEmpty()) {
            sendError(request, response, "Họ và tên người đặt vé không được để trống hoặc chỉ chứa khoảng trắng.");
            return;
        }
        if (normalizedBookingCCCD.isEmpty()) {
            sendError(request, response, "CMND/Hộ chiếu của người đặt vé không được để trống hoặc chỉ chứa khoảng trắng.");
            return;
        }
        if (bookingEmail.isEmpty() || !bookingEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            sendError(request, response, "Email không hợp lệ. Vui lòng nhập đúng định dạng.");
            return;
        }
        if (bookingPhone.isEmpty() || !bookingPhone.matches("^\\d{10,11}$")) {
            sendError(request, response, "Số điện thoại không hợp lệ. Vui lòng nhập 10-11 chữ số.");
            return;
        }

        session.setAttribute("bookingName", bookingName);
        session.setAttribute("bookingEmail", bookingEmail);
        session.setAttribute("bookingPhone", bookingPhone);
        session.setAttribute("bookingCCCD", bookingCCCD);

        session.setAttribute("fullNameList", fullNameListFinal);
        session.setAttribute("idNumberList", idNumberListFinal);
        session.setAttribute("typeList", typeListFinal);
        session.setAttribute("totalAmount", totalAmount);

        request.setAttribute("cartItems", cartItems2);
        request.setAttribute("tripID", cartItems2.get(0).getTrip().getTripID());
        request.setAttribute("departureStationID", departureStationID);
        request.setAttribute("arrivalStationID", arrivalStationID);
        request.setAttribute("departureDay", departureDay);
        request.setAttribute("tripType", tripType);
        request.setAttribute("returnDate", returnDate);

        request.getRequestDispatcher("confirm.jsp").forward(request, response);

        // Debug log
        for (int i = 0; i < Math.min(passengerCount, cartItems2.size()); i++) {
            System.out.println("fullName" + i + " = " + fullNameListFinal.get(i));
            System.out.println("idNumber" + i + " = " + idNumberListFinal.get(i));
        }
    }

    //======================== Utility methods ========================//
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
            return -1;
        }
    }

    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        input = input.trim().replaceAll("\\s+", " ");
        if (input.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("").toLowerCase();
    }

    @Override
    public String getServletInfo() {
        return "PassengerInfoServlet - handle passenger data and cart updates";
    }
}
