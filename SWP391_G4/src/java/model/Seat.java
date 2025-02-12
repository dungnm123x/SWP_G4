/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class Seat {
    private int seatID;
    private int seatNumber;
    private String status;
    private String seatType;
    private int carriageID;

    // Constructor
    public Seat(int seatID, int seatNumber, String status, String seatType, int carriageID) {
        this.seatID = seatID;
        this.seatNumber = seatNumber;
        this.status = status;
        this.seatType = seatType;
        this.carriageID = carriageID;
    }

    // Getters and Setters
    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getCarriageID() {
        return carriageID;
    }

    public void setCarriageID(int carriageID) {
        this.carriageID = carriageID;
    }

    @Override
    public String toString() {
        return "Seat [seatID=" + seatID + ", seatNumber=" + seatNumber + ", status=" + status + ", seatType=" + seatType + ", carriageID=" + carriageID + "]";
    }
}

