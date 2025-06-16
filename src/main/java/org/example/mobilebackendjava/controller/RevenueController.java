package org.example.mobilebackendjava.controller;

import org.example.mobilebackendjava.model.Payment;
import org.example.mobilebackendjava.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class RevenueController {

    private final PaymentService paymentService;

    @Autowired
    public RevenueController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/total")
    public long getTotalRevenue() {
        return paymentService.getTotalRevenue();
    }
}
