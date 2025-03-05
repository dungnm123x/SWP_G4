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
//    private int ticketID;
//    private int userID;
//    private int seatID;
//    private int tripID;
//    private String ticketStatus;
//    private String bookingDate;
//    private double ticketPrice;

    private int ticketID; // auto
    private String cccd;  // CMND/Hộ chiếu
    private int bookingID;
    private int seatID;
    private int tripID;
    private double ticketPrice;
    private String ticketStatus;

    // Constructor

    public Ticket() {
    }

    public Ticket(int ticketID, String cccd, int bookingID, int seatID, int tripID, double ticketPrice, String ticketStatus) {
        this.ticketID = ticketID;
        this.cccd = cccd;
        this.bookingID = bookingID;
        this.seatID = seatID;
        this.tripID = tripID;
        this.ticketPrice = ticketPrice;
        this.ticketStatus = ticketStatus;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
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

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
    
}
