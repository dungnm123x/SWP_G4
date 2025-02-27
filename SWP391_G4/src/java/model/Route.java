
package model;
public class Route {
    private int routeID;
    private Station departureStationID;
    private Station arrivalStationID;
    private double distance;
    private double basePrice;

    public Route() {
    }

    public Route(int routeID, Station departureStation, Station arrivalStation, double distance, double basePrice) {
        this.routeID = routeID;
        this.departureStationID = departureStation;
        this.arrivalStationID = arrivalStation;
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

    public Station getDepartureStation() {
        return departureStationID;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStationID = departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStationID;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStationID = arrivalStation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
