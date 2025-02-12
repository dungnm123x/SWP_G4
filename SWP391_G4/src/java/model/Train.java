/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Train {
    private int trainID;
    private String trainName;

    // Constructor
    public Train(int trainID, String trainName) {
        this.trainID = trainID;
        this.trainName = trainName;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Train [trainID=" + trainID + ", trainName=" + trainName + "]";
    }
}

