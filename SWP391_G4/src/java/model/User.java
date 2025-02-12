/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class User{
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
    }
}
