package org.example.mobilebackendjava.service;

import com.google.firebase.database.*;
import org.example.mobilebackendjava.model.Payment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class PaymentService {

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("payments");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println(">> Firebase snapshot count: " + snapshot.getChildrenCount());
                    for (DataSnapshot child : snapshot.getChildren()) {
                        try {
                            Payment payment = new Payment();
                            payment.setId(child.getKey());
                            payment.setAmount(child.child("amount").getValue(Integer.class));
                            payment.setPaid(Boolean.TRUE.equals(child.child("paid").getValue(Boolean.class)));
                            payment.setPaymentMethod(child.child("paymentMethod").getValue(String.class));

                            Long timeMillis = child.child("paymentTime").getValue(Long.class);
                            if (timeMillis != null) {
                                payment.setPaymentTime(new Date(timeMillis));
                            }

                            payment.setUserId(child.child("userId").getValue(String.class));

                            payments.add(payment);
                        } catch (Exception e) {
                            System.err.println(">> Error parsing payment: " + e.getMessage());
                        }
                    }
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println(">> Firebase read error: " + error.getMessage());
                    latch.countDown();
                }
            });

            latch.await();
        } catch (Exception e) {
            System.err.println(">> Exception in getAllPayments: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println(">> Total payments fetched: " + payments.size());
        return payments;
    }

    public long getTotalRevenue() {
        return getAllPayments().stream()
                .filter(Payment::isPaid)
                .mapToLong(Payment::getAmount)
                .sum();
    }
}
