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
public class CategoryRule {
    private int categoryRuleID;
    private String categoryRuleName;
    private String content;
    private String img;
    private Date update_date;
    private boolean status;

    public CategoryRule(int categoryRuleID, String categoryRuleName, String content, String img, Date update_date, boolean status) {
        this.categoryRuleID = categoryRuleID;
        this.categoryRuleName = categoryRuleName;
        this.content = content;
        this.img = img;
        this.update_date = update_date;
        this.status = status;
    }

    public CategoryRule() {
    }

    public int getCategoryRuleID() {
        return categoryRuleID;
    }

    public void setCategoryRuleID(int categoryRuleID) {
        this.categoryRuleID = categoryRuleID;
    }

    public String getCategoryRuleName() {
        return categoryRuleName;
    }

    public void setCategoryRuleName(String categoryRuleName) {
        this.categoryRuleName = categoryRuleName;
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

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

      
}
