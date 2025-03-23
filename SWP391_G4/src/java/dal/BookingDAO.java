/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import model.Booking;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import dto.BookingDTO;
import dto.TicketDTO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.Statement;

public class BookingDAO extends DBContext {

    public int insertBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Booking (UserID, TripID, RoundTripTripID, BookingDate, TotalPrice, PaymentStatus, BookingStatus) "
                + "VALUES (?, ?, ?, GETDATE(), ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserID());
            ps.setInt(2, booking.getTripID());

            // Nếu RoundTripTripID == null => setNull
            if (booking.getRoundTripTripID() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, booking.getRoundTripTripID());
            }

            ps.setDouble(4, booking.getTotalPrice());
            ps.setString(5, booking.getPaymentStatus());
            ps.setString(6, booking.getBookingStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            // Lấy BookingID auto-increment
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    /**
     * Cập nhật trạng thái booking (Active, Expired, ...)
     */
    public boolean updateBookingStatus(int bookingID, String status) throws SQLException {
        String sql = "UPDATE Booking SET BookingStatus = ? WHERE BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingID);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateBookingPaymentStatus(int bookingID, String paymentStatus) throws SQLException {
        String sql = "UPDATE Booking SET PaymentStatus = ? WHERE BookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setInt(2, bookingID);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Lấy danh sách toàn bộ Booking
     */
    public ArrayList<Booking> getAllBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bookings.add(new Booking(
                        rs.getInt("BookingID"),
                        rs.getInt("UserID"),
                        rs.getInt("TripID"),
                        rs.getObject("RoundTripTripID") == null ? null : rs.getInt("RoundTripTripID"),
                        rs.getTimestamp("BookingDate"),
                        rs.getDouble("TotalPrice"),
                        rs.getString("PaymentStatus"),
                        rs.getString("BookingStatus")
                ));
            }
        }
        return bookings;
    }

    public List<BookingDTO> getBookings(int page, int pageSize, String customerName, String phone, String email, String status, LocalDate startDate, LocalDate endDate, Integer routeId) {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT DISTINCT b.*, u.FullName, u.PhoneNumber, u.Email "
                + // Use DISTINCT
                "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + "LEFT JOIN Ticket ti ON b.BookingID = ti.BookingID "
                + // Join with Ticket
                "LEFT JOIN Trip tr ON ti.TripID = tr.TripID "
                + // Join with Trip (via Ticket)
                "LEFT JOIN Train t ON tr.TrainID = t.TrainID "
                + // Join with Train (via Trip)
                "LEFT JOIN Route r ON tr.RouteID = r.RouteID "
                + // Join with Route (via Trip)
                "LEFT JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + // Join with Departure Station
                "LEFT JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + // Join with Arrival Station
                "WHERE 1=1 ";

        // ... (Rest of your filtering conditions for customerName, phone, email, status, startDate, endDate - NO CHANGES HERE) ...
        if (customerName != null && !customerName.isEmpty()) {
            sql += " AND u.FullName LIKE ? ";
        }
        if (phone != null && !phone.isEmpty()) {
            sql += " AND u.PhoneNumber LIKE ? ";
        }
        if (email != null && !email.isEmpty()) {
            sql += " AND u.Email LIKE ? ";
        }
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sql += " AND b.PaymentStatus = ? "; // Use PaymentStatus for filtering
        }

        if (startDate != null) {
            sql += " AND b.BookingDate >= ? ";
        }
        if (endDate != null) {
            sql += " AND b.BookingDate <= ? ";
        }
        // --- ROUTE FILTERING (CRITICAL CHANGE) ---
        if (routeId != null) {
            sql += " AND tr.RouteID = ? "; // Filter by RouteID from the Trip table (via Ticket)
        }

        sql += " ORDER BY b.BookingID";
        sql += " OFFSET " + (page - 1) * pageSize + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set parameters for filtering (including routeId)
            int paramIndex = 1;
            if (customerName != null && !customerName.isEmpty()) {
                ps.setString(paramIndex++, "%" + customerName + "%"); // Use % for LIKE
            }
            if (phone != null && !phone.isEmpty()) {
                ps.setString(paramIndex++, "%" + phone + "%");
            }
            if (email != null && !email.isEmpty()) {
                ps.setString(paramIndex++, "%" + email + "%");
            }
            if (status != null && !status.isEmpty() && !status.equals("All")) {
                ps.setString(paramIndex++, status);
            }

            if (startDate != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(startDate.atStartOfDay())); // Convert LocalDate to Timestamp
            }
            if (endDate != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(endDate.atTime(23, 59, 59))); // End of day
            }
            if (routeId != null) {
                ps.setInt(paramIndex++, routeId); // Set the routeId parameter
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // ... (Rest of your result set processing - NO MAJOR CHANGES HERE) ...
                    BookingDTO booking = new BookingDTO();
                    booking.setBookingID(rs.getInt("BookingID"));
                    booking.setUserID(rs.getInt("UserID"));
                    booking.setTripID(rs.getInt("TripID"));
                    booking.setTotalPrice(rs.getDouble("TotalPrice"));
                    booking.setPaymentStatus(rs.getString("PaymentStatus"));
                    booking.setBookingStatus(rs.getString("BookingStatus"));
                    Timestamp bookingTimestamp = rs.getTimestamp("BookingDate");
                    if (bookingTimestamp != null) {
                        booking.setBookingDate(bookingTimestamp.toLocalDateTime());
                    }
                    booking.setCustomerName(rs.getString("FullName"));
                    booking.setCustomerPhone(rs.getString("PhoneNumber"));
                    booking.setCustomerEmail(rs.getString("Email"));
                    TicketDAO ticketDAO = new TicketDAO();
                    List<TicketDTO> tickets = ticketDAO.getTicketsByBookingId(booking.getBookingID());
                    booking.setTickets(tickets);
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
//Add getTotalBookingCount:

    public int getTotalBookingCount(String customerName, String phone, String email, String status, LocalDate startDate, LocalDate endDate, Integer routeId) {
        int total = 0;
        String sql = "SELECT COUNT(DISTINCT b.BookingID) "
                + // Count distinct bookings
                "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + "LEFT JOIN Ticket ti ON b.BookingID = ti.BookingID "
                + // Join for route filtering
                "LEFT JOIN Trip tr ON ti.TripID = tr.TripID "
                + "WHERE 1=1 ";

        if (customerName != null && !customerName.isEmpty()) {
            sql += " AND u.FullName LIKE ? ";
        }
        if (phone != null && !phone.isEmpty()) {
            sql += " AND u.PhoneNumber LIKE ? ";
        }
        if (email != null && !email.isEmpty()) {
            sql += " AND u.Email LIKE ? ";
        }
        if (status != null && !status.isEmpty() && !status.equals("All")) {
            sql += " AND b.PaymentStatus = ? "; // Use PaymentStatus for filtering
        }

        if (startDate != null) {
            sql += " AND b.BookingDate >= ? ";
        }
        if (endDate != null) {
            sql += " AND b.BookingDate <= ? ";
        }
        if (routeId != null) {
            sql += " AND tr.RouteID = ? "; // Filter by RouteID (via Ticket and Trip)
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            if (customerName != null && !customerName.isEmpty()) {
                ps.setString(paramIndex++, "%" + customerName + "%"); // Use % for LIKE
            }
            if (phone != null && !phone.isEmpty()) {
                ps.setString(paramIndex++, "%" + phone + "%");
            }
            if (email != null && !email.isEmpty()) {
                ps.setString(paramIndex++, "%" + email + "%");
            }
            if (status != null && !status.isEmpty() && !status.equals("All")) {
                ps.setString(paramIndex++, status);
            }

            if (startDate != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(startDate.atStartOfDay())); // Convert LocalDate to Timestamp
            }
            if (endDate != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(endDate.atTime(23, 59, 59))); // End of day
            }
            if (routeId != null) {
                ps.setInt(paramIndex++, routeId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1); // Get the count from the first column
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return total;
    }

    public int getBookingCountByStatus(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM Booking WHERE PaymentStatus = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return count;
    }

    // In BookingDAO.java
    public BookingDTO getBookingById(int bookingId) {
        BookingDTO booking = null; // Initialize to null
        String sql = "SELECT b.*, u.FullName, u.PhoneNumber, u.Email, "
                + "t.TrainName, "
                + // Get TrainName for display
                "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, "
                + // Get RouteName
                "tr.DepartureTime, tr.ArrivalTime "
                + //DepartureTime and ArrivalTime
                "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + "JOIN Trip tr ON b.TripID = tr.TripID "
                + //Join with Trip to get the other datas
                "JOIN Train t ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID "
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "WHERE b.BookingID = ?"; // No LEFT JOIN Ticket here

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Use if, not while, since it's a single booking
                    booking = new BookingDTO();
                    booking.setBookingID(rs.getInt("BookingID"));
                    booking.setUserID(rs.getInt("UserID"));
                    booking.setTripID(rs.getInt("TripID"));
                    booking.setTotalPrice(rs.getDouble("TotalPrice"));
                    booking.setPaymentStatus(rs.getString("PaymentStatus"));
                    booking.setBookingStatus(rs.getString("BookingStatus"));
                    Timestamp bookingTimestamp = rs.getTimestamp("BookingDate");
                    if (bookingTimestamp != null) {
                        booking.setBookingDate(bookingTimestamp.toLocalDateTime());
                    }
                    booking.setCustomerName(rs.getString("FullName"));
                    booking.setCustomerPhone(rs.getString("PhoneNumber"));
                    booking.setCustomerEmail(rs.getString("Email"));
                    booking.setTrainName(rs.getString("TrainName"));     // Get TrainName
                    booking.setRouteName(rs.getString("RouteName"));     //Get RouteName
                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        booking.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        booking.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error properly
        }
        return booking;
    }

    // Add other methods (add, update, delete, etc.) as needed.  Make sure to use
    // PreparedStatements and handle SQLExceptions.
    public boolean addBooking(BookingDTO booking) {
        // ... (Implementation for adding a booking) ...
        String sql = "INSERT INTO Booking (UserID, TripID, TotalPrice, PaymentStatus, BookingStatus) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserID());
            ps.setInt(2, booking.getTripID());
            ps.setDouble(3, booking.getTotalPrice());
            ps.setString(4, booking.getPaymentStatus());
            ps.setString(5, booking.getBookingStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false; // Indicate failure
            }
            //Get ID
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookingId = generatedKeys.getInt(1);
                    booking.setBookingID(bookingId); // Cập nhật ID cho đối tượng Booking

                    return true;
                } else {
                    return false; // Không lấy được ID
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBooking(BookingDTO booking) {
        String sql = "UPDATE Booking SET UserID = ?, TripID = ?, TotalPrice = ?, PaymentStatus = ?, BookingStatus = ? WHERE BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserID());
            ps.setInt(2, booking.getTripID());
            ps.setDouble(3, booking.getTotalPrice());
            ps.setString(4, booking.getPaymentStatus());
            ps.setString(5, booking.getBookingStatus());
            ps.setInt(6, booking.getBookingID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelBooking(int bookingID) throws SQLException {
    // Use a transaction to ensure atomicity
    connection.setAutoCommit(false);
    PreparedStatement updateBooking = null;
    PreparedStatement updateTicket = null;
    PreparedStatement updateSeat = null;

    try {
        // 1. Update the Booking status
        String updateBookingSql = "UPDATE Booking SET BookingStatus = 'Cancelled' WHERE BookingID = ?";
        updateBooking = connection.prepareStatement(updateBookingSql);
        updateBooking.setInt(1, bookingID);
        int bookingRowsAffected = updateBooking.executeUpdate();

        // Check if booking exists
        if (bookingRowsAffected == 0) {
            connection.rollback(); // Rollback if no booking found
            return false; // Indicate failure
        }
        // 2. Update the Ticket status for all tickets associated with the booking
        String updateTicketSql = "UPDATE Ticket SET TicketStatus = 'Cancelled' WHERE BookingID = ?";
        updateTicket = connection.prepareStatement(updateTicketSql);
        updateTicket.setInt(1, bookingID);
        updateTicket.executeUpdate(); // We don't check rowsAffected here; it's OK if there are no tickets

        // 3. Update the Seat status for all seats associated with the cancelled tickets
        // Join with Ticket table on bookingId
        String updateSeatSql = "UPDATE Seat SET Status = 'Available' " +
                               "WHERE CarriageID IN (SELECT c.CarriageID FROM Carriage c JOIN Seat s ON c.CarriageID = s.CarriageID "
                + "JOIN Ticket t ON s.SeatID = t.SeatID WHERE t.BookingID= ?)";
        updateSeat = connection.prepareStatement(updateSeatSql);
        updateSeat.setInt(1, bookingID);
        updateSeat.executeUpdate();
        connection.commit(); // Commit the transaction
        return true;        // Success

    } catch (SQLException e) {
        connection.rollback(); // Rollback on any error
        e.printStackTrace();
        throw e;            // Re-throw the exception after rollback (important!)

    } finally {
        // Close resources (in reverse order of creation)
        if (updateSeat != null) {
            try {
                updateSeat.close();
            } catch (SQLException e) {
                 e.printStackTrace();
            }
        }
         if (updateTicket != null) {
            try {
                updateTicket.close();
            } catch (SQLException e) {
                 e.printStackTrace();
            }
        }
         if (updateBooking != null) {
            try {
                updateBooking.close();
            } catch (SQLException e) {
                 e.printStackTrace();
            }
        }

        try {
            connection.setAutoCommit(true); // Restore auto-commit
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public boolean deleteBooking(int bookingID) {
        String sql = "DELETE FROM Booking WHERE BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0; // Return true if rows were deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
