package controller;

import dal.BookingDAO;
import dal.TrainDAO;
import dal.TripDAO;
import dal.RouteDBContext;
import dal.TicketDAO;
import dal.UserDAO;
import dto.BookingDTO;
import dto.TicketDTO;
import dto.TrainDTO;
import dto.TripDTO;
import dto.RouteDTO;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class OrderController extends HttpServlet {

    private BookingDAO bookingDAO;
    private TrainDAO trainDAO; // For dropdown in add/edit
    private TripDAO tripDAO;
    private RouteDBContext routeDB;
    private TicketDAO ticketDAO; // For getting tickets by booking ID
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        bookingDAO = new BookingDAO(); // Initialize DAOs
        trainDAO = new TrainDAO();
        tripDAO = new TripDAO();
        routeDB = new RouteDBContext();
        ticketDAO = new TicketDAO();
        userDAO = new UserDAO();
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
            action = "list"; // Default action
        }

        switch (action) {
            case "list":
                listOrders(request, response);
                break;
            case "view":
                showOrderDetails(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "cancel": {
                try {
                    cancelOrder(request, response);
                } catch (SQLException ex) {
                    Logger.getLogger(OrderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;

            case "getTrips":
                getTripsByTrain(request, response); // AJAX handler
                break;
            default:
                response.sendRedirect("order"); // Or an error page
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "add":
                addOrder(request, response);
                break;
            case "update":
                updateOrder(request, response);
                break;
            default:
                listOrders(request, response); // Or an appropriate error response.
        }
    }

    private void listOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ... (Pagination parameters - no changes) ...
        int page = 1;
        int pageSize = 10;  // You can make this configurable
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                // Handle invalid page number (e.g., redirect to page 1 or show an error)
                page = 1; // Reset to page 1 if parsing fails
            }
        }
        // ... (Filtering parameters - ensure routeId is handled) ...
        //String customerName = request.getParameter("customerName");
        //String phone = request.getParameter("phone");
        //String email = request.getParameter("email");
        String customerName = request.getParameter("customerName");
        customerName = (customerName != null) ? customerName.trim() : "";
        String phone = request.getParameter("phone");
        phone = (phone != null) ? phone.trim() : "";
        String email = request.getParameter("email");
        email = (email != null) ? email.trim() : "";
        String status = request.getParameter("status");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String routeIdStr = request.getParameter("routeId");
        Integer routeId = null; // Use Integer, not int, to allow for null

        if (routeIdStr != null && !routeIdStr.trim().isEmpty()) {
            try {
                routeId = Integer.parseInt(routeIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid Route ID format.");
                request.getRequestDispatcher("view/employee/order_management.jsp").forward(request, response);
                return;
            }
        }
        LocalDate startDate = null;
        LocalDate endDate = null;

        // Date parsing, with error handling
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDate.parse(startDateStr, dateFormatter);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDate.parse(endDateStr, dateFormatter);
            }
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date format.  Please use yyyy-MM-dd.");
            request.getRequestDispatcher("view/employee/order_management.jsp").forward(request, response); //Show error message
            return; // Stop processing
        }
        // Get the filtered, paginated list of bookings (using the modified BookingDAO method)
        List<BookingDTO> bookings = bookingDAO.getBookings(page, pageSize, customerName, phone, email, status, startDate, endDate, routeId);

        // ... (Get totalPages - should now work correctly with route filtering) ...
        int totalBookings = bookingDAO.getTotalBookingCount(customerName, phone, email, status, startDate, endDate, routeId);
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        // ... (Set request attributes - no changes) ...
        List<RouteDTO> routes = routeDB.getAllRoutes();  // *** ADD THIS LINE ***
        request.setAttribute("routes", routes); // *** AND THIS LINE ***

        // Set attributes for the JSP
        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize); // Good practice to pass this

        // Pass filter parameters back to the JSP (for "sticky" filters)
        request.setAttribute("customerName", customerName);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);
        request.setAttribute("status", status);
        request.setAttribute("startDate", startDateStr); // Pass back the raw strings
        request.setAttribute("endDate", endDateStr);
        request.setAttribute("routeId", routeIdStr);

        // --- Statistics ---  (Keep these lines, they are correct)
        int totalOrders = bookingDAO.getTotalBookingCount(null, null, null, null, null, null, null); // Get *unfiltered* total
        int paidOrders = bookingDAO.getBookingCountByStatus("Paid");
        int pendingOrders = bookingDAO.getBookingCountByStatus("Pending"); // Assuming "Pending" is your "Chưa thanh toán" status
        int cancelledOrders = bookingDAO.getBookingCountByStatus("Cancelled");

        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("paidOrders", paidOrders);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.getRequestDispatcher("view/employee/order_management.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch necessary data for dropdowns (Trains and Routes)
        List<TrainDTO> trains = trainDAO.getAllTrains();
        List<RouteDTO> routes = routeDB.getAllRoutes();
        request.setAttribute("trains", trains);
        request.setAttribute("routes", routes);
        request.getRequestDispatcher("view/employee/order_add.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int bookingID = Integer.parseInt(request.getParameter("id"));
        BookingDTO booking = bookingDAO.getBookingById(bookingID); // Get the Booking

        if (booking == null) {
            response.sendRedirect("order"); // Handle booking not found
            return;
        }

        // Get user information
        UserDTO user = userDAO.gett(booking.getUserID());

        // Get tickets
        List<TicketDTO> tickets = ticketDAO.getTicketsByBookingId(bookingID);
        booking.setTickets(tickets);

        // Get all trains for the dropdown
        List<TrainDTO> trains = trainDAO.getAllTrains();
        request.setAttribute("trains", trains);

        // Get available trips by trainId for the dropdown.  Option 2 (LESS GOOD)
        // 1.  Get the TripDTO from the BookingDTO
        TripDTO trip = booking.getTrip();

        List<TripDTO> trips = new ArrayList<>(); // Initialize to empty list

        // 2. Check if trip is NOT null (important for robustness)
        if (trip != null) {
            // 3. Get the TrainID from the TripDTO (using getTrainID(), which MUST be in TripDTO)
            trips = tripDAO.getTripsByTrainId(trip.getTrainID()); // Get train ID from TRIP
        } // else:  trips will remain an empty list, which is fine

        request.setAttribute("booking", booking);
        request.setAttribute("trips", trips); // Pass trip list
        request.setAttribute("user", user); // Pass user info
        request.setAttribute("ticket", tickets.isEmpty() ? null : tickets.get(0)); //Safe

        request.getRequestDispatcher("view/employee/order_edit.jsp").forward(request, response);
    }

    // In OrderController.java
    private void showOrderDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int bookingID;
        try {
            bookingID = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid booking ID.");
            request.getRequestDispatcher("view/employee/order_details.jsp").forward(request, response);
            return;
        }

        BookingDTO booking = bookingDAO.getBookingById(bookingID);

        if (booking == null) {
            request.setAttribute("error", "Booking not found.");
            request.getRequestDispatcher("view/employee/order_details.jsp").forward(request, response);
            return;
        }

        // Get user information.  No need to create a new UserDAO here.
        UserDTO user = userDAO.gett(booking.getUserID());

        if (user == null) {
            request.setAttribute("error", "User not found.");
            request.getRequestDispatcher("view/employee/order_details.jsp").forward(request, response);
            return;
        }

        // Get tickets for the booking.
        List<TicketDTO> tickets = ticketDAO.getTicketsByBookingId(bookingID);
        booking.setTickets(tickets);

        request.setAttribute("booking", booking);
        request.setAttribute("user", user);
        // No longer need request.setAttribute("ticket", ...);  We have the full list.

        request.getRequestDispatcher("view/employee/order_details.jsp").forward(request, response);
    }

    private void cancelOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        int bookingID = Integer.parseInt(request.getParameter("id"));

        try {
            boolean cancelled = bookingDAO.cancelBooking(bookingID); // Call the DAO method
            if (cancelled) {
                request.setAttribute("message", "Order cancelled successfully.");
            } else {
                request.setAttribute("error", "Failed to cancel order.");
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            e.printStackTrace(); // Log the exception
        }

        response.sendRedirect("order"); // Redirect to order list
    }

    private void addOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Similar try-catch structure as before, but adapted for your new requirements:
        try {
            int trainID = Integer.parseInt(request.getParameter("trainID"));
            int routeID = Integer.parseInt(request.getParameter("routeID"));
            int tripID = Integer.parseInt(request.getParameter("tripID"));
            String departureTimeStr = request.getParameter("departureTime");
            String arrivalTimeStr = request.getParameter("arrivalTime");
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerEmail = request.getParameter("customerEmail");
            String customerCCCD = request.getParameter("customerCCCD");
            String customerAddress = request.getParameter("customerAddress");
            int numTickets = Integer.parseInt(request.getParameter("numTickets"));
            String paymentStatus = request.getParameter("paymentStatus");
            String bookingStatus = request.getParameter("bookingStatus");

            // Convert date/time strings to LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);

            // Create TripDTO object
            TripDTO trip = new TripDTO();
            trip.setTrainID(trainID);
            trip.setRouteID(routeID);
            trip.setDepartureTime(departureTime);
            trip.setArrivalTime(arrivalTime);

            // Add the trip
            // boolean added = tripDB.addTrip(newTrip);
//                if (added) {
//                    request.setAttribute("message", "Trip added successfully!");
//                } else {
//                    request.setAttribute("error", "Failed to add trip.");
//                }
            response.sendRedirect("trip"); // Redirect to trip list

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format.");
            // showAddForm(request, response); //Re-render the form
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date/time format.  Use isobaric-MM-ddTHH:mm");
            // showAddForm(request, response); //Re-render the form
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            // showAddForm(request, response); //Re-render the form
        }
    }

    private void updateOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Implement update logic, similar to addOrder, but using bookingDAO.updateBooking()
        //  1. Get parameters (including bookingID)
        //  2. Validate parameters
        //  3. Create a BookingDTO object (or update an existing one)
        //  4. Call bookingDAO.updateBooking()
        //  5. Set message/error attributes
        //  6. Redirect to "order" (or forward back to edit form on error)
        try {
            int tripID = Integer.parseInt(request.getParameter("tripID"));
            int trainID = Integer.parseInt(request.getParameter("trainID"));
            int routeID = Integer.parseInt(request.getParameter("routeID"));
            String departureTimeStr = request.getParameter("departureTime");
            String arrivalTimeStr = request.getParameter("arrivalTime");
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerEmail = request.getParameter("customerEmail");
            String customerCCCD = request.getParameter("customerCCCD");
            String customerAddress = request.getParameter("customerAddress");
            int numTickets = Integer.parseInt(request.getParameter("numTickets"));
            String paymentStatus = request.getParameter("paymentStatus");
            String bookingStatus = request.getParameter("bookingStatus");

            // Convert date/time strings to LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, formatter);

            // Create TripDTO object
            TripDTO updatedTrip = new TripDTO();
            updatedTrip.setTripID(tripID);
            updatedTrip.setTrainID(trainID);
            updatedTrip.setRouteID(routeID);
            updatedTrip.setDepartureTime(departureTime);
            updatedTrip.setArrivalTime(arrivalTime);

            // Add the trip
            // boolean added = tripDB.addTrip(newTrip);
//                if (added) {
//                    request.setAttribute("message", "Trip added successfully!");
//                } else {
//                    request.setAttribute("error", "Failed to add trip.");
//                }
            response.sendRedirect("trip"); // Redirect to trip list

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format.");
            showAddForm(request, response); //Re-render the form
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date/time format.  Use isobaric-MM-ddTHH:mm");
            showAddForm(request, response); //Re-render the form
        } catch (Exception e) {
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            showAddForm(request, response); //Re-render the form
        }
    }

    // Method to get trips by train ID (for AJAX)
    protected void getTripsByTrain(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int trainId = Integer.parseInt(request.getParameter("trainId"));
        List<TripDTO> trips = tripDAO.getTripsByTrainId(trainId);

        // Convert the list of trips to JSON format.  Use a library like Gson for this in real projects.
        // For simplicity, we'll do a very basic conversion here:
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < trips.size(); i++) {
            TripDTO trip = trips.get(i);
            json.append("{");
            json.append("\"tripID\":").append(trip.getTripID()).append(",");
            json.append("\"routeName\":\"").append(escapeJsonString(trip.getRouteName())).append("\","); // Escape!
            json.append("\"departureTime\":\"").append(trip.getFormattedDepartureTime()).append("\""); // Use formatted time
            json.append("}");
            if (i < trips.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        // Set content type and write JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

    // Helper function to escape special characters for JSON (VERY BASIC, for demo only)
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\") // Escape backslashes
                .replace("\"", "\\\"") // Escape double quotes
                .replace("\n", "\\n") // Escape newlines
                .replace("\r", "\\r") // Escape carriage returns
                .replace("\t", "\\t");   // Escape tabs
    }

}
