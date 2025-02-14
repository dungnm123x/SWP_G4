package controller;

import dal.DAOAdmin;
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
            if ("addEmployee".equals(view)) {
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
            } else if ("trains".equals(view)) {
                List<Train> trains = (search == null || search.isEmpty())
                        ? dao.getAllTrains()
                        : dao.searchTrains(search);
                request.setAttribute("list", trains);
                request.setAttribute("type", "trains");
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

        if ("delete".equals(action)) {
            String type = request.getParameter("type");
            int id = Integer.parseInt(request.getParameter("id"));

            if ("employees".equals(type)) {
                dao.deleteEmployee(id);
            } else if ("customers".equals(type)) {
                dao.deleteCustomer(id);
            } else if ("trains".equals(type)) {
                dao.deleteTrain(id);
            }
            response.sendRedirect("admin?view=" + type);
        } else if ("addEmployee".equals(action)) {
            try {
                String username = request.getParameter("username");
                String password = request.getParameter("password"); // Cần hash mật khẩu
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");

                User newUser = new User(Integer.parseInt(request.getParameter("userID")), username, password, fullName, email, phone, 3);
                boolean success = dao.insertEmployee(newUser);

                if (success) {
                    request.getSession().setAttribute("message", "✅ Thêm nhân viên thành công!");
                    response.sendRedirect("admin?view=employees");
                } else {
                    request.setAttribute("message", "❌ Thêm nhân viên thất bại! Vui lòng thử lại.");
                    request.getRequestDispatcher("view/adm/addEmployees.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message", "⚠️ Lỗi hệ thống: " + e.getMessage());
                request.getRequestDispatcher("view/adm/addEmployees.jsp").forward(request, response);
            }
        }

    }
}
