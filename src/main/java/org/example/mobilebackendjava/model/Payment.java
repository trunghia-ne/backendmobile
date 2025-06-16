package org.example.mobilebackendjava.model;
import java.util.Date;

public class Payment {
    private Long id; // Khóa chính tự động tăng
    private int amount;
    private boolean paid;
    private String paymentMethod;
    private Date paymentTime;
    private String userId;

    // Constructors
    public Payment() {}

    public Payment(int amount, boolean paid, String paymentMethod, Date paymentTime,String userId) {
        this.amount = amount;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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