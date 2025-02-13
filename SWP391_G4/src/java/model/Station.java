package model;

public class Station {

    private int stationID;
    private String stationName;
    private String address;

    public Station() {
    }

    public Station(int stationID, String stationName, String address) {
        this.stationID = stationID;
        this.stationName = stationName;
        this.address = address;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
