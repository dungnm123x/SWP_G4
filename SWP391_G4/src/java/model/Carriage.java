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
=======
package model;

public class Carriage {
    private int carriageID;
    private String carriageNumber;
    private String carriageType;
    private Train train;
    private int capacity;

    public Carriage() {
>>>>>>> main
    }

    // Getters and Setters
    public int getCarriageID() {
        return carriageID;
    }

    public void setCarriageID(int carriageID) {
        this.carriageID = carriageID;
    }

<<<<<<< HEAD
    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
=======
    public String getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(String carriageNumber) {
>>>>>>> main
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }

<<<<<<< HEAD
    public int getTrainID() {
        return trainID;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
=======
    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
>>>>>>> main
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
<<<<<<< HEAD

    @Override
    public String toString() {
        return "Carriage [carriageID=" + carriageID + ", carriageNumber=" + carriageNumber + ", carriageType=" + carriageType + ", trainID=" + trainID + ", capacity=" + capacity + "]";
    }
}

=======
}
>>>>>>> main
