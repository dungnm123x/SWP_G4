/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.Date;
/**
 *
 * @author dung9
 */


public class Booking {
    private int bookingID;
    private int userID;
    private int tripID;
    private Integer roundTripTripID; // Chuyến về nếu có (nullable)
    private Date bookingDate;
    private double totalPrice;
    private String paymentStatus;
    private String bookingStatus;

    public Booking() {
    }

    // Constructor
    public Booking(int bookingID, int userID, int tripID, Integer roundTripTripID, Date bookingDate, double totalPrice, String paymentStatus, String bookingStatus) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.tripID = tripID;
        this.roundTripTripID = roundTripTripID;
        this.bookingDate = bookingDate;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
    }

    // Getters and Setters
    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public Integer getRoundTripTripID() {
        return roundTripTripID;
    }

    public void setRoundTripTripID(Integer roundTripTripID) {
        this.roundTripTripID = roundTripTripID;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public String toString() {
        return "Booking [bookingID=" + bookingID + ", userID=" + userID + ", tripID=" + tripID + 
               ", roundTripTripID=" + roundTripTripID + ", bookingDate=" + bookingDate + 
               ", totalPrice=" + totalPrice + ", paymentStatus=" + paymentStatus + 
               ", bookingStatus=" + bookingStatus + "]";
    }
}


