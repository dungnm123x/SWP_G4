package controller;

import dal.DAOAdmin;
import dal.DashBoardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import model.CalendarEvent;
import model.Feedback;
import model.StationWithCoordinates;
import model.User;
import model.Train;

public class AdminController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOAdmin dao = new DAOAdmin();
        String view = request.getParameter("view");
        String search = request.getParameter("search");
        int page = 1; // Default page
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        int pageSize = 10; // Number of items per page
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1) {
            response.sendRedirect("login");
            return;
        }
        if (view == null) {
            view = "dashboard";
        }
        try {
            if ("dashboard".equals(view)) {
                DashBoardDAO dashBoardDAO = new DashBoardDAO();
                // Xử lý dữ liệu doanh thu
                String viewType = request.getParameter("viewType"); // "monthly" hoặc "yearly"
                String selectedMonth = request.getParameter("month"); // Ví dụ: "03/2025"
                String selectedYear = request.getParameter("year"); // Ví dụ: "2025"

                Map<String, double[]> revenueData = null;
                String[] labels = null;
                if ("yearly".equals(viewType)) {
                    int year = selectedYear != null ? Integer.parseInt(selectedYear) : 2025; // Mặc định là 2025
                    revenueData = dashBoardDAO.getRevenueByYear(year);
                    labels = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
                    request.setAttribute("viewType", "yearly");
                    request.setAttribute("selectedYear", year);
                } else { // Mặc định là monthly
                    int year = 2025;
                    int month = 3; // Mặc định là tháng 3/2025
                    if (selectedMonth != null && selectedMonth.matches("\\d{2}/\\d{4}")) {
                        String[] parts = selectedMonth.split("/");
                        month = Integer.parseInt(parts[0]);
                        year = Integer.parseInt(parts[1]);
                    }
                    revenueData = dashBoardDAO.getRevenueByMonth(year, month);
                    labels = new String[31];
                    for (int i = 1; i <= 31; i++) {
                        labels[i - 1] = String.valueOf(i);
                    }
                    request.setAttribute("viewType", "monthly");
                    request.setAttribute("selectedMonth", String.format("%02d/%d", month, year));
                }

// Chuẩn bị dữ liệu cho biểu đồ
                double[] bookingRevenue = new double[labels.length];
                double[] refundAmount = new double[labels.length];
                double[] netRevenue = new double[labels.length];
                for (int i = 0; i < labels.length; i++) {
                    double[] data = revenueData.get(labels[i]);
                    bookingRevenue[i] = data[0]; // A
                    refundAmount[i] = data[1];   // B
                    netRevenue[i] = data[2];     // A - B
                }

                request.setAttribute("labels", labels);
                request.setAttribute("bookingRevenue", bookingRevenue);
                request.setAttribute("refundAmount", refundAmount);
                request.setAttribute("netRevenue", netRevenue);
                int totalUsers = dashBoardDAO.getTotalUsers();
                int totalEmployees = dashBoardDAO.getTotalEmployees();
                int totalCustomers = dashBoardDAO.getTotalCustomers();
                int totalTrains = dashBoardDAO.getTotalTrains();
                int totalBookings = dashBoardDAO.getTotalBookings();
                int totalTrips = dashBoardDAO.getTotalTrips();
                int totalBlogs = dashBoardDAO.getTotalBlogs();
                int totalRules = dashBoardDAO.getTotalRules();
                int totalStations = dashBoardDAO.getTotalStations();
                int totalTickets = dashBoardDAO.getTotalTickets();
                int totalRoutes = dashBoardDAO.getTotalRoutesCount();
                double totalRefundComplete = dashBoardDAO.getTotalRefundComplete();
                double totalRefundWait = dashBoardDAO.getTotalRefundWait();
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("totalEmployees", totalEmployees);
                request.setAttribute("totalCustomers", totalCustomers);
                request.setAttribute("totalTrains", totalTrains);
                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("totalTrips", totalTrips);
                request.setAttribute("totalBlogs", totalBlogs);
                request.setAttribute("totalRules", totalRules);
                request.setAttribute("totalStations", totalStations);
                request.setAttribute("totalTickets", totalTickets);
                request.setAttribute("tripStatistics", totalRoutes);
                request.setAttribute("totalRefundComplete", totalRefundComplete);
                request.setAttribute("totalRefundWait", totalRefundWait);
                List<Feedback> feedbackList = dashBoardDAO.getLatestFeedbacks();
                request.setAttribute("feedbackList", feedbackList);
                int[] starDistribution = dashBoardDAO.getStarDistribution();
                request.setAttribute("starDistribution", starDistribution);
                List<StationWithCoordinates> stationsWithCoords = dashBoardDAO.getStationsWithCoordinates();
                request.setAttribute("stationsWithCoords", stationsWithCoords);

                request.setAttribute("type", "dashboard");
                request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
                return;
            } else if ("calendar".equals(view)) {
                List<CalendarEvent> events = dao.getAllCalendarEvents();
                request.setAttribute("calendarEvents", events);
                request.setAttribute("type", "calendar");
                request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
                return;
            } else if ("employees".equals(view)) {
                List<User> employees;
                int totalEmployees;
                if (search == null || search.isEmpty()) {
                    employees = dao.getAllEmployees(page, pageSize);
                    totalEmployees = dao.countAllEmployees();
                } else {
                    employees = dao.searchEmployees(search, page, pageSize);
                    totalEmployees = dao.countSearchEmployees(search);
                }
                request.setAttribute("list", employees);
                request.setAttribute("type", "employees");
                int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("currentPage", page);
            } else if ("customers".equals(view)) {
                // Similar logic for customers
                List<User> customers;
                int totalCustomers;
                if (search == null || search.isEmpty()) {
                    customers = dao.getAllCustomers(page, pageSize);
                    totalCustomers = dao.countAllCustomers();
                } else {
                    customers = dao.searchCustomers(search, page, pageSize);
                    totalCustomers = dao.countSearchCustomers(search);
                }
                request.setAttribute("list", customers);
                request.setAttribute("type", "customers");
                int totalPages = (int) Math.ceil((double) totalCustomers / pageSize);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("currentPage", page);
            } else if ("userauthorization".equals(view)) {
                // Similar logic for user authorization
                if (user != null && user.getRoleID() == 1) {
                    try {
                        String searchKeyword = request.getParameter("search");
                        List<User> users;
                        int totalUsers;
                        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                            users = dao.searchUsers(searchKeyword, page, pageSize); // Phương thức tìm kiếm mới trong DAO
                            totalUsers = dao.countSearchUsers(searchKeyword);
                        } else {
                            users = dao.getAllUsers(page, pageSize);
                            totalUsers = dao.countAllUsers();
                        }
                        request.setAttribute("list", users);
                        request.setAttribute("type", "userauthorization");
                        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
                        request.setAttribute("totalPages", totalPages);
                        request.setAttribute("currentPage", page);
                        request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
                        return;
                    } catch (SQLException e) {
                        throw new ServletException(e);
                    }
                } else {
                    response.sendRedirect("admin?view=dashboard");
                    return;
                }
            } else if ("addEmployee".equals(view)) {
                request.getRequestDispatcher("/view/adm/addEmployees.jsp").forward(request, response);
                return;
            } else if ("details".equals(view)) { // Xử lý yêu cầu xem chi tiết
                String type = request.getParameter("type");
                int id = Integer.parseInt(request.getParameter("id"));
                User userDetails = null;

                if ("employees".equals(type) || "customers".equals(type)) {
                    userDetails = dao.getUserById(id, type); // Gọi phương thức mới trong DAO
                }

                if (userDetails != null) {
                    request.setAttribute("userDetails", userDetails);
                    request.setAttribute("type", type); // Truyền type để biết đang xem chi tiết employee hay customer
                    request.getRequestDispatcher("view/adm/userDetails.jsp").forward(request, response);
                } else {
                    // Xử lý trường hợp không tìm thấy user (ví dụ: chuyển hướng về trang danh sách)
                    response.sendRedirect("admin?view=" + type);
                    return; // Đảm bảo dừng xử lý sau khi chuyển hướng
                }
                return; // Kết thúc xử lý cho view details
            } else if ("disable".equals(view) || "restore".equals(view)) {
                int userId = Integer.parseInt(request.getParameter("id"));
                String type = request.getParameter("type");
                boolean success = false;

                if ("disable".equals(view)) {
                    if ("employees".equals(type)) {
                        success = dao.disableEmployee(userId);
                    } else if ("customers".equals(type)) {
                        success = dao.disableCustomer(userId);
                    }
                } else if ("restore".equals(view)) {
                    if ("employees".equals(type)) {
                        success = dao.restoreEmployee(userId);
                    } else if ("customers".equals(type)) {
                        success = dao.restoreCustomer(userId);
                    }
                }

                if (success) {
                    request.getSession().setAttribute("messageStatus", "✅ Thay đổi trạng thái thành công!");
                } else {
                    request.getSession().setAttribute("messageStatus", "❌ Thay đổi trạng thái thất bại!");
                }
                response.sendRedirect("admin?view=" + type);
                return;
            }

            request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleCalendarEventError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.getSession().setAttribute("message11", errorMessage);
        response.sendRedirect("admin?view=calendar");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOAdmin dao = new DAOAdmin();
        String action = request.getParameter("action");
        User user = (User) request.getSession().getAttribute("user");

        if ("addCalendarEvent".equals(action) || "updateCalendarEvent".equals(action)) {
            try {
                int eventId = 0;
                if ("updateCalendarEvent".equals(action)) {
                    eventId = Integer.parseInt(request.getParameter("eventId"));
                }
                String title = request.getParameter("title");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");
                boolean allDay = "on".equals(request.getParameter("allDay"));
                String description = request.getParameter("description");

                // Validation 1: Title
                if (title == null || title.trim().isEmpty()) {
                    handleCalendarEventError(request, response, "❌ Tiêu đề không được để trống!");
                    return;
                }
                if (title.length() > 100) {
                    handleCalendarEventError(request, response, "❌ Tiêu đề không được dài quá 100 ký tự!");
                    return;
                }

                // Validation 2: Description
                if (description != null && description.length() > 500) {
                    handleCalendarEventError(request, response, "❌ Mô tả không được dài quá 500 ký tự!");
                    return;
                }

                // Validation 3: Date Format
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                dateFormat.setLenient(false);
                Date startDate = null;
                Date endDate = null;

                // Parse Start Date
                if (startDateStr == null || startDateStr.trim().isEmpty()) {
                    handleCalendarEventError(request, response, "❌ Ngày bắt đầu không được để trống!");
                    return;
                }
                try {
                    startDate = dateFormat.parse(startDateStr);
                } catch (Exception e) {
                    handleCalendarEventError(request, response, "❌ Ngày bắt đầu không hợp lệ (yyyy-MM-ddTHH:mm)!");
                    return;
                }

                // Parse End Date (Optional)
                if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                    try {
                        endDate = dateFormat.parse(endDateStr);
                    } catch (Exception e) {
                        handleCalendarEventError(request, response, "❌ Ngày kết thúc không hợp lệ (yyyy-MM-ddTHH:mm)!");
                        return;
                    }

                    // Validation 4: Start Date vs. End Date
                    if (endDate != null && startDate.after(endDate)) {
                        handleCalendarEventError(request, response, "❌ Ngày bắt đầu phải trước ngày kết thúc!");
                        return;
                    }
                }

                // Validation 5: Start Date in the Past (Optional, but Recommended)
                Date now = new Date();
                if (startDate.before(now) && !allDay) {
                    handleCalendarEventError(request, response, "❌ Ngày bắt đầu không được là thời điểm trong quá khứ!");
                    return;
                }

                // If all validations pass, create or update the event
                CalendarEvent event = new CalendarEvent();
                event.setUserID(user.getUserId());
                event.setTitle(title);
                event.setStartDate(startDate);
                event.setEndDate(endDate);
                event.setAllDay(allDay);
                event.setDescription(description);

                boolean success;
                if ("addCalendarEvent".equals(action)) {
                    success = dao.addCalendarEvent(event);
                } else {
                    event.setEventID(eventId);
                    success = dao.updateCalendarEvent(event);
                }

                if (success) {
                    request.getSession().setAttribute("message2", "✅ " + ("addCalendarEvent".equals(action) ? "Thêm" : "Cập nhật") + " sự kiện thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ " + ("addCalendarEvent".equals(action) ? "Thêm" : "Cập nhật") + " sự kiện thất bại!");
                }
                response.sendRedirect("admin?view=calendar");

            } catch (Exception e) {
                e.printStackTrace();
                handleCalendarEventError(request, response, "⚠️ Lỗi hệ thống: " + e.getMessage());
            }
        } else if ("deleteCalendarEvent".equals(action)) {
            try {
                int eventId = Integer.parseInt(request.getParameter("eventId"));

                boolean success = dao.deleteCalendarEvent(eventId);

                if (success) {
                    request.getSession().setAttribute("message2", "✅ Xóa sự kiện thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ Xóa sự kiện thất bại!");
                }
                response.sendRedirect("admin?view=calendar");

            } catch (Exception e) {
                e.printStackTrace();
                handleCalendarEventError(request, response, "⚠️ Lỗi hệ thống: " + e.getMessage());
            }
        } else if ("addEmployee".equals(action)) {
            try {
                String username = request.getParameter("username");
                String password = request.getParameter("password"); // Cần hash mật khẩu
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");

                User newUser = new User(0, username, password, fullName, email, phone, address, 3, true);

                try {
                    boolean success = dao.addEmployee(newUser);
                    if (success) {
                        request.getSession().setAttribute("message2", "✅ Thêm nhân viên thành công!");
                        response.sendRedirect("admin?view=employees");
                        return; // Important: Stop further processing after successful redirect
                    }
                } catch (IllegalArgumentException ex) {
                    request.setAttribute("errorMessage", ex.getMessage()); // Set error message
                    request.getRequestDispatcher("view/adm/addEmployees.jsp").forward(request, response);
                    return; // Important: Stop further processing after forwarding with error
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "⚠️ Lỗi hệ thống: " + e.getMessage()); // Catch and set general errors
                request.getRequestDispatcher("view/adm/addEmployees.jsp").forward(request, response);
                return; // Stop processing
            }
        } else if ("save".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String type = request.getParameter("type");
                String username = request.getParameter("username");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");

                if (fullName == null || fullName.trim().isEmpty()) {
                    request.setAttribute("messageSave", "❌ Họ và tên không được để trống.");
                    request.getRequestDispatcher("userDetails.jsp").forward(request, response);
                    return;
                }

                if (address == null || address.trim().isEmpty()) {
                    request.setAttribute("messageSave", "❌ Địa chỉ không được để trống.");
                    request.getRequestDispatcher("userDetails.jsp").forward(request, response);
                    return;
                }

                User updatedUser = new User(id, username, null, fullName, email, phone, address, (type.equals("employees") ? 3 : 2), true);

                boolean success = dao.updateUser(updatedUser);

                if (success) {
                    request.getSession().setAttribute("messageSave", "✅ Cập nhật thành công!");
                } else {
                    request.setAttribute("messageSave", "❌ Cập nhật thất bại! Vui lòng thử lại.");
                }
                response.sendRedirect("admin?view=" + type);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("messageSave", "⚠️ Lỗi hệ thống: " + e.getMessage());
                response.sendRedirect("admin?view=" + request.getParameter("type"));
            }
        } // Action: Đặt Role cho User
        else if ("setUserRole".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int roleId = Integer.parseInt(request.getParameter("roleId"));

                // Kiểm tra xem người dùng có status = 0 không
                boolean isActiveUser = dao.isUserActive(userId); // Thêm hàm này vào DAO
                if (!isActiveUser && roleId == 1) { // Nếu user không active và muốn set role Admin
                    request.getSession().setAttribute("message10", "❌ Không thể đặt Role Admin cho người dùng bị vô hiệu hóa.");
                } else if (dao.updateUserRole(userId, roleId)) {
                    request.getSession().setAttribute("message10", "✅ Đặt Role thành công!");
                } else {
                    request.getSession().setAttribute("message10", "❌ Đặt Role thất bại.");
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("Hệ thống cần ít nhất một Admin")) {
                    request.getSession().setAttribute("message10", "⚠️ Không thể thay đổi vai trò. Hệ thống cần ít nhất một Admin.");
                } else {
                    e.printStackTrace();
                    request.getSession().setAttribute("message10", "⚠️ Lỗi hệ thống: " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("message10", "⚠️ Lỗi hệ thống: " + e.getMessage());
            }
            response.sendRedirect("admin?view=userauthorization");
            return;
        }

    }
}
