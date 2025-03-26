package com.example.payment_backend.controller;

import com.example.payment_backend.service.PaymentService;
import com.razorpay.RazorpayException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public Map<String, String> createOrder(@RequestBody Map<String, Object> requestData) {
        try {
            int amount = (int) requestData.get("amount"); // Get amount from request
            return paymentService.createOrder(amount);
        } catch (RazorpayException e) {
            return Map.of("error", "Error creating order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public Map<String, String> verifyPayment(@RequestBody Map<String, String> paymentDetails) {
        return paymentService.verifyPayment(paymentDetails);
    }
}
