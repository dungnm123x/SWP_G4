package dal;

import dal.DBContext;
import model.User;
import model.Train;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOAdmin extends DBContext {

    // View all employees
    public List<User> getAllEmployees() throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 3";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new User(rs.getInt("UserID"), rs.getString("Username"),rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"),rs.getString("Addresss"),rs.getInt("RoleID")));
            }
        }
        return employees;
    }

    // Delete an employee
    public boolean deleteEmployee(int userId) {
        try {
            // Xóa tất cả booking liên quan trước
            String sql1 = "DELETE FROM Booking WHERE UserID = ?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setInt(1, userId);
            ps1.executeUpdate();

            // Xóa nhân viên sau khi đã xóa booking
            String sql2 = "DELETE FROM Users WHERE UserID = ?";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, userId);
            return ps2.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // View all customers
    public List<User> getAllCustomers() throws SQLException {
        List<User> customers = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 2";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new User(rs.getInt("UserID"), rs.getString("Username"),rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"),rs.getString("Address"),rs.getInt("RoleID")));
            }
        }
        return customers;
    }

    // Delete a customer
    public boolean deleteCustomer(int userId) {
        try {
            // Xóa tất cả booking liên quan trước
            String sql1 = "DELETE FROM Booking WHERE UserID = ?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setInt(1, userId);
            ps1.executeUpdate();

            // Xóa khách hàng sau khi đã xóa booking
            String sql2 = "DELETE FROM Users WHERE UserID = ?";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, userId);
            return ps2.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // View all trains
    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM Train";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trains.add(new Train(rs.getInt("TrainID"), rs.getString("TrainName")));
            }
        }
        return trains;
    }

    // Delete a train
    public boolean deleteTrain(int trainId) {
        try {
            // Xóa tất cả các toa thuộc train trước
            String sql1 = "DELETE FROM Carriage WHERE TrainID = ?";
            PreparedStatement ps1 = connection.prepareStatement(sql1);
            ps1.setInt(1, trainId);
            ps1.executeUpdate();

            // Xóa chuyến tàu sau khi đã xóa tất cả toa
            String sql2 = "DELETE FROM Train WHERE TrainID = ?";
            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, trainId);
            return ps2.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
