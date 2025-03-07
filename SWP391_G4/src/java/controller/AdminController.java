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
import model.User;
import model.Train;

public class AdminController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOAdmin dao = new DAOAdmin();
        String view = request.getParameter("view");
        String search = request.getParameter("search");

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

                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("totalEmployees", totalEmployees);
                request.setAttribute("totalCustomers", totalCustomers);
                request.setAttribute("totalTrains", totalTrains);
                request.setAttribute("totalBookings", totalBookings);
                request.setAttribute("totalTrips", totalTrips);
                request.setAttribute("totalBlogs", totalBlogs);
                request.setAttribute("totalRules", totalRules);

                request.setAttribute("type", "dashboard");
                request.getRequestDispatcher("view/adm/admin.jsp").forward(request, response);
                return;
            } else if ("addEmployee".equals(view)) {
                request.getRequestDispatcher("/view/adm/addEmployees.jsp").forward(request, response);
                return;
            }

            if ("employees".equals(view)) {
                List<User> employees = (search == null || search.isEmpty())
                        ? dao.getAllEmployees()
                        : dao.searchEmployees(search);
                request.setAttribute("list", employees);
                request.setAttribute("type", "employees");
            } else if ("customers".equals(view)) {
                List<User> customers = (search == null || search.isEmpty())
                        ? dao.getAllCustomers()
                        : dao.searchCustomers(search);
                request.setAttribute("list", customers);
                request.setAttribute("type", "customers");
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
        }

    }
}
