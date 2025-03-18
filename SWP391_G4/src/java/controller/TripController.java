package controller;

import dal.TrainDBContext;
import dal.TripDBContext; // Use the dedicated TripDBContext
import dal.RouteDBContext;
import dto.TrainDTO;
import dto.TripDTO;
import dto.RouteDTO; // Import RouteDTO
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import model.User;

public class TripController extends HttpServlet {

    private TripDBContext tripDB;  // Use TripDBContext, *not* TrainDBContext
    private TrainDBContext trainDB; // For populating the Train dropdown
    private RouteDBContext routeDB; // For populating the Route dropdown

    @Override
    public void init() throws ServletException {
        tripDB = new TripDBContext(); // Initialize TripDBContext
        trainDB = new TrainDBContext(); // Initialize TrainDBContext
        routeDB = new RouteDBContext(); // Initialize RouteDBContext
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        if (action == null) {
            action = "list"; // Default action is to list trips
        }

        switch (action) {
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteTrip(request, response);
                break;
            case "list": // Handle filtering within the list action
            default: // Default to list (including initial display and filtering)
                listTrips(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            listTrips(request, response);
        }

        switch (action) {
            case "add":
                addTrip(request, response);
                break;
            case "update":
                updateTrip(request, response);
                break;
            case "list": // Usually, POST is not used for filtering.
                listTrips(request, response); // Just call listTrips for consistency.
                break;
            default:
                listTrips(request, response);
        }
    }

    private void listTrips(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Pagination parameters
        String pageParam = request.getParameter("page");
        int page = (pageParam != null && pageParam.matches("\\d+")) ? Integer.parseInt(pageParam) : 1;
        int pageSize = 10; // Or get this from a configuration

        // Filtering parameters (get them *before* calling getTrips)
        String departStation = request.getParameter("departStation");
        String arriveStation = request.getParameter("arriveStation");
        String departureDateStr = request.getParameter("departureDate");
        LocalDateTime departureDate = null;
        if (departureDateStr != null && !departureDateStr.isEmpty()) {
            try {
                departureDate = LocalDate.parse(departureDateStr).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Handle date parsing error (e.g., set an error message)
                request.setAttribute("error", "Invalid departure date format.");
                //  Maybe don't return, but set trips to an empty list, and let the JSP display the error.
            }
        }

        List<TripDTO> trips;
        int totalTrips;

        //Check if has filter or not
        if (departStation != null || arriveStation != null || departureDate != null) {
            trips = tripDB.getFilteredTrips(departStation, arriveStation, departureDate, page, pageSize);
            totalTrips = tripDB.getFilteredTripsCount(departStation, arriveStation, departureDate);
        } else {
            trips = tripDB.getAllTrips(page, pageSize);
            totalTrips = tripDB.getTotalTripsCount();
        }

        int totalPages = (int) Math.ceil((double) totalTrips / pageSize);

        request.setAttribute("trips", trips);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        // Pass filter parameters back to the JSP to maintain filter state.
        request.setAttribute("departStation", departStation); //For displaying value on filter
        request.setAttribute("arriveStation", arriveStation); //For displaying value on filter
        request.setAttribute("departureDate", departureDateStr); //For displaying value on filter

        request.getRequestDispatcher("view/employee/main.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch necessary data for dropdowns (Trains and Routes)
        List<TrainDTO> trains = trainDB.getAllTrains(); // Lấy danh sách TÀU (không có tham số)
        List<RouteDTO> routes = routeDB.getAllRoutes();// Get ALL routes
        request.setAttribute("trains", trains);
        request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/trip_add.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int tripID = Integer.parseInt(request.getParameter("id"));
        TripDTO trip = tripDB.getTripById(tripID);

        if (trip == null) {
            // Handle the case where the trip is not found
            response.sendRedirect("trip"); // Redirect to list, or show an error page
            return;
        }
        // Get trains and routes
        List<TrainDTO> trains = trainDB.getAllTrains();// Lấy danh sách TÀU (không có tham số)
        List<RouteDTO> routes = routeDB.getAllRoutes();

        request.setAttribute("trip", trip);
        request.setAttribute("trains", trains);
        request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/trip_edit.jsp").forward(request, response);
    }

    // Inside your TripController.java

private void addTrip(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try {
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        int routeID = Integer.parseInt(request.getParameter("routeID"));
        String departureTimeStr = request.getParameter("departureTime");
        String arrivalTimeStr = request.getParameter("arrivalTime");
        String tripStatus = request.getParameter("tripStatus");

        // Date/Time Parsing and Validation (CRUCIAL)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
        LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);

        // *** VALIDATION: Arrival Time must be AFTER Departure Time ***
        if (!arrivalTime.isAfter(departureTime)) {
            // Validation failed!
            request.setAttribute("error", "Arrival time must be after departure time.");
            // Re-populate the form data (so the user doesn't lose their input)
            List<TrainDTO> trains = trainDB.getAllTrains();
            List<RouteDTO> routes = routeDB.getAllRoutes();
            request.setAttribute("trains", trains);
            request.setAttribute("routes", routes);

             // Create a TripDTO object and set value into it.
            TripDTO prefilledTrip = new TripDTO();
            prefilledTrip.setTrainID(trainID);
            prefilledTrip.setRouteID(routeID);
            prefilledTrip.setDepartureTime(departureTime);
            prefilledTrip.setArrivalTime(arrivalTime);
            prefilledTrip.setTripStatus(tripStatus);

            request.setAttribute("trip", prefilledTrip); // Use "trip" attribute
            // Forward back to the add form (trip_add.jsp)
            request.getRequestDispatcher("view/employee/trip_add.jsp").forward(request, response);
            return; // IMPORTANT: Stop processing here!
        }

        // Create TripDTO object (if validation passed)
        TripDTO newTrip = new TripDTO();
        newTrip.setTrainID(trainID);
        newTrip.setRouteID(routeID);
        newTrip.setDepartureTime(departureTime);
        newTrip.setArrivalTime(arrivalTime);
        newTrip.setTripStatus(tripStatus);
        // Add the trip
        boolean added = tripDB.addTrip(newTrip);

        if (added) {
            request.setAttribute("message", "Trip added successfully!");
        } else {
            request.setAttribute("error", "Failed to add trip.");
        }
       response.sendRedirect("trip"); // Redirect to the trip list

    } catch (NumberFormatException e) {
        request.setAttribute("error", "Invalid number format.");
         List<TrainDTO> trains = trainDB.getAllTrains();
         List<RouteDTO> routes = routeDB.getAllRoutes();
         request.setAttribute("trains", trains);
         request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/trip_add.jsp").forward(request, response);
    } catch (DateTimeParseException e) {
        request.setAttribute("error", "Invalid date/time format.  Use yyyy-MM-ddTHH:mm");
         List<TrainDTO> trains = trainDB.getAllTrains();
        List<RouteDTO> routes = routeDB.getAllRoutes();
        request.setAttribute("trains", trains);
         request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/trip_add.jsp").forward(request, response);
    } catch (Exception e) {
        request.setAttribute("error", "An error occurred: " + e.getMessage());
          List<TrainDTO> trains = trainDB.getAllTrains();
         List<RouteDTO> routes = routeDB.getAllRoutes();
        request.setAttribute("trains", trains);
         request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/trip_add.jsp").forward(request, response);
    }
}

//Similar changes in update method
 private void updateTrip(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
          try {
                int tripID = Integer.parseInt(request.getParameter("tripID"));
                int trainID = Integer.parseInt(request.getParameter("trainID"));
                int routeID = Integer.parseInt(request.getParameter("routeID"));
                String departureTimeStr = request.getParameter("departureTime");
                String arrivalTimeStr = request.getParameter("arrivalTime");
                String tripStatus = request.getParameter("tripStatus");
                // Convert date/time strings to LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);

                 // *** VALIDATION: Arrival Time must be AFTER Departure Time ***
                if (!arrivalTime.isAfter(departureTime)) {
                    // Validation failed!
                    request.setAttribute("error", "Arrival time must be after departure time.");

                    // Re-populate form data
                    List<TrainDTO> trains = trainDB.getAllTrains();
                    List<RouteDTO> routes = routeDB.getAllRoutes();
                    request.setAttribute("trains", trains);
                    request.setAttribute("routes", routes);

                    // Set the *existing* trip data back into the request (for editing)
                    TripDTO trip = new TripDTO();
                    trip.setTripID(tripID);  // Don't forget the ID!
                    trip.setTrainID(trainID);
                    trip.setRouteID(routeID);
                    trip.setDepartureTime(departureTime);
                    trip.setArrivalTime(arrivalTime);
                    trip.setTripStatus(tripStatus);

                    request.setAttribute("trip", trip);  // VERY important

                    request.getRequestDispatcher("view/employee/trip_edit.jsp").forward(request, response);
                    return; // Stop processing here!
                }

                // Create TripDTO object
                TripDTO updatedTrip = new TripDTO();
                updatedTrip.setTripID(tripID);
                updatedTrip.setTrainID(trainID);
                updatedTrip.setRouteID(routeID);
                updatedTrip.setDepartureTime(departureTime);
                updatedTrip.setArrivalTime(arrivalTime);
                updatedTrip.setTripStatus(tripStatus);
                // Update the trip
                boolean updated = tripDB.updateTrip(updatedTrip);
                if (updated) {
                    request.setAttribute("message", "Trip updated successfully!");
                } else {
                    request.setAttribute("error", "Failed to update trip.");
                }
                response.sendRedirect("trip"); // Redirect to trip list

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid number format.");
                showEditForm(request, response);
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Invalid date/time format. Use yyyy-MM-ddTHH:mm");
                showEditForm(request, response);
            }  catch (Exception e) {
                request.setAttribute("error", "An error occurred: " + e.getMessage());
                showEditForm(request, response);
            }
    }

    private void deleteTrip(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int tripID = Integer.parseInt(request.getParameter("id"));
        boolean deleted = tripDB.deleteTrip(tripID); // Call deleteTrip
        if (deleted) {
            request.setAttribute("message", "Trip deleted successfully.");
        } else {
            request.setAttribute("error", "Failed to delete trip.");
        }
        response.sendRedirect("trip"); // Redirect to the trip list
    }

}
