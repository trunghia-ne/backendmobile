package org.example.mobilebackendjava.model;

import java.util.Date;
    
    public class Payment {
        private String id; // id Firebase
        private int amount;
        private boolean paid;
        private String paymentMethod;
        private Date paymentTime;
        private String userId;

    public Payment() {
    }

    public Payment(String id, int amount, boolean paid, String paymentMethod, Date paymentTime, String userId) {
        this.id = id;
        this.amount = amount;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.userId = userId;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Date getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
