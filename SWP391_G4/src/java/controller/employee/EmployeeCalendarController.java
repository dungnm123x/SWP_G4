package controller.employee;

import dal.DAOAdmin;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.CalendarEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EmployeeCalendarController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || (user.getRoleID() != 2 && user.getRoleID() != 1)) { // Allow RoleID 1 (admin) for testing
            response.sendRedirect("login");
            return;
        }

        // Fetch calendar events for the logged-in employee
        DAOAdmin dao = new DAOAdmin();
        try {
            List<CalendarEvent> events = dao.getAllCalendarEvents();
            request.setAttribute("calendarEvents", events);
            request.getRequestDispatcher("view/employee/employeeCalendar.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error retrieving calendar events: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // No POST actions needed for a read-only calendar view
        doGet(request, response);
    }
}
