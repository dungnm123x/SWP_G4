package dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

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

    // Thông tin vé & chuyến đi
    private int ticketID;
    private String cccd;
    private String trainName;
    private String routeName;
    private Timestamp departureTime;
    private int carriageNumber;
    private int seatNumber;
    private String tripType;

    public RefundDTO(int refundID, String bankAccountID, String bankName, Timestamp refundDate, String refundStatus,
            BigDecimal totalRefund, int userID, String customerName, String customerEmail, String phoneNumber,
            int ticketID, String cccd, String trainName, String routeName, Timestamp departureTime,
            int carriageNumber, int seatNumber, String tripType) {
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
        this.ticketID = ticketID;
        this.cccd = cccd;
        this.trainName = trainName;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.carriageNumber = carriageNumber;
        this.seatNumber = seatNumber;
        this.tripType = tripType;
    }

    // Getters & Setters...
    public int getRefundID() {
        return refundID;
    }

    public void setRefundID(int refundID) {
        this.refundID = refundID;
    }

    public String getBankAccountID() {
        return bankAccountID;
    }

    public void setBankAccountID(String bankAccountID) {
        this.bankAccountID = bankAccountID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Timestamp getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Timestamp refundDate) {
        this.refundDate = refundDate;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public BigDecimal getTotalRefund() {
        return totalRefund;
    }

    public void setTotalRefund(BigDecimal totalRefund) {
        this.totalRefund = totalRefund;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public int getCarriageNumber() {
        return carriageNumber;
    }

    public void setCarriageNumber(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    private List<TicketDTO> tickets;

    public List<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }
}
