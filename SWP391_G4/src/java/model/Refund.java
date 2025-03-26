/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author dung9
 */
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Refund {
    private int refundID;
    private int userID;
    private String bankAccountID;
    private String bankName;
    private Timestamp refundDate;
    private Timestamp confirmRefundDate;
    private String refundStatus;
    private BigDecimal totalRefund;

    // Constructor
    public Refund() {
    }

    public Refund(int refundID, int userID, String bankAccountID, String bankName, Timestamp refundDate,Timestamp confirmRefundDate, String refundStatus, BigDecimal totalRefund) {
        this.refundID = refundID;
        this.userID = userID;
        this.bankAccountID = bankAccountID;
        this.bankName = bankName;
        this.refundDate = refundDate;
        this.confirmRefundDate=confirmRefundDate;
        this.refundStatus = refundStatus;
        this.totalRefund = totalRefund;
    }

    // Getters and Setters
    public int getRefundID() {
        return refundID;
    }

    public void setRefundID(int refundID) {
        this.refundID = refundID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
    public Timestamp getConfirmRefundDate() {
        return confirmRefundDate;
    }

    public void setConfirmRefundDate(Timestamp confirmRefundDate) {
        this.confirmRefundDate = confirmRefundDate;
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
}