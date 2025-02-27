/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;
import java.util.List;
/**
 *
 * @author Admin
 */

public class RailwayDTO {
    private int carriageID;
    private int carriageNumber;
    private String carriageType;
    private int trainID;
    private int capacity;
    
    private int seatID;
    private int seatNumber;
    private String seatStatus;
    private String seatType;
    
    private int stationID;
    private String stationName;
    private String Address;
    
    private int routeID;
    private int departureStationID;
    private int arrivalStationID;
    private int distance;
    private double basePrice;
    
    private int ticketID;
    private String cccd;
    private String ticketStatus;
    private double ticketPrice;

    private int tripID;
    private String departureTime;
    private String arrivalTime;
    private String tripStatus;
    private String tripType;
    private Integer roundTripReference; // Tham chiếu chuyến về nếu có

    private int bookingID;
    private int userID;
    private int tripIDBooking;
    private Integer roundTripTripID; // Vé khứ hồi nếu có

    public RailwayDTO() {
    }

    public RailwayDTO(int carriageID, int carriageNumber, String carriageType, int trainID, int capacity, int seatID, int seatNumber, String seatStatus, String seatType, int stationID, String stationName, String Address, int routeID, int departureStationID, int arrivalStationID, int distance, double basePrice, int ticketID, String cccd, String ticketStatus, double ticketPrice, int tripID, String departureTime, String arrivalTime, String tripStatus, String tripType, Integer roundTripReference, int bookingID, int userID, int tripIDBooking, Integer roundTripTripID) {
        this.carriageID = carriageID;
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.trainID = trainID;
        this.capacity = capacity;
        this.seatID = seatID;
        this.seatNumber = seatNumber;
        this.seatStatus = seatStatus;
        this.seatType = seatType;
        this.stationID = stationID;
        this.stationName = stationName;
        this.Address = Address;
        this.routeID = routeID;
        this.departureStationID = departureStationID;
        this.arrivalStationID = arrivalStationID;
        this.distance = distance;
        this.basePrice = basePrice;
        this.ticketID = ticketID;
        this.cccd = cccd;
        this.ticketStatus = ticketStatus;
        this.ticketPrice = ticketPrice;
        this.tripID = tripID;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.tripStatus = tripStatus;
        this.tripType = tripType;
        this.roundTripReference = roundTripReference;
        this.bookingID = bookingID;
        this.userID = userID;
        this.tripIDBooking = tripIDBooking;
        this.roundTripTripID = roundTripTripID;
    }

    public int getCarriageID() {
        return carriageID;
    }

    public void setCarriageID(int carriageID) {
        this.carriageID = carriageID;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }

    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus) {
        this.seatStatus = seatStatus;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public int getDepartureStationID() {
        return departureStationID;
    }

    public void setDepartureStationID(int departureStationID) {
        this.departureStationID = departureStationID;
    }

    public int getArrivalStationID() {
        return arrivalStationID;
    }

    public void setArrivalStationID(int arrivalStationID) {
        this.arrivalStationID = arrivalStationID;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
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

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public Integer getRoundTripReference() {
        return roundTripReference;
    }

    public void setRoundTripReference(Integer roundTripReference) {
        this.roundTripReference = roundTripReference;
    }

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

    public int getTripIDBooking() {
        return tripIDBooking;
    }

    public void setTripIDBooking(int tripIDBooking) {
        this.tripIDBooking = tripIDBooking;
    }

    public Integer getRoundTripTripID() {
        return roundTripTripID;
    }

    public void setRoundTripTripID(Integer roundTripTripID) {
        this.roundTripTripID = roundTripTripID;
    }

   
    
    public int getStatus() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

