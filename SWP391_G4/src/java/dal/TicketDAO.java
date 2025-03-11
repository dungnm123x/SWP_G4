/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Ticket;
import model.RailwayDTO;

public class TicketDAO extends DBContext {

//    public void insertTicket(Ticket ticket) {
//        String sql = "INSERT INTO Ticket (CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus) "
//                + "VALUES (?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, ticket.getCccd());
//            ps.setInt(2, ticket.getBookingID());
//            ps.setInt(3, ticket.getSeatID());
//            ps.setInt(4, ticket.getTripID());
//            ps.setDouble(5, ticket.getTicketPrice());
//            ps.setString(6, ticket.getTicketStatus());
//            ps.executeUpdate();
//        } catch (SQLException e) {
//
//        }
//    }
    public int insertTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO Ticket (CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        // Tạo PreparedStatement có yêu cầu trả về khóa tự sinh
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getCccd());
            ps.setInt(2, ticket.getBookingID());
            ps.setInt(3, ticket.getSeatID());
            ps.setInt(4, ticket.getTripID());
            ps.setDouble(5, ticket.getTicketPrice());
            ps.setString(6, ticket.getTicketStatus());

            ps.executeUpdate(); // Thực thi INSERT

            // Lấy ResultSet chứa các khóa tự sinh
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // cột 1 là giá trị của ID vừa sinh
                } else {
                    throw new SQLException("No generated key returned.");
                }
            }
        }
    }

    /**
     * Cập nhật trạng thái ghế (seat) (Available, Booked, ...)
     */
    public void updateSeatStatus(int seatID, String status) {
        String sql = "UPDATE Seat SET Status=? WHERE SeatID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, seatID);
            ps.executeUpdate();
        } catch (SQLException e) {

        }
    }

    public void updateTicketStatus(int ticketID, String newStatus) {
        String sql = "UPDATE Ticket SET TicketStatus = ? WHERE TicketID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, ticketID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Hoặc log lỗi
        }
    }

    public boolean ticketExistsByCCCDAndPaid(String cccd, int tripID) {
        String sql = "SELECT 1 "
                + "FROM Ticket t "
                + "JOIN Booking b ON t.BookingID = b.BookingID "
                + "WHERE t.CCCD = ? "
                + "  AND t.TripID = ? "
                + "  AND b.PaymentStatus = 'Paid' ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cccd);
            ps.setInt(2, tripID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // nếu có row => true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<RailwayDTO> getDetailedTicketsByUserID(int userID) {
        List<RailwayDTO> tickets = new ArrayList<>();
        String sql = "SELECT t.TicketID, t.CCCD, s.SeatNumber, c.CarriageNumber, "
                + "st1.StationName AS DepartureStation, st2.StationName AS ArrivalStation, "
                + "tr.DepartureTime, tr.TrainID, trn.TrainName, t.TicketPrice, t.TicketStatus, "
                + "CASE "
                + "   WHEN b.RoundTripTripID IS NOT NULL AND b.RoundTripTripID = t.TripID THEN 'Chuyến về' "
                + "   ELSE 'Chuyến đi' "
                + "END AS TripType "
                + "FROM Ticket t "
                + "JOIN Seat s ON t.SeatID = s.SeatID "
                + "JOIN Carriage c ON s.CarriageID = c.CarriageID "
                + "JOIN Trip tr ON t.TripID = tr.TripID "
                + "JOIN Train trn ON tr.TrainID = trn.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID "
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "JOIN Booking b ON t.BookingID = b.BookingID "
                + "WHERE b.UserID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new RailwayDTO(
                            rs.getInt("TicketID"),
                            rs.getString("CCCD"),
                            rs.getString("DepartureStation") + " → " + rs.getString("ArrivalStation"),
                            rs.getString("TrainName"), // Hiển thị tên tàu
                            rs.getTimestamp("DepartureTime"),
                            rs.getInt("CarriageNumber"),
                            rs.getInt("SeatNumber"),
                            rs.getDouble("TicketPrice"),
                            rs.getString("TicketStatus"),
                            rs.getString("TripType")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByUserID(int userID) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE BookingID IN (SELECT BookingID FROM Booking WHERE UserID = ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new Ticket(
                            rs.getInt("TicketID"),
                            rs.getString("CCCD"),
                            rs.getInt("BookingID"),
                            rs.getInt("SeatID"),
                            rs.getInt("TripID"),
                            rs.getDouble("TicketPrice"),
                            rs.getString("TicketStatus")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
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
