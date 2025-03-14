package dal;

import dal.DBContext;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;

public class DAOAdmin extends DBContext {

    public boolean disableEmployee(int userId) {
        String query = "UPDATE [User] SET Status = 0 WHERE UserID = ? AND RoleID = 2"; // RoleID 2 for Employee
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disableCustomer(int userId) {
        String query = "UPDATE [User] SET Status = 0 WHERE UserID = ? AND RoleID = 3"; // RoleID 3 for Customer
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restoreEmployee(int userId) {
        String query = "UPDATE [User] SET Status = 1 WHERE UserID = ? AND RoleID = 2"; // RoleID 2 for Employee
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restoreCustomer(int userId) {
        String query = "UPDATE [User] SET Status = 1 WHERE UserID = ? AND RoleID = 3"; // RoleID 3 for Customer
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUser(User user) {
        String updateQuery = "UPDATE [User] SET FullName = ?, PhoneNumber = ?, Address = ? WHERE UserID = ?";
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

    public List<User> searchEmployees(String keyword) throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 2 AND (Username LIKE ? OR FullName LIKE ? OR Email LIKE ? OR PhoneNumber LIKE ?)"; // RoleID 2
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

    public List<User> searchCustomers(String keyword) throws SQLException {
        List<User> customers = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 3 AND (Username LIKE ? OR FullName LIKE ? OR Email LIKE ? OR PhoneNumber LIKE ?)"; // RoleID 3
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

    public List<User> getAllEmployees() throws SQLException {
        List<User> employees = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 2"; // RoleID 2
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                employees.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
            }
        }
        return employees;
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String query = "SELECT 1 FROM [User] WHERE Username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu username đã tồn tại
            }
        }
    }

    public boolean isEmailTaken(String email) throws SQLException {
        String query = "SELECT 1 FROM [User] WHERE Email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu email đã tồn tại
            }
        }
    }

    public boolean isPhoneTaken(String phone) throws SQLException {
        String query = "SELECT 1 FROM [User] WHERE PhoneNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu số điện thoại đã tồn tại
            }
        }
    }

    public boolean addEmployee(User user) throws SQLException {
        if (isUsernameTaken(user.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        if (isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }
        if (isPhoneTaken(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại!");
        }

        String insertQuery = "INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber, Address, RoleID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setInt(7, 2); // RoleID 2 for Employee
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> getAllCustomers() throws SQLException {
        List<User> customers = new ArrayList<>();
        String query = "SELECT * FROM [User] WHERE RoleID = 3"; // RoleID 3
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new User(rs.getInt("UserID"), rs.getString("Username"), rs.getString("Password"), rs.getString("FullName"), rs.getString("Email"), rs.getString("PhoneNumber"), rs.getString("Address"), rs.getInt("RoleID"), rs.getBoolean("Status")));
            }
        }
        return customers;
    }

    public User getEmployeeById(int userId) throws SQLException {
        User user = null;
        String query = "SELECT * FROM [User] WHERE UserID = ? AND RoleID = 2"; // RoleID 2
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

    public User getCustomerById(int userId) throws SQLException {
        User user = null;
        String query = "SELECT * FROM [User] WHERE UserID = ? AND RoleID = 3"; // RoleID 3
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
            query += " AND RoleID = 2";  // RoleID 2
        } else if ("customers".equals(type)) {
            query += " AND RoleID = 3"; // RoleID 3
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

    public boolean addFeedback(Feedback feedback) {
        String query = "INSERT INTO Feedback (UserID, Content, Rating) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, feedback.getUserId());
            ps.setString(2, feedback.getContent());
            ps.setInt(3, feedback.getRating());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.err.println("Feedback not added: No rows affected.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the full stack trace to the console
            System.err.println("SQL Exception: " + e.getMessage()); // Log the specific SQL error
            return false;
        }
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
