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
import dto.RefundDTO;
import dto.TicketDTO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.Statement;
import model.Refund;

/**
 *
 * @author dung9
 */
public class RefundDAO extends DBContext {

    public int insertRefund(int userID, String bankAccountID, String bankName, double totalRefund) throws SQLException {
        String sql = "INSERT INTO Refund (UserID, BankAccountID, BankName, RefundDate, TotalRefund, RefundStatus) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'Wait')";

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userID);
            ps.setString(2, bankAccountID);
            ps.setString(3, bankName);
            ps.setDouble(4, totalRefund);

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

    public boolean updateTicketsWithRefundID(int refundID, List<Integer> ticketIDs) throws SQLException {
        String sql = "UPDATE Ticket SET RefundID = ? WHERE TicketID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int ticketID : ticketIDs) {
                ps.setInt(1, refundID);
                ps.setInt(2, ticketID);
                ps.addBatch();
            }
            int[] affectedRows = ps.executeBatch();
            return affectedRows.length == ticketIDs.size();
        }
    }

    public List<Refund> getAllRefunds() throws SQLException {
        List<Refund> refunds = new ArrayList<>();
        String sql = "SELECT * FROM Refund";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                refunds.add(new Refund(
                        rs.getInt("RefundID"),
                        rs.getInt("UserID"),
                        rs.getString("BankAccountID"),
                        rs.getString("BankName"),
                        rs.getTimestamp("RefundDate"),
                        rs.getString("RefundStatus"),
                        rs.getBigDecimal("TotalRefund")
                ));
            }
        }
        return refunds;
    }

    public List<Refund> getRefundsByStatus(String status) throws SQLException {
        List<Refund> refunds = new ArrayList<>();
        String sql = "SELECT * FROM Refund WHERE RefundStatus = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    refunds.add(new Refund(
                            rs.getInt("RefundID"),
                            rs.getInt("UserID"),
                            rs.getString("BankAccountID"),
                            rs.getString("BankName"),
                            rs.getTimestamp("RefundDate"),
                            rs.getString("RefundStatus"),
                            rs.getBigDecimal("TotalRefund")
                    ));
                }
            }
        }
        return refunds;
    }

    public List<RefundDTO> getAllRefundDetails(String bankAccountID, String refundDate, String refundStatus,
            String customerName, String phoneNumber, String customerEmail) throws SQLException {
        List<RefundDTO> refundList = new ArrayList<>();
        String sql = "SELECT r.RefundID, r.BankAccountID, r.BankName, r.RefundDate, r.RefundStatus, r.TotalRefund, "
                + "u.UserID, u.FullName AS customerName, u.Email AS customerEmail, u.PhoneNumber, "
                + "t.TicketID, t.CCCD, "
                + "tr.TrainName AS TrainName, "
                + "sd.StationName AS DepartureStation, sa.StationName AS ArrivalStation, "
                + "tp.DepartureTime, c.CarriageNumber, s.SeatNumber, tp.TripType "
                + "FROM Refund r "
                + "JOIN [User] u ON r.UserID = u.UserID "
                + "JOIN Ticket t ON t.RefundID = r.RefundID "
                + "JOIN Seat s ON t.SeatID = s.SeatID "
                + "JOIN Carriage c ON s.CarriageID = c.CarriageID "
                + "JOIN Trip tp ON t.TripID = tp.TripID "
                + "JOIN Train tr ON tp.TrainID = tr.TrainID "
                + "JOIN Route rt ON tp.RouteID = rt.RouteID "
                + "JOIN Station sd ON rt.DepartureStationID = sd.StationID "
                + "JOIN Station sa ON rt.ArrivalStationID = sa.StationID "
                + "WHERE 1=1";  // Điều kiện mặc định

        List<Object> parameters = new ArrayList<>();

        // Lọc theo số tài khoản ngân hàng
        if (bankAccountID != null && !bankAccountID.trim().isEmpty()) {
            sql += " AND r.BankAccountID = ?";
            parameters.add(bankAccountID);
        }

        // Lọc theo ngày hoàn tiền
        if (refundDate != null && !refundDate.trim().isEmpty()) {
            sql += " AND CONVERT(DATE, r.RefundDate) = ?";
            parameters.add(refundDate);
        }

        // Lọc theo trạng thái hoàn tiền
        if (refundStatus != null && !refundStatus.trim().isEmpty()) {
            sql += " AND r.RefundStatus = ?";
            parameters.add(refundStatus);
        }

        // Lọc theo tên khách hàng
        if (customerName != null && !customerName.trim().isEmpty()) {
            sql += " AND u.FullName LIKE ?";
            parameters.add("%" + customerName + "%");
        }

        // Lọc theo số điện thoại
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            sql += " AND u.PhoneNumber LIKE ?";
            parameters.add("%" + phoneNumber + "%");
        }

        // Lọc theo email
        if (customerEmail != null && !customerEmail.trim().isEmpty()) {
            sql += " AND u.Email LIKE ?";
            parameters.add("%" + customerEmail + "%");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String route = rs.getString("DepartureStation") + " → " + rs.getString("ArrivalStation");

                refundList.add(new RefundDTO(
                        rs.getInt("RefundID"),
                        rs.getString("BankAccountID"),
                        rs.getString("BankName"),
                        rs.getTimestamp("RefundDate"),
                        rs.getString("RefundStatus"),
                        rs.getBigDecimal("TotalRefund"),
                        rs.getInt("UserID"),
                        rs.getString("customerName"),
                        rs.getString("customerEmail"),
                        rs.getString("PhoneNumber"),
                        rs.getInt("TicketID"),
                        rs.getString("CCCD"),
                        rs.getString("TrainName"),
                        route, // ✅ Tuyến đường
                        rs.getTimestamp("DepartureTime"),
                        rs.getInt("CarriageNumber"),
                        rs.getInt("SeatNumber"),
                        rs.getString("TripType")
                ));
            }
        }
        return refundList;
    }

    public RefundDTO getRefundDetailsByID(int refundID) throws SQLException {
        String sql = "SELECT r.RefundID, r.BankAccountID, r.BankName, r.RefundDate, r.RefundStatus, r.TotalRefund, "
                + "u.UserID, u.FullName AS customerName, u.Email AS customerEmail, u.PhoneNumber, "
                + "t.TicketID, t.CCCD, tr.TrainName, sd.StationName AS DepartureStation, "
                + "sa.StationName AS ArrivalStation, tp.DepartureTime, c.CarriageNumber, "
                + "s.SeatNumber, tp.TripType, t.TicketPrice "
                + "FROM Refund r "
                + "JOIN [User] u ON r.UserID = u.UserID "
                + "JOIN Ticket t ON t.RefundID = r.RefundID "
                + "JOIN Seat s ON t.SeatID = s.SeatID "
                + "JOIN Carriage c ON s.CarriageID = c.CarriageID "
                + "JOIN Trip tp ON t.TripID = tp.TripID "
                + "JOIN Train tr ON tp.TrainID = tr.TrainID "
                + "JOIN Route rt ON tp.RouteID = rt.RouteID "
                + "JOIN Station sd ON rt.DepartureStationID = sd.StationID "
                + "JOIN Station sa ON rt.ArrivalStationID = sa.StationID "
                + "WHERE r.RefundID = ?";

        RefundDTO refund = null;
        List<TicketDTO> tickets = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, refundID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (refund == null) {
                        refund = new RefundDTO(
                                rs.getInt("RefundID"),
                                rs.getString("BankAccountID"),
                                rs.getString("BankName"),
                                rs.getTimestamp("RefundDate"),
                                rs.getString("RefundStatus"),
                                rs.getBigDecimal("TotalRefund"),
                                rs.getInt("UserID"),
                                rs.getString("customerName"),
                                rs.getString("customerEmail"),
                                rs.getString("PhoneNumber"),
                                0, "", "", "", null, 0, 0, "" // Giá trị mặc định cho vé
                        );
                    }

                    TicketDTO ticket = new TicketDTO(
                            rs.getInt("TicketID"),
                            rs.getString("customerName"),
                            rs.getString("CCCD"),
                            0, 0, 0,
                            rs.getDouble("TicketPrice"),
                            "Refunded",
                            rs.getString("SeatNumber"),
                            rs.getString("CarriageNumber"),
                            "Standard",
                            rs.getString("TrainName"),
                            rs.getString("DepartureStation") + " → " + rs.getString("ArrivalStation"),
                            rs.getTimestamp("DepartureTime").toLocalDateTime(),
                            null
                    );

                    tickets.add(ticket);
                }
            }
        }

        if (refund != null) {
            refund.setTickets(tickets);
        }
        return refund;
    }

    public boolean updateRefundStatus(int refundID, String newStatus) {
        String sql = "UPDATE Refund SET RefundStatus = ? WHERE RefundID = ? AND RefundStatus = 'Wait'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, refundID);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
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
