package org.example.mobilebackendjava.model;

public class PaymentResponse {
    private boolean success;
    private String transactionId;
    private int amount;

    public PaymentResponse() {
    }

    public PaymentResponse(boolean success, String transactionId, int amount) {
        this.success = success;
        this.transactionId = transactionId;
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
