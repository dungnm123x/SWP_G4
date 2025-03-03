/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.util.ArrayList;
import model.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CartDAO extends DBContext {

    public int getTicketID(int seatID) {
        if (seatID <= 0) {
            System.out.println("❌ Lỗi: SeatID không hợp lệ!");
            return -1;
        }

        int ticketID = -1; // Mặc định không tìm thấy
        String query = "SELECT TOP 1 TicketID FROM Ticket WHERE SeatID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, seatID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ticketID = rs.getInt("TicketID");
                System.out.println("✅ TicketID tìm thấy: " + ticketID);
            } else {
                System.out.println("⚠️ Không tìm thấy TicketID cho SeatID: " + seatID);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi SQL trong getTicketID: " + e.getMessage());
        }
        return ticketID;
    }

    // Tạo `ticketID` mới nếu không có sẵn
    public int generateTicketID(int seatID, int bookingID, int tripID, double price) {
        int ticketID = -1;
        String insertQuery = "INSERT INTO Ticket (CCCD, BookingID, SeatID, TripID, TicketPrice, TicketStatus) VALUES (?, ?, ?, ?, ?, 'Unused')";

        try (
                PreparedStatement ps = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "000000000000"); // CCCD giả lập
            ps.setInt(2, bookingID);
            ps.setInt(3, seatID);
            ps.setInt(4, tripID);
            ps.setDouble(5, price);
            ps.executeUpdate();

            // Lấy `ticketID` vừa tạo
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticketID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketID;
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
    public static void main(String[] args) {
        CartDAO d = new CartDAO();
        System.out.println(d.getTicketID(1));
    }

}
