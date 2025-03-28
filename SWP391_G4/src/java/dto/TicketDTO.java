package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TicketDTO {

    private int ticketID;
    private String passengerName;
    private String cccd; // Or whatever ID you use
    private String passengerType;
    private int bookingID;
    private int seatID;
    private int tripID;
    private double ticketPrice;
    private String ticketStatus;
    // Add fields to store related data for *display* purposes:
    private String seatNumber; // From Seat table
    private String carriageNumber; // From Carriage table
    private String carriageType;
    private String trainName;
    private String routeName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public TicketDTO(int ticketID, String passengerName, String cccd,String passengerType, int bookingID, int seatID, int tripID, double ticketPrice, String ticketStatus, String seatNumber, String carriageNumber, String carriageType, String trainName, String routeName, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.ticketID = ticketID;
        this.passengerName = passengerName;
        this.cccd = cccd;
        this.passengerType=passengerType;
        this.bookingID = bookingID;
        this.seatID = seatID;
        this.tripID = tripID;
        this.ticketPrice = ticketPrice;
        this.ticketStatus = ticketStatus;
        this.seatNumber = seatNumber;
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.trainName = trainName;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public TicketDTO() {
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
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

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(String carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFormattedDepartureTime() {
        if (departureTime == null) {
            return "";
        }
        return departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedArrivalTime() {
        if (arrivalTime == null) {
            return "";
        }
        return arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
