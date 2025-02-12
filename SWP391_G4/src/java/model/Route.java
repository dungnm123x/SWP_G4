/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Route {
    private int routeID;
    private int departureStationID;
    private int arrivalStationID;
    private int distance;
    private double basePrice;

    // Constructor
    public Route(int routeID, int departureStationID, int arrivalStationID, int distance, double basePrice) {
        this.routeID = routeID;
        this.departureStationID = departureStationID;
        this.arrivalStationID = arrivalStationID;
        this.distance = distance;
        this.basePrice = basePrice;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Route [routeID=" + routeID + ", departureStationID=" + departureStationID + ", arrivalStationID=" + arrivalStationID + ", distance=" + distance + ", basePrice=" + basePrice + "]";
    }
}

