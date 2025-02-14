
package controller;

import dal.StationDAO;
import dto.RailwayDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Station;

public class StationController extends HttpServlet {
    private StationDAO stationDAO = new StationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            int id = Integer.parseInt(request.getParameter("stationID"));
            stationDAO.deleteStation(id);
        }
        List<Station> stations = stationDAO.getStations();

        request.setAttribute("stations", stations );
        request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String name = request.getParameter("stationName");
        String address = request.getParameter("address");

        if (action.equals("edit")) {
            int id = Integer.parseInt(request.getParameter("stationID"));
            stationDAO.updateStation(new Station(id, name, address));
        } else {
            stationDAO.addStation(new Station(0, name, address));
        }
        response.sendRedirect("station");
    }
}