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

    public int insertRefund(int userID, double totalRefund) throws SQLException {
        String sql = "INSERT INTO Booking (UserID, TripID, RoundTripTripID, BookingDate, TotalPrice, PaymentStatus, BookingStatus) "
                + "VALUES (?, NULL, NULL, GETDATE(), ?, 'Refund', 'Expired')";

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userID);
            ps.setDouble(2, totalRefund);

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
        String sql = "SELECT * FROM (SELECT b.*, u.FullName, u.PhoneNumber, u.Email, "
                + "t.TrainName, "
                + "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, "
                + "tr.DepartureTime, tr.ArrivalTime, "
                + "COUNT(ti.TicketID) AS TicketCount, "
                + // Thêm đếm số vé
                "ROW_NUMBER() OVER (ORDER BY b.BookingID) as row_num "
                + "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + "JOIN Trip tr ON b.TripID = tr.TripID "
                + "JOIN Train t ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID "
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "LEFT JOIN Ticket ti ON b.BookingID = ti.BookingID "
                + // LEFT JOIN với bảng Ticket
                "WHERE 1=1 ";

        // ... (Phần lọc giữ nguyên, không thay đổi) ...
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
            sql += " AND tr.RouteID = ? "; // Add condition for routeId
        }

        // Thêm GROUP BY vào cuối câu truy vấn (TRƯỚC pagination)
        sql += " GROUP BY b.BookingID, u.FullName, u.PhoneNumber, u.Email, t.TrainName, st1.StationName, st2.StationName, tr.DepartureTime, tr.ArrivalTime, b.TotalPrice, b.PaymentStatus,b.BookingStatus,b.BookingDate,b.UserID,b.TripID,b.RoundTripTripID, r.RouteID";
        sql += ") as x WHERE row_num BETWEEN ? AND ?"; // Add pagination

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // ... (Đặt các tham số lọc, y như cũ) ...
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

            ps.setInt(paramIndex++, (page - 1) * pageSize + 1); // Offset
            ps.setInt(paramIndex++, page * pageSize); // Limit

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingDTO booking = new BookingDTO();
                    // ... (Lấy các trường thông tin của Booking như cũ) ...
                    booking.setBookingID(rs.getInt("BookingID"));
                    booking.setUserID(rs.getInt("UserID"));
                    booking.setTripID(rs.getInt("TripID"));
                    // ... set other Booking fields ...
                    booking.setTotalPrice(rs.getDouble("TotalPrice"));
                    booking.setPaymentStatus(rs.getString("PaymentStatus"));
                    booking.setBookingStatus(rs.getString("BookingStatus"));
                    Timestamp bookingTimestamp = rs.getTimestamp("BookingDate");
                    if (bookingTimestamp != null) {
                        booking.setBookingDate(bookingTimestamp.toLocalDateTime());
                    }
                    // Set related object data (from JOINs)
                    booking.setCustomerName(rs.getString("FullName"));  // From User table
                    booking.setCustomerPhone(rs.getString("PhoneNumber"));
                    booking.setCustomerEmail(rs.getString("Email"));
                    booking.setTrainName(rs.getString("TrainName"));     // From Train table
                    booking.setRouteName(rs.getString("RouteName"));
                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        booking.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        booking.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }

                    // Quan trọng: Lấy số lượng vé
                    booking.setTotalTickets(rs.getInt("TicketCount"));

                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý exception
        }
        return bookings;
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

    // Method to get the total count of bookings (for pagination)
    public int getTotalBookingCount(String customerName, String phone, String email, String status, LocalDate startDate, LocalDate endDate, Integer routeId) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total "
                + "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + // Join with User
                "JOIN Trip tr ON b.TripID = tr.TripID "
                + // Join with Trip
                "JOIN Route r ON tr.RouteID = r.RouteID "
                + "WHERE 1=1 ";

        // Add WHERE clauses for filtering.  Use PreparedStatement parameters!
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
            sql += " AND b.PaymentStatus = ? ";
        }
        if (startDate != null) {
            sql += " AND b.BookingDate >= ? ";
        }
        if (endDate != null) {
            sql += " AND b.BookingDate <= ? ";
        }
        if (routeId != null) {
            sql += " AND tr.RouteID = ? ";
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
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(startDate.atStartOfDay()));
            }
            if (endDate != null) {
                ps.setTimestamp(paramIndex++, Timestamp.valueOf(endDate.atTime(23, 59, 59)));
            }
            if (routeId != null) {
                ps.setInt(paramIndex++, routeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total"); // Use the alias
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public BookingDTO getBookingById(int bookingID) {
        BookingDTO booking = null;
        String sql = "SELECT b.*, u.FullName, u.PhoneNumber, u.Email, "
                + "t.TrainName, "
                + "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, "
                + "tr.DepartureTime, tr.ArrivalTime "
                + "FROM Booking b "
                + "JOIN [User] u ON b.UserID = u.UserID "
                + "JOIN Trip tr ON b.TripID = tr.TripID "
                + "JOIN Train t ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID"
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "WHERE b.BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    booking = new BookingDTO();
                    booking.setBookingID(rs.getInt("BookingID"));
                    booking.setUserID(rs.getInt("UserID"));
                    booking.setTripID(rs.getInt("TripID"));
                    // ... set other Booking fields ...
                    booking.setTotalPrice(rs.getDouble("TotalPrice"));
                    booking.setPaymentStatus(rs.getString("PaymentStatus"));
                    booking.setBookingStatus(rs.getString("BookingStatus"));
                    Timestamp bookingTimestamp = rs.getTimestamp("BookingDate");
                    if (bookingTimestamp != null) {
                        booking.setBookingDate(bookingTimestamp.toLocalDateTime());
                    }
                    // Set related object data (from JOINs)
                    booking.setCustomerName(rs.getString("FullName"));  // From User table
                    booking.setCustomerPhone(rs.getString("PhoneNumber"));
                    booking.setCustomerEmail(rs.getString("Email"));
                    booking.setTrainName(rs.getString("TrainName"));     // From Train table
                    booking.setRouteName(rs.getString("RouteName")); // Combined station names

                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        booking.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        booking.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }
                    // Don't set tickets here!  Handle that separately in the controller.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
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

    public boolean cancelBooking(int bookingID) {
        String sql = "UPDATE Booking SET BookingStatus = 'Cancelled' WHERE BookingID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
