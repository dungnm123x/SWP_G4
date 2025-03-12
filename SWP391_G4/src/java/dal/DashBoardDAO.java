package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashBoardDAO extends DBContext<Object> {
    
    public int getTotalTicketsSold() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Tickets";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM Orders"; // Thay total_amount bằng tên cột doanh thu
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        }
        return 0.0;
    }

    public int getTotalOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Orders";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalEvents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Events";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public double getRevenueToday() throws SQLException {
        String sql = "SELECT SUM(TotalPrice) FROM Booking WHERE BookingDate >= CAST(GETDATE() AS DATE)";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                double revenue = rs.getDouble(1);
                System.out.println("Revenue today: " + revenue); // Thêm dòng này
                return revenue;
            }
        }
        System.out.println("Revenue today: 0"); // Thêm dòng này
        return 0;
    }

    public double getRevenueThisWeek() throws SQLException {
        String sql = "SELECT SUM(TotalPrice) FROM Booking WHERE BookingDate >= DATEADD(wk,DATEDIFF(wk,7,GETDATE()),0)";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double getRevenueThisMonth() throws SQLException {
        String sql = "SELECT SUM(TotalPrice) FROM Booking WHERE MONTH(BookingDate) = MONTH(GETDATE()) AND YEAR(BookingDate) = YEAR(GETDATE())";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double getRevenueThisYear() throws SQLException {
        String sql = "SELECT SUM(TotalPrice) FROM Booking WHERE YEAR(BookingDate) = YEAR(GETDATE())";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    private int getCount(String sql) throws SQLException {
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalUsers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User]");
    }

    public int getTotalEmployees() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User] WHERE RoleID = 2");
    }

    public int getTotalCustomers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User] WHERE RoleID = 3");
    }

    public int getTotalTrains() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Train");
    }

    public int getTotalBookings() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Booking");
    }

    public int getTotalTrips() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Trip");
    }

    public int getTotalBlogs() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Blog");
    }

    public int getTotalRules() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [Rule]");
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Insert operation is not supported.");
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Update operation is not supported.");
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Delete operation is not supported.");
    }

    @Override
    public ArrayList<Object> list() {
        throw new UnsupportedOperationException("List operation is not supported.");
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Get operation is not supported.");
    }
}
