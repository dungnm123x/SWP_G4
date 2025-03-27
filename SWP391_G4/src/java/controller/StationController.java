package controller;

import dal.StationDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException; // Import SQLException if needed by DAO
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Station;
import model.User;

public class StationController extends HttpServlet {

    private StationDAO stationDAO = new StationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Check user role - IMPORTANT for security
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || (user.getRoleID() != 1 && user.getRoleID() != 2)) {
            response.sendRedirect("login"); // Redirect to login if not authorized
            return;
        }

        if (action == null || "list".equals(action)) {
            listStations(request, response); // Call the pagination method
        } else if ("edit".equals(action)) {
            showEditForm(request, response);
        } else if ("delete".equals(action)) {
           deleteStation(request, response);
        } else {
            // Handle other actions or redirect to list
            response.sendRedirect("station");
        }
    }
    // Method to get list station with pagination
    private void listStations(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Pagination logic
    int page = 1;  // Default to page 1
    int pageSize = 10; // Set the number of stations per page.  Make this configurable!

    String pageParam = request.getParameter("page");
    if (pageParam != null && !pageParam.isEmpty()) {
        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            // Handle invalid page number (e.g., redirect to page 1 or an error page)
            page = 1; // Reset to page 1 if parsing fails
        }
    }

    List<Station> stations = stationDAO.getStations(page, pageSize); // Get stations for the current page
    int totalStations = stationDAO.getTotalStationCount(); // Get total count
    int totalPages = (int) Math.ceil((double) totalStations / pageSize); // Calculate total pages.  Use double to avoid integer division.

    request.setAttribute("stations", stations); // List of stations for the *current* page
    request.setAttribute("currentPage", page);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("pageSize", pageSize); // Good to have this in the JSP too.

    request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
}
 private void showEditForm(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    int stationID = Integer.parseInt(request.getParameter("stationID"));
    Station station = stationDAO.getStationById(stationID);

    if (station == null) {
        // Handle case where station with given ID is not found
        response.sendRedirect("station"); // Redirect to list (or an error page)
        return;
    }
     // Pass the existing station object and *also* the list of all stations.
    request.setAttribute("station", station);
     List<Station> stations = stationDAO.getStations(1, Integer.MAX_VALUE);
    request.setAttribute("stations", stations); // Keep existing station list
    request.getRequestDispatcher("view/employee/station-management.jsp").forward(request, response);
}

    private void deleteStation(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException{
         int stationID = Integer.parseInt(request.getParameter("stationID"));
         // Check if the station is in use *before* deleting
        if (stationDAO.isStationUsed(stationID)) {
            request.setAttribute("error", "Không thể xóa nhà ga. Nó đang được sử dụng trong một hoặc nhiều tuyến đường.");
            listStations(request, response); // **QUAN TRỌNG: Gọi lại listStations để hiển thị lỗi và phân trang đúng**
            return; // Stop processing!
        }
         stationDAO.deleteStation(stationID);
         response.sendRedirect("station?message=Xóa nhà ga thành công"); // Redirect with success message.
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String name = request.getParameter("stationName");
        String address = request.getParameter("address");

        if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("stationID"));

            // --- VALIDATION FOR UPDATE ---
            if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()) {
                request.setAttribute("error", "Tên ga và địa chỉ không được để trống.");
                // Repopulate necessary data for the view (including pagination) and forward
                Station station = new Station(id, name, address); // Keep attempted values
                request.setAttribute("station", station); // Set the attempted station data
                listStations(request, response); // **QUAN TRỌNG: Gọi lại listStations để hiển thị lỗi và phân trang đúng**
                return; // Stop processing
            }

            // Check for duplicate name (excluding the current station)
            if (stationDAO.isStationNameExist(name, id)) {
                request.setAttribute("error", "Tên ga đã tồn tại.");
                // Repopulate necessary data for the view (including pagination) and forward
                Station station = new Station(id, name, address); // Keep attempted values
                request.setAttribute("station", station); // Set the attempted station data
                listStations(request, response); // **QUAN TRỌNG: Gọi lại listStations**
                return; // Stop processing
            }
            // --- END VALIDATION FOR UPDATE ---

            Station station = new Station(id, name, address);
            stationDAO.updateStation(station);
            response.sendRedirect("station?message=Cập nhật ga thành công"); // Redirect on success

        } else { // Default action is "add"
            // --- VALIDATION FOR ADD ---
            if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()) {
                request.setAttribute("error", "Tên ga và địa chỉ không được để trống.");
                listStations(request, response); // **QUAN TRỌNG: Gọi lại listStations**
                return; // Stop processing
            }

            // Check for duplicate station name
            if (stationDAO.isStationNameExist(name)) {
                request.setAttribute("error", "Tên ga đã tồn tại.");
                listStations(request, response); // **QUAN TRỌNG: Gọi lại listStations**
                return; // Stop processing
            }
            // --- END VALIDATION FOR ADD ---

            Station station = new Station(0, name, address);
            stationDAO.addStation(station);
            response.sendRedirect("station?message=Thêm ga thành công"); // Redirect on success
        }
    }
}