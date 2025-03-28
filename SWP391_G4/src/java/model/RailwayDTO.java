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

    public int getTicketID() { return ticketID; }
    public String getPassengerName(){return passengerName;};
    public String getCccd() { return cccd; }
    public String getPassengerType(){return passengerType;};
    public String getRoute() { return route; }
    public String getTrainCode() { return trainCode; }
    public Timestamp getDepartureTime() { return departureTime; }
    public int getCarriageNumber() { return carriageNumber; }
    public int getSeatNumber() { return seatNumber; }
    public double getTicketPrice() { return ticketPrice; }
    public String getTicketStatus() { return ticketStatus; }
    public String getTripType() { return tripType; } // ✅ Thêm getter này
}