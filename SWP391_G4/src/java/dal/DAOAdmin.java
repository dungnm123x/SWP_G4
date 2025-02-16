package dal;

import dal.DBContext;
import model.User;
import model.Train;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOAdmin extends DBContext {

    //chỉnh sửa account 
    public boolean updateUser(User user) {
        String updateQuery = "UPDATE [User] SET FullName = ?, PhoneNumber = ?, Address = ? WHERE UserID = ?"; // Loại bỏ Username và Email
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhoneNumber());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm nhân viên theo Username, FullName, Email hoặc PhoneNumber
    public List<User> searchEmployees(String keyword) throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 3 AND (Username LIKE ? OR FullName LIKE ? OR Email LIKE ? OR PhoneNumber LIKE ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                            rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
                }
            }
        }
        return employees;
    }

// Tìm kiếm khách hàng tương tự
    public List<User> searchCustomers(String keyword) throws SQLException {
        List<User> customers = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 2 AND (Username LIKE ? OR FullName LIKE ? OR Email LIKE ? OR PhoneNumber LIKE ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                            rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
                }
            }
        }
        return customers;
    }

// Tìm kiếm tàu theo tên hoặc ID
    public List<Train> searchTrains(String keyword) throws SQLException {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM Train WHERE TrainID LIKE ? OR TrainName LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(new Train(rs.getInt("TrainID"), rs.getString("TrainName")));
                }
            }
        }
        return trains;
    }

    // View all employees
    public List<User> getAllEmployees() throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 3";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
            }
        }
        return employees;
    }
    // Thêm nhân viên mới

    // Thêm nhân viên mới
    public boolean addEmployee(User user) {

        String insertQuery = "INSERT INTO [User] ( Username, Password, FullName, Email, PhoneNumber, Address, RoleID) VALUES ( ?, ?, ?, ?, ?, ?, ?)";

        try {

            // Chèn dữ liệu nhân viên mới
            try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword()); // Cần mã hóa mật khẩu nếu cần bảo mật
                ps.setString(3, user.getFullName());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getPhoneNumber());
                ps.setString(6, user.getAddress());
                ps.setInt(7, user.getRoleID()); // RoleID = 3 là nhân viên

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Vô hiệu hóa nhân viên (cập nhật status = 0 thay vì xóa)
    public boolean disableEmployee(int userId) {
        String query = "UPDATE [User] SET Status = 0 WHERE UserID = ? AND RoleID = 3";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kích hoạt lại nhân viên
    public boolean restoreEmployee(int userId) {
        String query = "UPDATE [User] SET Status = 1 WHERE UserID = ? AND RoleID = 3";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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
                customers.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
            }
        }
        return customers;
    }

    // Tương tự cho khách hàng
    public boolean disableCustomer(int userId) {
        String query = "UPDATE [User] SET Status = 0 WHERE UserID = ? AND RoleID = 2";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restoreCustomer(int userId) {
        String query = "UPDATE [User] SET Status = 1 WHERE UserID = ? AND RoleID = 2";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
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
    // Lấy thông tin nhân viên theo userId

    public User getEmployeeById(int userId) throws SQLException {
        User user = null;
        String query = "SELECT * FROM [User] WHERE UserID = ? AND RoleID = 3";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                            rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"),
                            rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status"));
                }
            }
        }
        return user;
    }

// Lấy thông tin khách hàng theo userId
    public User getCustomerById(int userId) throws SQLException {
        User user = null;
        String query = "SELECT * FROM [User] WHERE UserID = ? AND RoleID = 2";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                            rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"),
                            rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status"));
                }
            }
        }
        return user;
    }

    public User getUserById(int userId, String type) throws SQLException {
        String query = "SELECT * FROM [User] WHERE UserID = ?";
        if ("employees".equals(type)) {
            query += " AND RoleID = 3";
        } else if ("customers".equals(type)) {
            query += " AND RoleID = 2";
        }
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"),
                            rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status"));
                }
            }
        }
        return null; // Return null if the user is not found
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
