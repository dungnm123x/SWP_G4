package controller;

import dal.RouteDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import model.Route;
import model.Station;
import model.User;

public class RouteController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RouteDAO routeDAO = new RouteDAO();
        List<Route> routes = routeDAO.list();
        List<Station> stations = routeDAO.getAllStations();

        request.setAttribute("routes", routes);
        request.setAttribute("stations", stations);
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }

        // Kiểm tra nếu có ID cần chỉnh sửa
        String editId = request.getParameter("editId");
        if (editId != null) {
            Route route = routeDAO.getRouteById(Integer.parseInt(editId));
            request.setAttribute("editRoute", route);
        }

        request.getRequestDispatcher("view/employee/route-list.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RouteDAO routeDAO = new RouteDAO();
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            int departureStationID = Integer.parseInt(request.getParameter("departureStation"));
            int arrivalStationID = Integer.parseInt(request.getParameter("arrivalStation"));
            double distance = Double.parseDouble(request.getParameter("distance"));
            double basePrice = Double.parseDouble(request.getParameter("basePrice"));

            if (routeDAO.isRouteExists(departureStationID, arrivalStationID)) {
                request.setAttribute("error", "Tuyến tàu này đã tồn tại!");
            } else if (distance < 0 || basePrice < 0) {
                request.setAttribute("error", "Khoảng cách và giá không thể là số âm!");
            } else if (departureStationID == arrivalStationID) {
                request.setAttribute("error", "Ga đi không thể trùng với ga đến.");
            } else {
                boolean success = routeDAO.addRoute(departureStationID, arrivalStationID, distance, basePrice);
                request.setAttribute("message", success ? "Thêm tuyến tàu thành công!" : "Có lỗi xảy ra, vui lòng thử lại.");
            }
        } else if ("update".equals(action)) {
            int routeID = Integer.parseInt(request.getParameter("routeID"));
            int departureStationID = Integer.parseInt(request.getParameter("departureStation"));
            int arrivalStationID = Integer.parseInt(request.getParameter("arrivalStation"));
            double distance = Double.parseDouble(request.getParameter("distance"));
            double basePrice = Double.parseDouble(request.getParameter("basePrice"));

            boolean success = routeDAO.updateRoute(routeID, departureStationID, arrivalStationID, distance, basePrice);
            request.setAttribute("message", success ? "Cập nhật tuyến tàu thành công!" : "Cập nhật thất bại.");
        } else if ("delete".equals(action)) {
            int routeID = Integer.parseInt(request.getParameter("routeID"));
            boolean success = routeDAO.deleteRoute(routeID);
            request.setAttribute("message", success ? "Xóa tuyến tàu thành công!" : "Xóa thất bại(tuyến đang trong sử dụng).");
        }

        doGet(request, response); // Load lại danh sách tuyến tàu
    }
}
