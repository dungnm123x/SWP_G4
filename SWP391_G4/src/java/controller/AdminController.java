package controller;

import dal.DAOAdmin;
import dal.DashBoardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.Feedback;
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
                int totalUsers = dashBoardDAO.getTotalUsers();
                int totalEmployees = dashBoardDAO.getTotalEmployees();
                int totalCustomers = dashBoardDAO.getTotalCustomers();
                int totalTrains = dashBoardDAO.getTotalTrains();
                int totalBookings = dashBoardDAO.getTotalBookings();
                int totalTrips = dashBoardDAO.getTotalTrips();
                int totalBlogs = dashBoardDAO.getTotalBlogs();
                int totalRules = dashBoardDAO.getTotalRules();
                int totalStations = dashBoardDAO.getTotalStations();
                double revenueToday = dashBoardDAO.getRevenueToday();
                double revenueThisWeek = dashBoardDAO.getRevenueThisWeek();
                double revenueThisMonth = dashBoardDAO.getRevenueThisMonth();
                double revenueThisYear = dashBoardDAO.getRevenueThisYear();

                request.setAttribute("revenueToday", revenueToday);
                request.setAttribute("revenueThisWeek", revenueThisWeek);
                request.setAttribute("revenueThisMonth", revenueThisMonth);
                request.setAttribute("revenueThisYear", revenueThisYear);
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("totalEmployees", totalEmployees);
                request.setAttribute("totalCustomers", totalCustomers);
                request.setAttribute("totalTrains", totalTrains);
                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("totalTrips", totalTrips);
                request.setAttribute("totalBlogs", totalBlogs);
                request.setAttribute("totalRules", totalRules);
                request.setAttribute("totalStations", totalStations);
                List<Feedback> feedbackList = dashBoardDAO.getLatestFeedbacks();
                request.setAttribute("feedbackList", feedbackList);

                request.setAttribute("type", "dashboard");
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
                if (user != null && user.getUserId() == 1) {
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
                    request.getSession().setAttribute("message2", "✅ Thay đổi trạng thái thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ Thay đổi trạng thái thất bại!");
                }
                response.sendRedirect("admin?view=" + type);
                return;
            }

            request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOAdmin dao = new DAOAdmin();
        String action = request.getParameter("action");

        if ("addEmployee".equals(action)) {
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
        } else if ("save".equals(action)) { // Lưu thông tin đã chỉnh sửa
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String type = request.getParameter("type");
                String username = request.getParameter("username");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");

                User updatedUser = new User(id, username, null, fullName, email, phone, address, (type.equals("employees") ? 3 : 2), true); // Tạo đối tượng User mới

                boolean success = dao.updateUser(updatedUser); // Gọi hàm update trong DAO

                if (success) {
                    request.getSession().setAttribute("message2", "✅ Cập nhật thành công!");
                } else {
                    request.setAttribute("message2", "❌ Cập nhật thất bại! Vui lòng thử lại.");
                }
                response.sendRedirect("admin?view=" + type); // Quay lại trang danh sách
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message2", "⚠️ Lỗi hệ thống: " + e.getMessage());
                response.sendRedirect("admin?view=" + request.getParameter("type"));
            }
        } else if ("authorizeUser".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int authorizedBy = ((User) request.getSession().getAttribute("user")).getUserId();

                if (dao.authorizeAdmin(userId, authorizedBy)) {
                    request.getSession().setAttribute("message2", "✅ Phân quyền Admin thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ Phân quyền Admin thất bại.");
                }
                response.sendRedirect("admin?view=userauthorization");
                return;
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } // Action: Thu hồi quyền người dùng
        else if ("revokeUser".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));

                if (dao.revokeAdminAuthorization(userId)) {
                    request.getSession().setAttribute("message2", "✅ Thu hồi quyền thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ Thu hồi quyền thất bại.");
                }
                response.sendRedirect("admin?view=userauthorization");
                return;
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } // Action: Đặt Role cho User
        else if ("setUserRole".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int roleId = Integer.parseInt(request.getParameter("roleId"));

                if (dao.updateUserRole(userId, roleId)) {
                    request.getSession().setAttribute("message2", "✅ Đặt Role thành công!");
                } else {
                    request.getSession().setAttribute("message2", "❌ Đặt Role thất bại.");
                }
                response.sendRedirect("admin?view=userauthorization");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("message2", "⚠️ Lỗi hệ thống: " + e.getMessage());
                response.sendRedirect("admin?view=userauthorization");
                return;
            }
        }

    }
}
