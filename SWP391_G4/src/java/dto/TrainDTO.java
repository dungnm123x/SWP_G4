package dto;

import java.sql.*;
import java.time.LocalDateTime;

public class TrainDTO {

    private int trainID;
    private String trainName;
    private int totalCarriages;
    private int totalSeats;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;

    public TrainDTO() {
    }

    public TrainDTO(int trainID, String trainName, int totalCarriages, int totalSeats) {
        this.trainID = trainID;
        this.trainName = trainName;
        this.totalCarriages = totalCarriages;
        this.totalSeats = totalSeats;
    }

    public TrainDTO(int trainID, String trainName, int totalCarriages, int totalSeats, String departureStation, String arrivalStation, LocalDateTime departureTime, LocalDateTime arrivalTime, double price) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
