package dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RefundDTO {
    private int refundID;
    private String bankAccountID;
    private String bankName;
    private Timestamp refundDate;
    private String refundStatus;
    private BigDecimal totalRefund;

    // Thông tin khách hàng (User)
    private int userID;
    private String customerName;
    private String customerEmail;
    private String phoneNumber;
    private String address;

    // Thông tin vé (Ticket & Trip)
    private int ticketID;
    private String cccd;
    private String departureStation;
    private String arrivalStation;
    private String trainName;
    private Timestamp departureTime;
    private int carriageNumber;
    private int seatNumber;
    private double ticketPrice;
    private String ticketStatus;
    private String tripType;

    public RefundDTO(int refundID, String bankAccountID, String bankName, Timestamp refundDate, String refundStatus, 
                     BigDecimal totalRefund, int userID, String customerName, String customerEmail, String phoneNumber, 
                     String address, int ticketID, String cccd, String departureStation, String arrivalStation, 
                     String trainName, Timestamp departureTime, int carriageNumber, int seatNumber, 
                     double ticketPrice, String ticketStatus, String tripType) {
        this.refundID = refundID;
        this.bankAccountID = bankAccountID;
        this.bankName = bankName;
        this.refundDate = refundDate;
        this.refundStatus = refundStatus;
        this.totalRefund = totalRefund;
        this.userID = userID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.ticketID = ticketID;
        this.cccd = cccd;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.trainName = trainName;
        this.departureTime = departureTime;
        this.carriageNumber = carriageNumber;
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.ticketStatus = ticketStatus;
        this.tripType = tripType;
    }

    // Getters
    public int getRefundID() { return refundID; }
    public String getBankAccountID() { return bankAccountID; }
    public String getBankName() { return bankName; }
    public Timestamp getRefundDate() { return refundDate; }
    public String getRefundStatus() { return refundStatus; }
    public BigDecimal getTotalRefund() { return totalRefund; }
    public int getUserID() { return userID; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public int getTicketID() { return ticketID; }
    public String getCccd() { return cccd; }
    public String getDepartureStation() { return departureStation; }
    public String getArrivalStation() { return arrivalStation; }
    public String getTrainName() { return trainName; }
    public Timestamp getDepartureTime() { return departureTime; }
    public int getCarriageNumber() { return carriageNumber; }
    public int getSeatNumber() { return seatNumber; }
    public double getTicketPrice() { return ticketPrice; }
    public String getTicketStatus() { return ticketStatus; }
    public String getTripType() { return tripType; }
}
