package model;

import java.sql.Timestamp;

public class Feedback {
    private int feedbackId;
    private User user; // Thay đổi từ int userId sang User user
    private String content;
    private int rating;
    private Timestamp feedbackDate;
    private boolean status;

    public Feedback() {
    }

    public Feedback(User user, String content, int rating) {
        this.user = user; // Thay đổi để nhận đối tượng User
        this.content = content;
        this.rating = rating;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(Timestamp feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}