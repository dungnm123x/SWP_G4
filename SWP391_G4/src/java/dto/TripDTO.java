package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TripDTO {
    private int tripID;
    private int trainID;
    private String trainName; // Store train name for display
    private int routeID;
    private String routeName; // Store route name (departure - arrival)
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String tripStatus; // e.g., "Scheduled," "Departed," "Arrived," "Cancelled"

    // Constructors
    public TripDTO() {}

    public TripDTO(int tripID, int trainID, String trainName, int routeID, String routeName,
                   LocalDateTime departureTime, LocalDateTime arrivalTime, String tripStatus) {
        this.tripID = tripID;
        this.trainID = trainID;
        this.trainName = trainName;
        this.routeID = routeID;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.tripStatus = tripStatus;
    }

    // Getters and Setters (generate these for all fields)
     public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

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

    public String getTripStatus() {
        return tripStatus;
    }
    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
}

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
    // Add formatted date/time getters (for JSP)
    public String getFormattedDepartureTime() {
        if (departureTime == null) return "";
        return departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedArrivalTime() {
        if (arrivalTime == null) return "";
        return arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    //akshjkdsd
}