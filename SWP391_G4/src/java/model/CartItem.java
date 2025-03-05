/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class CartItem {

    private String ticketID;
    private String trainName;
    private String departureDate;
    private String carriageNumber;
    private String seatNumber;
    private String seatID;
    private double price;
    private Trip trip;
    private String departureStationName ; 
    private String arrivalStationName;
    private boolean returnTrip;

    public CartItem() {
    }

    public CartItem(String ticketID, String trainName, String departureDate, String carriageNumber, String seatNumber, String seatID, double price, Trip trip, String departureStationName, String arrivalStationName, boolean ReturnTrip) {
        this.ticketID = ticketID;
        this.trainName = trainName;
        this.departureDate = departureDate;
        this.carriageNumber = carriageNumber;
        this.seatNumber = seatNumber;
        this.seatID = seatID;
        this.price = price;
        this.trip = trip;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.returnTrip = ReturnTrip;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(String carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatID() {
        return seatID;
    }

    public void setSeatID(String seatID) {
        this.seatID = seatID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getDepartureStationName() {
        return departureStationName;
    }

    public void setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
    }

    public String getArrivalStationName() {
        return arrivalStationName;
    }

    public void setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
    }

    public boolean isReturnTrip() {
        return returnTrip;
    }

    public void setReturnTrip(boolean ReturnTrip) {
        this.returnTrip = ReturnTrip;
    }
    
    
}
