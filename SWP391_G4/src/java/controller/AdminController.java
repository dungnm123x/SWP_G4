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

        try {
            if ("employees".equals(view)) {
                List<User> employees = dao.getAllEmployees();
                request.setAttribute("list", employees);
                request.setAttribute("type", "employees");
            } else if ("customers".equals(view)) {
                List<User> customers = dao.getAllCustomers();
                request.setAttribute("list", customers);
                request.setAttribute("type", "customers");
            } else if ("trains".equals(view)) {
                List<Train> trains = dao.getAllTrains();
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
        String type = request.getParameter("type");
        int id = Integer.parseInt(request.getParameter("id"));

        if ("delete".equals(action)) {
            if ("employees".equals(type)) {
                dao.deleteEmployee(id);
            } else if ("customers".equals(type)) {
                dao.deleteCustomer(id);
            } else if ("trains".equals(type)) {
                dao.deleteTrain(id);
            }
        }
        response.sendRedirect("admin?view=" + type);
    }
}
