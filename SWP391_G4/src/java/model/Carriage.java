package model;

public class Carriage {

    private int carriageID;
    private String carriageNumber;
    private String carriageType;
    private Train train;
    private int capacity;

    public Carriage() {
    }

    public Carriage(String carriageNumber, String carriageType, Train train, int capacity) {
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.train = train;
        this.capacity = capacity;
    }

    public Carriage(int carriageID, String carriageNumber, String carriageType, int capacity) {
        this.carriageID = carriageID;
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.capacity = capacity;
    }

    public Carriage(int carriageID, String carriageNumber, String carriageType, Train train, int capacity) {
        this.carriageID = carriageID;
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.train = train;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getCarriageID() {
        return carriageID;
    }

    public void setCarriageID(int carriageID) {
        this.carriageID = carriageID;
    }

    public String getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(String carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public String getCarriageType() {
        return carriageType;
    }

    public void setCarriageType(String carriageType) {
        this.carriageType = carriageType;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
