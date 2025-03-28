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

import dto.TicketDTO;
import dto.TrainDTO;
import dto.TripDTO;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.Train;
import model.Carriage;
import model.CartItem;

public class TicketDAO extends DBContext {

    public List<TicketDTO> getTicketsByBookingId(int bookingID) {
        List<TicketDTO> tickets = new ArrayList<>();
        String sql = "SELECT t.*, s.SeatNumber, c.CarriageNumber, c.CarriageType, tr.TrainID, tr.RouteID "
                + "FROM Ticket t "
                + "JOIN Seat s ON t.SeatID = s.SeatID "
                + "JOIN Carriage c ON s.CarriageID = c.CarriageID "
                + "JOIN Trip tr ON t.TripID = tr.TripID "
                + //Join with Trip
                "WHERE t.BookingID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TicketDTO ticket = new TicketDTO();
                    ticket.setTicketID(rs.getInt("TicketID"));
                    ticket.setPassengerName(rs.getString("PassengerName"));
                    ticket.setCccd(rs.getString("CCCD"));
                    ticket.setBookingID(rs.getInt("BookingID"));
                    ticket.setSeatID(rs.getInt("SeatID"));
                    ticket.setTripID(rs.getInt("TripID"));
                    ticket.setTicketPrice(rs.getDouble("TicketPrice"));
                    ticket.setTicketStatus(rs.getString("TicketStatus"));
                    ticket.setSeatNumber(rs.getString("SeatNumber")); // From Seat table
                    ticket.setCarriageNumber(rs.getString("CarriageNumber"));//From Carriage table
                    ticket.setCarriageType(rs.getString("CarriageType"));
                    //Get Train name
                    TrainDAO trainDAO = new TrainDAO();
                    TrainDTO train = trainDAO.getFullTrainInfoById(rs.getInt("TrainID"));
                    ticket.setTrainName(train.getTrainName());
                    //Get Route Name
                    TripDAO tripDAO = new TripDAO();
                    TripDTO trip = tripDAO.getTripById(rs.getInt("TripID"));
                    ticket.setRouteName(trip.getRouteName());
                    ticket.setDepartureTime(trip.getDepartureTime());
                    ticket.setArrivalTime(trip.getArrivalTime());

                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger in a real application
        }
        return tickets;
    }

    public boolean checkAdultTicketValid(String adultTicketCode, CartItem childCartItem) {
        // Câu SQL truy vấn TicketID, Booking -> PaymentStatus, BookingStatus
        String sql = ""
                + "SELECT t.TripID, b.PaymentStatus, b.BookingStatus "
                + "FROM Ticket t "
                + "JOIN Booking b ON t.BookingID = b.BookingID "
                + "WHERE t.TicketID = ?";

        try {
            // Giả sử adultTicketCode là TicketID (kiểu số)
            int adultTicketID = Integer.parseInt(adultTicketCode);

            // Tạo PreparedStatement (dùng connection có sẵn từ DBContext)
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, adultTicketID);

                // Thực thi
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Lấy TripID, PaymentStatus, BookingStatus
                        int adultTripID = rs.getInt("TripID");
                        String paymentStatus = rs.getString("PaymentStatus");
                        String bookingStatus = rs.getString("BookingStatus");

                        // Kiểm tra PaymentStatus & BookingStatus
                        if (!"Paid".equalsIgnoreCase(paymentStatus)) {
                            return false;
                        }
                        if (!"Active".equalsIgnoreCase(bookingStatus)) {
                            return false;
                        }

                        // So sánh TripID vé người lớn với TripID vé trẻ em
                        int childTripID = childCartItem.getTrip().getTripID();
                        if (adultTripID != childTripID) {
                            return false;
                        }

                        // Qua tất cả check => hợp lệ
                        return true;
                    } else {
                        // Không tìm thấy TicketID tương ứng
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // adultTicketCode không phải số
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            // Lỗi DB
            e.printStackTrace();
            return false;
        }
    }

    public int insertTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO Ticket (PassengerName,passengerType,CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?,?)";

        // Tạo PreparedStatement có yêu cầu trả về khóa tự sinh
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ticket.getPassengerName());
            ps.setString(2, ticket.getPassengerType());
            ps.setString(3, ticket.getCccd());
            ps.setInt(4, ticket.getBookingID());
            ps.setInt(5, ticket.getSeatID());
            ps.setInt(6, ticket.getTripID());
            ps.setDouble(7, ticket.getTicketPrice());
            ps.setString(8, ticket.getTicketStatus());

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

    public boolean isCCCDRefundedInBooking(String cccd, int bookingID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Ticket WHERE CCCD = ? AND BookingID = ? AND TicketStatus = 'Refunded'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cccd);
            ps.setInt(2, bookingID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
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

    public void cancelTicket(int ticketID, int seatID) {
        // Cập nhật trạng thái vé
        String updateTicketSQL = "UPDATE Ticket SET TicketStatus = 'Refunded' WHERE TicketID = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateTicketSQL)) {
            ps.setInt(1, ticketID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }

        // Cập nhật trạng thái ghế
        String updateSeatSQL = "UPDATE Seat SET Status = 'Available' WHERE SeatID = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateSeatSQL)) {
            ps.setInt(1, seatID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }
    }

    public boolean isTicketActiveByCCCD(String cccd, int tripID) {
        String sql = "SELECT 1 "
                + "FROM Ticket t "
                + "JOIN Booking b ON t.BookingID = b.BookingID "
                + "WHERE t.CCCD = ? "
                + "  AND t.TripID = ? "
                + "  AND b.PaymentStatus = 'Paid' "
                + "  AND t.TicketStatus IN ('Used', 'Unused')"; // vé chưa refund

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, cccd);
            ps.setInt(2, tripID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Có vé hợp lệ => true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<RailwayDTO> getDetailedTicketsByUserID(int userID) {
        List<RailwayDTO> tickets = new ArrayList<>();
        String sql = "SELECT t.TicketID,t.PassengerName, t.CCCD,t.PassengerType, s.SeatNumber, c.CarriageNumber, "
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
                            rs.getString("PassengerName"),
                            rs.getString("CCCD"),
                            rs.getString("PassengerType"),
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
                            rs.getString("PassengerName"),
                            rs.getString("passengerType"),
                            rs.getString("CCCD"),
                            rs.getInt("BookingID"),
                            rs.getInt("SeatID"),
                            rs.getInt("TripID"),
                            rs.getInt("RefundID"),
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
