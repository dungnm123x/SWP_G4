/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private int ticketID;
    private int quantity;
    private double totalPrice;

    // Constructor
    public OrderDetail(int orderDetailID, int orderID, int ticketID, int quantity, double totalPrice) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.ticketID = ticketID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "OrderDetail [orderDetailID=" + orderDetailID + ", orderID=" + orderID + ", ticketID=" + ticketID + ", quantity=" + quantity + ", totalPrice=" + totalPrice + "]";
    }
}

