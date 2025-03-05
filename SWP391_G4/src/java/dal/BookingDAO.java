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

/**
 *
 * @author Admin
 */
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
