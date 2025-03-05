/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author dung9
 */
public class Rule {
    private int ruleID;
    private String title;
    private int userID;
    private Date update_date;
    private String content;
    private String img;
    private boolean status;
    private int categoryRuleID;

    public Rule(int ruleID, String title, int userID, Date update_date, String content, String img, boolean status, int categoryRuleID) {
        this.ruleID = ruleID;
        this.title = title;
        this.userID = userID;
        this.update_date = update_date;
        this.content = content;
        this.img = img;
        this.status = status;
        this.categoryRuleID = categoryRuleID;
    }

    public Rule() {
    }

  

    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCategoryRuleID() {
        return categoryRuleID;
    }

    public void setCategoryRuleID(int categoryRuleID) {
        this.categoryRuleID = categoryRuleID;
    }

    public void setUpdated_date(java.sql.Date date) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

  
}
