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
=======
package model;

public class Seat {
    private int seatID;
    private String seatNumber;
    private String status;
    private String seatType;
    private Carriage carriage;

    public Seat() {
>>>>>>> main
    }

    // Getters and Setters
    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

<<<<<<< HEAD
    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
=======
    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
>>>>>>> main
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

<<<<<<< HEAD
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

=======
    public Carriage getCarriage() {
        return carriage;
    }

    public void setCarriage(Carriage carriage) {
        this.carriage = carriage;
    }
}
>>>>>>> main
