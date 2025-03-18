package controller;

import dal.StationDAO;
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

        if (action == null || "list".equals(action)) {
            listStations(request, response);
        } else if ("edit".equals(action)) {
            showEditForm(request, response);
        } else if ("delete".equals(action)) {
            deleteStation(request, response); // No longer need to catch exception here
        } else {
            response.sendRedirect("station");
        }
    }

    private void listStations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = 1;
        int pageSize = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Station> stations = stationDAO.getStations(page, pageSize);
        int totalStations = stationDAO.getTotalStationCount();
        int totalPages = (int) Math.ceil((double) totalStations / pageSize);

        request.setAttribute("stations", stations);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        int stationID = Integer.parseInt(request.getParameter("stationID"));
        Station station = stationDAO.getStationById(stationID);

        if (station == null) {
            response.sendRedirect("station");
            return;
        }

        request.setAttribute("station", station);
        request.setAttribute("stations", stationDAO.getAllStations()); // Get *all* stations
        request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
    }



    private void deleteStation(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        int stationID = Integer.parseInt(request.getParameter("stationID"));

        if (stationDAO.isStationUsed(stationID)) {
            // **CRITICAL FIX: Pass error message AND current page to the JSP.**
            request.setAttribute("error", "Cannot delete station. It is used in one or more routes.");
            listStations(request, response); // Re-display the list, preserving pagination
            return;
        }

        stationDAO.deleteStation(stationID);
        response.sendRedirect("station?message=Station deleted successfully"); // Redirect with success message
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String name = request.getParameter("stationName");
        String address = request.getParameter("address");

        if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("stationID"));
            Station station = new Station(id, name, address);
            stationDAO.updateStation(station);
            response.sendRedirect("station");
        } else {
            Station station = new Station(0, name, address);
            stationDAO.addStation(station);
            response.sendRedirect("station");
        }

    }
}