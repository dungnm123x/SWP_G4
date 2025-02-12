<<<<<<< HEAD
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Trip {
    private int tripID;
    private int trainID;
    private int routeID;
    private String departureTime;
    private String arrivalTime;

    // Constructor
    public Trip(int tripID, int trainID, int routeID, String departureTime, String arrivalTime) {
        this.tripID = tripID;
        this.trainID = trainID;
        this.routeID = routeID;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
=======
package model;

import java.util.Date;

public class Trip {
    private int tripID;
    private Train train;
    private Route route;
    private Date departureTime;
    private Date arrivalTime;
    private String tripStatus;

    public Trip() {
>>>>>>> main
    }

    // Getters and Setters
    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

<<<<<<< HEAD
    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
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

    @Override
    public String toString() {
        return "Trip [tripID=" + tripID + ", trainID=" + trainID + ", routeID=" + routeID + ", departureTime=" + departureTime + ", arrivalTime=" + arrivalTime + "]";
    }
}

=======
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
}
>>>>>>> main
