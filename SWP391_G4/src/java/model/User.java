/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class User {
<<<<<<< HEAD
     private int UserId;
    private String Username;
    private String Password;
    private String FullName;
    private String Email;
    private String PhoneNumber;
    private int RoleID;
    public User() {
    }

    public User(int UserId, String Username, String Password, String FullName, String Email, String PhoneNumber,int RoleID) {
        this.UserId = UserId;
        this.Username = Username;
        this.Password = Password;
        this.FullName = FullName;
        this.Email = Email;
        this.PhoneNumber = PhoneNumber;
        this.RoleID = RoleID;
    }

    public int getUserId() {
        return UserId;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getFullName() {
        return FullName;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public int getRoleID() {
        return RoleID;
    }
    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public void setRoleID(int RoleID) {
        this.RoleID = RoleID;
=======
    private int userID;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private int roleID;

    public User() {
    }

    // Constructor
    public User(int userID, String username, String password, String fullName, String email, String phoneNumber, int roleID) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roleID = roleID;
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", username=" + username + ", password=" + password + ", fullName=" + fullName + ", email=" + email + ", phoneNumber=" + phoneNumber + ", roleID=" + roleID + "]";
>>>>>>> main
    }
}
