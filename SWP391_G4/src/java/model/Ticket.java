/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Ticket {
    private int ticketID;
    private int userID;
    private int seatID;
    private int tripID;
    private String ticketStatus;
    private String bookingDate;
    private double ticketPrice;

    public Ticket() {
    }

    // Constructor
    public Ticket(int ticketID, int userID, int seatID, int tripID, String ticketStatus, String bookingDate, double ticketPrice) {
        this.ticketID = ticketID;
        this.userID = userID;
        this.seatID = seatID;
        this.tripID = tripID;
        this.ticketStatus = ticketStatus;
        this.bookingDate = bookingDate;
        this.ticketPrice = ticketPrice;
    }

    // Getters and Setters
    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    @Override
    public String toString() {
        return "Ticket [ticketID=" + ticketID + ", userID=" + userID + ", seatID=" + seatID + ", tripID=" + tripID + ", ticketStatus=" + ticketStatus + ", bookingDate=" + bookingDate + ", ticketPrice=" + ticketPrice + "]";
    }
}
