/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author dung9
 */
public class Encryptor {
    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_16LE)); // Use UTF-16LE to match SQL Server

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase(); // Match SQL Server uppercase hex format
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }


    public static void main(String[] args) {
        // Test với mật khẩu
        String password = "admin123";  // Thử với mật khẩu của SQL
        String encryptedPassword = encryptPassword(password);
        System.out.println("Original Password: " + password);
        System.out.println("Encrypted Password: " + encryptedPassword);  // Kết quả sẽ giống với SQL
    }
}
