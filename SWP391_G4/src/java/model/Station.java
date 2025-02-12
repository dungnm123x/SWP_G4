/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Station {
    private int stationID;
    private String stationName;
    private String address;

    // Constructor
    public Station(int stationID, String stationName, String address) {
        this.stationID = stationID;
        this.stationName = stationName;
        this.address = address;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Station [stationID=" + stationID + ", stationName=" + stationName + ", address=" + address + "]";
    }
}
