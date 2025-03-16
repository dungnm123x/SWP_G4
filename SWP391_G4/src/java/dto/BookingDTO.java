package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingDTO {
    private int bookingID;
    private int userID;
    private int tripID;
    private Integer roundTripTripID; // Use Integer, as it can be null
    private LocalDateTime bookingDate;
    private double totalPrice;
    private String paymentStatus;
    private String bookingStatus;
    private TripDTO trip;
    // Add fields to store related object data *for display purposes*:
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String trainName; // From Train table
    private String routeName; // e.g., "Hanoi - Saigon"
    private LocalDateTime departureTime; // From Trip table
    private LocalDateTime arrivalTime;
     // Add this field to store ticket information
    private List<TicketDTO> tickets; // VERY important for the details view

    public BookingDTO() {
    }

    public BookingDTO(int bookingID, int userID, int tripID, Integer roundTripTripID, LocalDateTime bookingDate, double totalPrice, String paymentStatus, String bookingStatus, String customerName, String customerPhone, String customerEmail, String trainName, String routeName, LocalDateTime departureTime, LocalDateTime arrivalTime, List<TicketDTO> tickets) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.tripID = tripID;
        this.roundTripTripID = roundTripTripID;
        this.bookingDate = bookingDate;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.trainName = trainName;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.tickets = tickets;
    }

    public BookingDTO(int bookingID, int userID, int tripID, Integer roundTripTripID, LocalDateTime bookingDate, double totalPrice, String paymentStatus, String bookingStatus, TripDTO trip, String customerName, String customerPhone, String customerEmail, String trainName, String routeName, LocalDateTime departureTime, LocalDateTime arrivalTime, List<TicketDTO> tickets) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.tripID = tripID;
        this.roundTripTripID = roundTripTripID;
        this.bookingDate = bookingDate;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
        this.trip = trip;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.trainName = trainName;
        this.routeName = routeName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.tickets = tickets;
    }

    public TripDTO getTrip() {
        return trip;
    }

    public void setTrip(TripDTO trip) {
        this.trip = trip;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public Integer getRoundTripTripID() {
        return roundTripTripID;
    }

    public void setRoundTripTripID(Integer roundTripTripID) {
        this.roundTripTripID = roundTripTripID;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    
    public String getFormattedBookingDate() {
        if (bookingDate == null) return "";
        return bookingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedDepartureTime() {
        if (departureTime == null) return "";
        return departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedArrivalTime() {
        if (arrivalTime == null) return "";
        return arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // Getters and setters for the list of tickets
    public List<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }
}