package controller;

import dal.SeatDAO;
import dal.TicketDAO;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CartItem;
import model.User;

/**
 * Servlet xử lý trang "passengerInfo.jsp": - Thêm, xóa vé (removeOne /
 * clearAll) - Nhập thông tin hành khách - Kiểm tra logic tuổi, CCCD trùng -
 * Điều hướng sang confirm.jsp
 */
public class PassengerInfoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 1) Kiểm tra đăng nhập
        if (user == null) {
            String qs = request.getQueryString();
            String reqURL = "passengerinfo" + (qs != null && !qs.isEmpty() ? "?" + qs : "");
            session.setAttribute("redirectAfterLogin", reqURL);
            response.sendRedirect("login.jsp");
            return;
        }

        // 2) Lấy giỏ hàng; nếu null, khởi tạo
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        // 3) Đồng bộ các list: fullNameList, typeList, idNumberList, confirmedDOB
        session.setAttribute("fullNameList",
                syncList((List<String>) session.getAttribute("fullNameList"), cartItems.size(), ""));
        session.setAttribute("typeList",
                syncList((List<String>) session.getAttribute("typeList"), cartItems.size(), "Người lớn"));
        session.setAttribute("idNumberList",
                syncList((List<String>) session.getAttribute("idNumberList"), cartItems.size(), ""));
        session.setAttribute("confirmedDOB",
                syncBooleanArray((boolean[]) session.getAttribute("confirmedDOB"), cartItems.size()));

        // 3.1) Đồng bộ cho birthDayList, birthMonthList, birthYearList
        session.setAttribute("birthDayList",
                syncList((List<String>) session.getAttribute("birthDayList"), cartItems.size(), ""));
        session.setAttribute("birthMonthList",
                syncList((List<String>) session.getAttribute("birthMonthList"), cartItems.size(), ""));
        session.setAttribute("birthYearList",
                syncList((List<String>) session.getAttribute("birthYearList"), cartItems.size(), ""));

        // 4) Nếu loại vé là "Trẻ em", tự động đánh dấu confirmedDOB = true
        List<String> typeList = (List<String>) session.getAttribute("typeList");
        boolean[] confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
        for (int i = 0; i < cartItems.size(); i++) {
            if ("Trẻ em".equals(typeList.get(i))) {
                confirmedDOB[i] = true;
            }
        }
        session.setAttribute("confirmedDOB", confirmedDOB);

        // 5) Lưu thông tin người đặt vé mặc định (nếu user đã đăng nhập)
        session.setAttribute("bookingName", user.getFullName());
        session.setAttribute("bookingEmail", user.getEmail());
        session.setAttribute("bookingPhone", user.getPhoneNumber());

        // 6) Tạo passengerinfoURL => nếu cần redirect khi có lỗi
        String passengerinfoURL = request.getRequestURL().toString();
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            passengerinfoURL += "?" + request.getQueryString();
        }
        session.setAttribute("passengerinfoURL", passengerinfoURL);

        // 7) Tạo previousURL => nút Quay lại
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

        // 8) Forward => passengerInfo.jsp
        request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
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

        // Lấy previousURL
        String previousURL = (String) session.getAttribute("previousURL");

        // Lấy cartItems
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }

        //=====================================================
        // 1) CLEAR ALL
        //=====================================================
        if ("clearAll".equals(action)) {
            if (!cartItems.isEmpty()) {
                // Trả lại seat => Available
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

        //=====================================================
        // 2) REMOVE ONE
        //=====================================================
        if ("removeOne".equals(action)) {
            // (1) Nếu AJAX => cập nhật dữ liệu form => session
            if (request.getParameter("fullName0") != null) {
                int passengerCount = cartItems.size();
                List<String> fullNameList = (List<String>) session.getAttribute("fullNameList");
                List<String> typeList = (List<String>) session.getAttribute("typeList");
                List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
                List<String> birthDayList = (List<String>) session.getAttribute("birthDayList");
                List<String> birthMonthList = (List<String>) session.getAttribute("birthMonthList");
                List<String> birthYearList = (List<String>) session.getAttribute("birthYearList");

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
                    String dayParam = request.getParameter("birthDay" + i);
                    if (dayParam != null) {
                        birthDayList.set(i, dayParam);
                    }
                    String monthParam = request.getParameter("birthMonth" + i);
                    if (monthParam != null) {
                        birthMonthList.set(i, monthParam);
                    }
                    String yearParam = request.getParameter("birthYear" + i);
                    if (yearParam != null) {
                        birthYearList.set(i, yearParam);
                    }
                }

                session.setAttribute("fullNameList", fullNameList);
                session.setAttribute("typeList", typeList);
                session.setAttribute("idNumberList", idNumberList);
                session.setAttribute("birthDayList", birthDayList);
                session.setAttribute("birthMonthList", birthMonthList);
                session.setAttribute("birthYearList", birthYearList);
            }

            // (2) Tìm vé cần xóa theo seatID
            String seatIDParam = request.getParameter("seatID");
            int removeIndex = -1;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem item = cartItems.get(i);

                String currentSeatID = item.getTrip().getTripID() + "_"
                        + item.getTrainName() + "_"
                        + item.getDepartureDate() + "_"
                        + item.getCarriageNumber() + "_"
                        + item.getSeatNumber();

                if (currentSeatID.equals(seatIDParam)) {
                    removeIndex = i;
                    break;
                }
            }

            if (removeIndex != -1) {
                // Lấy seatID thực để trả ghế
                CartItem removedItem = cartItems.get(removeIndex);
                String actualSeatID = removedItem.getSeatID();  // ID trong DB

                // (3) Xóa vé khỏi cart
                cartItems.remove(removeIndex);

                // (4) Đồng bộ lại list
                List<String> fullNameList = (List<String>) session.getAttribute("fullNameList");
                List<String> typeList = (List<String>) session.getAttribute("typeList");
                List<String> idNumberList = (List<String>) session.getAttribute("idNumberList");
                List<String> birthDayList = (List<String>) session.getAttribute("birthDayList");
                List<String> birthMonthList = (List<String>) session.getAttribute("birthMonthList");
                List<String> birthYearList = (List<String>) session.getAttribute("birthYearList");

                if (removeIndex < fullNameList.size()) {
                    fullNameList.remove(removeIndex);
                }
                if (removeIndex < typeList.size()) {
                    typeList.remove(removeIndex);
                }
                if (removeIndex < idNumberList.size()) {
                    idNumberList.remove(removeIndex);
                }
                if (removeIndex < birthDayList.size()) {
                    birthDayList.remove(removeIndex);
                }
                if (removeIndex < birthMonthList.size()) {
                    birthMonthList.remove(removeIndex);
                }
                if (removeIndex < birthYearList.size()) {
                    birthYearList.remove(removeIndex);
                }

                session.setAttribute("fullNameList", fullNameList);
                session.setAttribute("typeList", typeList);
                session.setAttribute("idNumberList", idNumberList);
                session.setAttribute("birthDayList", birthDayList);
                session.setAttribute("birthMonthList", birthMonthList);
                session.setAttribute("birthYearList", birthYearList);

                // (5) confirmedDOB
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

                // (6) Trẻ em => confirmedDOB
                typeList = (List<String>) session.getAttribute("typeList");
                confirmedDOB = (boolean[]) session.getAttribute("confirmedDOB");
                for (int i = 0; i < cartItems.size(); i++) {
                    if ("Trẻ em".equals(typeList.get(i))) {
                        confirmedDOB[i] = true;
                    }
                }
                session.setAttribute("confirmedDOB", confirmedDOB);

                // (7) Trả seat => Available
                SeatDAO seatDAO = new SeatDAO();
                seatDAO.updateSeatStatus(actualSeatID, "Available");

                // (8) Nếu bạn có TimerTask => hủy
                ConcurrentHashMap<String, TimerTask> seatTasks = CartServlet.getSeatBookingTasks();
                TimerTask task = seatTasks.get(actualSeatID);
                if (task != null) {
                    task.cancel();
                    seatTasks.remove(actualSeatID);
                }
            }

            // (9) Nếu cart trống => quay về
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

            // (10) Forward passengerInfo.jsp
            if ("true".equals(renderPartial)) {
                request.setAttribute("partialMode", true);
            }
            request.getRequestDispatcher("passengerInfo.jsp").forward(request, response);
            return;
        }

        // 2.5) AJAX confirmDOB => user bấm xác nhận popup => server
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

        //=====================================================
        // 3) Bấm "Tiếp tục" => confirm.jsp
        //=====================================================
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
        List<String> birthDayListFinal = new ArrayList<>();
        List<String> birthMonthListFinal = new ArrayList<>();
        List<String> birthYearListFinal = new ArrayList<>();

        // Kiểm tra CCCD trùng
        TicketDAO ticketDAO = new TicketDAO();
        Set<String> cccdSetGo = new HashSet<>();
        Set<String> cccdSetReturn = new HashSet<>();

        // =========== Vòng lặp đọc input cho từng vé ===========
        for (int i = 0; i < passengerCount; i++) {
            String fullName = request.getParameter("fullName" + i);
            String passengerType = request.getParameter("passengerType" + i);
            String idNumber = request.getParameter("idNumber" + i);
            String tripIDStr = request.getParameter("tripID" + i);

            // day, month, year
            String dayStr = request.getParameter("birthDay" + i);
            String monthStr = request.getParameter("birthMonth" + i);
            String yearStr = request.getParameter("birthYear" + i);

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

            // Chuẩn hóa
            String normalizedFullName = normalizeString(fullName);
            String normalizedCCCD = normalizeString(idNumber);
            boolean needCCCDRegex = !"Trẻ em".equals(passengerType);

            // Kiểm tra trùng CCCD => DB
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

            // Xác định item => xem có phải chuyến về
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

            // Lấy departureDate => parse
            // (Giả sử CartItem có getDepartureDate() = "2025-03-28")
            String departureDateStr = currentItem.getDepartureDate(); // "2025-03-28 08:00:00"
            String dateOnly = departureDateStr.split(" ")[0];         // "2025-03-28"
            LocalDate departureDate = LocalDate.parse(dateOnly);      // OK

            // Tính discount
            double discountRate = 0.0;
            if ("Trẻ em".equals(passengerType)) {
                int age = calculateAge(dayStr, monthStr, yearStr, departureDate);
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
                int age = calculateAge(dayStr, monthStr, yearStr, departureDate);
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
            double finalPrice = basePrice - discountAmount + 1000; // +1000 phí bảo hiểm
            totalAmount += finalPrice;
            finalPriceList.add(finalPrice);

            // Lưu dữ liệu
            fullNameListFinal.add(fullName);
            idNumberListFinal.add(idNumber);
            typeListFinal.add(passengerType);
            birthDayListFinal.add(dayStr);
            birthMonthListFinal.add(monthStr);
            birthYearListFinal.add(yearStr);
            currentItem.setPrice(finalPrice);
        }

        // Lưu finalPriceList
        session.setAttribute("finalPriceList", finalPriceList);

        // 4) Kiểm tra thông tin người đặt vé
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

        // 5) Lưu final => session => confirm
        session.setAttribute("fullNameList", fullNameListFinal);
        session.setAttribute("idNumberList", idNumberListFinal);
        session.setAttribute("typeList", typeListFinal);
        session.setAttribute("birthDayList", birthDayListFinal);
        session.setAttribute("birthMonthList", birthMonthListFinal);
        session.setAttribute("birthYearList", birthYearListFinal);
        session.setAttribute("totalAmount", totalAmount);

        // 6) Forward => confirm.jsp
        request.setAttribute("cartItems", cartItems2);
        request.setAttribute("tripID", cartItems2.get(0).getTrip().getTripID());
        request.setAttribute("departureStationID", departureStationID);
        request.setAttribute("arrivalStationID", arrivalStationID);
        request.setAttribute("departureDay", departureDay);
        request.setAttribute("tripType", tripType);
        request.setAttribute("returnDate", returnDate);

        request.getRequestDispatcher("confirm.jsp").forward(request, response);
    }

    //======================== Utility methods ========================//
    /**
     * Đồng bộ list string theo kích thước newSize
     */
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

    /**
     * Đồng bộ mảng boolean
     */
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

    /**
     * Gửi lỗi => dừng xử lý
     */
    private void sendError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        String passengerinfoURL = (String) session.getAttribute("passengerinfoURL");
        String renderPartial = request.getParameter("renderPartial");

        if ("true".equals(renderPartial)) {
            // AJAX => trả về text
            response.setContentType("text/plain");
            response.getWriter().write("ERROR|" + passengerinfoURL + "|" + errorMessage);
        } else {
            // Form submit => lưu lỗi => redirect
            session.setAttribute("errorMessage", errorMessage);
            response.sendRedirect(passengerinfoURL);
        }
    }

    /**
     * Tính tuổi tại thời điểm departureDate.
     */
    private int calculateAge(String dayStr, String monthStr, String yearStr, LocalDate departureDate) {
        try {
            int d = Integer.parseInt(dayStr);
            int m = Integer.parseInt(monthStr);
            int y = Integer.parseInt(yearStr);
            LocalDate birthDate = LocalDate.of(y, m, d);

            // Tính tuổi theo departureDate
            int age = departureDate.getYear() - birthDate.getYear();
            if (departureDate.getMonthValue() < m
                    || (departureDate.getMonthValue() == m && departureDate.getDayOfMonth() < d)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Chuẩn hóa chuỗi: cắt khoảng trắng, bỏ dấu, toLowerCase()
     */
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        input = input.trim().replaceAll("\\s+", " ");
        if (input.isEmpty()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("")
                .toLowerCase();
    }

    @Override
    public String getServletInfo() {
        return "PassengerInfoServlet - handle passenger data and cart updates";
    }
}
