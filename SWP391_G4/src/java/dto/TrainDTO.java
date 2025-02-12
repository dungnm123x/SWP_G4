package dto;
import java.sql.*;

public class TrainDTO {
     private int trainID;
    private String trainName;
    private int totalCarriages;
    private int totalSeats;
    private String departureStation;
    private String arrivalStation;
    private Date departureTime;
    private Date arrivalTime;
    private double price;

    public TrainDTO() {
    }

    public TrainDTO(int trainID, String trainName, int totalCarriages, int totalSeats, String departureStation, String arrivalStation, Date departureTime, Date arrivalTime, double price) {
        this.trainID = trainID;
        this.trainName = trainName;
        this.totalCarriages = totalCarriages;
        this.totalSeats = totalSeats;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public int getTotalCarriages() {
        return totalCarriages;
    }

    public void setTotalCarriages(int totalCarriages) {
        this.totalCarriages = totalCarriages;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    
}
