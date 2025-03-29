package model;

import java.sql.Timestamp;

public class RailwayDTO {
    private int ticketID;
    private String cccd;
    private String passengerType;
    private String route;
    private String trainCode;
    private Timestamp departureTime;
    private int carriageNumber;
    private int seatNumber;
    private double ticketPrice;
    private String ticketStatus;
    private String tripType; // ✅ Thêm trường này
    private String passengerName;

    public RailwayDTO(int ticketID,String passengerName, String cccd,String passengerType, String route, String trainCode, Timestamp departureTime,
                      int carriageNumber, int seatNumber, double ticketPrice, String ticketStatus, String tripType) {
        this.ticketID = ticketID;
        this.passengerName=passengerName;
        this.cccd = cccd;
        this.passengerType=passengerType;
        this.route = route;
        this.trainCode = trainCode;
        this.departureTime = departureTime;
        this.carriageNumber = carriageNumber;
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.ticketStatus = ticketStatus;
        this.tripType = tripType;
    }
    public RailwayDTO() {
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

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
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

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

   
}