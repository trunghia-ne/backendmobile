package org.example.mobilebackendjava.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.mobilebackendjava.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private Firestore firestore;

    @GetMapping
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("payments").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                try {
                    Payment payment = new Payment();
                    payment.setId(null); // Nếu cần lấy doc.getId(), bạn có thể xử lý riêng

                    payment.setAmount(doc.getLong("amount").intValue());
                    payment.setPaid(doc.getBoolean("paid"));
                    payment.setPaymentMethod(doc.getString("paymentMethod"));

                    // ✅ Convert Firestore Timestamp -> java.util.Date
                    com.google.cloud.Timestamp ts = doc.getTimestamp("paymentTime");
                    if (ts != null) {
                        payment.setPaymentTime(ts.toDate());
                    }

                    payment.setUserId(doc.getString("userId"));

                    payments.add(payment);
                } catch (Exception e) {
                    System.err.println("Error parsing payment " + doc.getId() + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Firestore exception: " + e.getMessage());
        }

        return payments;
    }

    @GetMapping("/total")
    public Long getTotalRevenue() {
        long total = 0;
        for (Payment p : getAllPayments()) {
            if (p.isPaid()) {
                total += p.getAmount();
            }
        }
        return total;
    }
}
