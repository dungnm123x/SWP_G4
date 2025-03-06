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
import dto.RailwayDTO;

import model.Ticket;

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
