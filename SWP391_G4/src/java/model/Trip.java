package model;

import java.util.Date;

public class Trip {
    private int tripID;
    private Train train;
    private Route route;
    private Date departureTime;
    private Date arrivalTime;
    private String tripStatus;
    private String tripType;
    private Integer roundTripReference; // Chuyến về nếu có

    public Trip() {
    }

    // Getters and Setters
    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
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
}
