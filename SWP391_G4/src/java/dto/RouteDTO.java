package dto;

public class RouteDTO {
    private int routeID;
    private String departureStationName; // Store station *names* for easy display
    private String arrivalStationName;
    private int departureStationID; //keep ID
    private int arrivalStationID;//keep ID

    // Constructors
    public RouteDTO() {}

    public RouteDTO(int routeID, String departureStationName, String arrivalStationName) {
        this.routeID = routeID;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
    }
    public RouteDTO(int routeID, String departureStationName, String arrivalStationName, int departureStationID, int arrivalStationID) {
        this.routeID = routeID;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.departureStationID = departureStationID;
        this.arrivalStationID = arrivalStationID;
    }

    // Getters and Setters (generate these for all fields)

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
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

}