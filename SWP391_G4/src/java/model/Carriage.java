/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Carriage {
    private int carriageID;
    private int carriageNumber;
    private String carriageType;
    private int trainID;
    private int capacity;

    // Constructor
    public Carriage(int carriageID, int carriageNumber, String carriageType, int trainID, int capacity) {
        this.carriageID = carriageID;
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.trainID = trainID;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getCarriageID() {
        return carriageID;
    }

    public void setCarriageID(int carriageID) {
        this.carriageID = carriageID;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }

    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Carriage [carriageID=" + carriageID + ", carriageNumber=" + carriageNumber + ", carriageType=" + carriageType + ", trainID=" + trainID + ", capacity=" + capacity + "]";
    }
}

