
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Station;
import dal.StationDAO;
import model.User;

public class EditStationController extends HttpServlet {

    private StationDAO stationDAO = new StationDAO();
 @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String action = request.getParameter("action");
    User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }

    if ("edit".equals(action)) {
        int id = Integer.parseInt(request.getParameter("stationID"));
        Station station = stationDAO.getStationById(id);
        request.setAttribute("station", station);
        request.getRequestDispatcher("view/employee/edit-station.jsp").forward(request, response);
        return; 
    }

    // Nếu không phải edit, hiển thị danh sách ga
    List<Station> stations = stationDAO.getStations();
    request.setAttribute("stations", stations);
    request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
}


 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String action = request.getParameter("action");
    String name = request.getParameter("stationName");
    String address = request.getParameter("address");

    if ("edit".equals(action)) {
        int id = Integer.parseInt(request.getParameter("stationID"));
        stationDAO.updateStation(new Station(id, name, address));
    } 

    response.sendRedirect("station");
}



}
