/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.User;

/**
 *
 * @author dung9
 */
public class UserDAO extends DBContext{

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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
        private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("UserID"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("FullName"),
                rs.getString("Email"),
                rs.getString("PhoneNumber"),
                rs.getString("address"),
                rs.getInt("RoleID"),
                rs.getBoolean("Status")
        );
    }

    public User checkUserLogin(String username, String password) {
        String sql = "SELECT * FROM [User] WHERE Username=? AND Password=?";
        try (Connection conn = connection; PreparedStatement stm = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("DEBUG: Database connection failed!");
                return null;
            }

            System.out.println("DEBUG: Kiểm tra đăng nhập với Username = " + username);
            System.out.println("DEBUG: Password nhập vào = " + password);

            stm.setString(1, username);
            stm.setString(2, password);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: Đăng nhập thành công!");
                    return mapUser(rs);
                } else {
                    System.out.println("DEBUG: Không tìm thấy tài khoản phù hợp!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("DEBUG: Lỗi khi kiểm tra đăng nhập!");
        }
        return null;
    }

    public User checkAccountExist(String username) {
        String sql = "SELECT UserID, Username, Password, FullName, Email, PhoneNumber, Address ,RoleID FROM [User] WHERE Username=?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("RoleID"),
                        rs.getBoolean("Status")

                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để debug
        }
        return null;
    }

    public void AddAccount(User newUser) {
        String sql = "INSERT INTO [dbo].[User] (Username, Password, FullName, Email, PhoneNumber, RoleID, Address) VALUES (?, ?, ?, ?, ?, 3, ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String fullName = (newUser.getFullName() != null) ? newUser.getFullName() : "";

            st.setString(1, newUser.getUsername());
            st.setString(2, newUser.getPassword());
            st.setString(3, fullName);
            st.setString(4, newUser.getEmail());
            st.setString(5, newUser.getPhoneNumber());
            st.setString(6, newUser.getAddress());

            st.executeUpdate();
            System.out.println("Tạo tài khoản thành công!");
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để debug
        }
    }

    public boolean registerUser(User newUser) {
        String sql = "INSERT INTO [User] (Username, Password, FullName, Email, PhoneNumber, RoleID) VALUES (?, ?, ?, ?, ?, 2)";
        try (Connection conn = connection; PreparedStatement stm = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("DEBUG: Database connection failed!");
                return false;
            }

            stm.setString(1, newUser.getUsername());
            stm.setString(2, newUser.getPassword()); // Không mã hóa mật khẩu
            stm.setString(3, newUser.getFullName());
            stm.setString(4, newUser.getEmail());
            stm.setString(5, newUser.getPhoneNumber());

            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update(int userId, String fullName, String address, String phone) {
        String sql = "UPDATE [User] SET FullName = ?, Address = ?, PhoneNumber = ? WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, fullName);
            st.setString(2, address);
            st.setString(3, phone);
            st.setInt(4, userId);
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM [User] WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("RoleID"),
                        rs.getBoolean("Status")

                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkphone(String phone) {
String sql = "SELECT UserID, Username, Password, FullName, Email, PhoneNumber, Address ,RoleID FROM [User] WHERE PhoneNumber=?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, phone);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để debug
        }
        return false;    
    }

    public boolean checkemail(String email) {
String sql = "SELECT UserID, Username, Password, FullName, Email, PhoneNumber, Address ,RoleID FROM [User] WHERE Email=?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi để debug
        }
        return false;        }

    public User getUserByUsername(String username) {
                String sql = "SELECT * FROM [User] WHERE Username = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("FullName"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("RoleID"),
                        rs.getBoolean("Status")

                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}